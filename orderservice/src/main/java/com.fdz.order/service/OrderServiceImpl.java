package com.fdz.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.constant.Constants;
import com.fdz.common.enums.*;
import com.fdz.common.exception.BizException;
import com.fdz.common.redis.RedisDataManager;
import com.fdz.common.utils.IDGenerator;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.order.config.ApplicationProperties;
import com.fdz.order.convert.DtoConvert;
import com.fdz.order.domain.*;
import com.fdz.order.dto.*;
import com.fdz.order.manager.OrderManager;
import com.fdz.order.service.content.ContentService;
import com.fdz.order.service.content.dto.PartnerRestResult;
import com.fdz.order.service.content.dto.RecordDto;
import com.fdz.order.service.content.dto.ThirdpartyProductDto;
import com.fdz.order.vo.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderManager orderManager;

    @Resource
    private IDGenerator orderIDGenerator;

    @Resource
    private ContentService contentService;

    @Resource
    private DtoConvert dtoConvert;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IDGenerator paymentIDGenerator;

    @Resource
    private ApplicationProperties applicationProperties;

    private static final String XLS = "xls";

    private static final String XLSX = "xlsx";

    private static final int SIZE = 50;

    @Resource
    private RedisDataManager redisDataManager;

    @Override
    public CashierResult shopping(CashierDto cashierDto, Long partnerId) {
        Orders oldOrders = orderManager.findOrdersByPartnerSn(cashierDto.getSn());
        if (oldOrders != null) {
            CashierResult cashierResult = new CashierResult();
            cashierResult.setOrderSn(oldOrders.getOrderSn());
            cashierResult.setSn(cashierDto.getSn());
            cashierResult.setGoodsDtos(cashierDto.getGoodsList());
            return cashierResult;
        } else {
            List<GoodsDto> goodsDtoList = cashierDto.getGoodsList();
            if (StringUtils.isEmpty(goodsDtoList)) {
                throw new BizException("产品不可以为空!");
            }
            String orderSn = String.valueOf(orderIDGenerator.getId());
            List<Long> ppIds = new ArrayList<>();
            goodsDtoList.forEach(a -> ppIds.add(a.getProductId()));
            Map<Long, ThirdpartyProductDto> tpResultMap = findTpResultMap(ppIds);
            BigDecimal amount = BigDecimal.ZERO;
            BigDecimal platformAmount = BigDecimal.ZERO;
            BigDecimal costAmount = BigDecimal.ZERO;
            BigDecimal infoAmount = BigDecimal.ZERO;
            List<OrdersProduct> ordersProducts = new ArrayList<>();
            for (GoodsDto a : goodsDtoList) {
                ThirdpartyProductDto dto = tpResultMap.get(a.getProductId());
                if (!a.getPlatformPrice().equals(dto.getPlatformPrice())) {
                    log.error("商家传递的平台售价跟自有平台售价不一致.");
                    a.setPlatformPrice(dto.getPlatformPrice());
                }
                amount = amount.add(dto.getSalePrice().multiply(new BigDecimal(a.getNum())));
                platformAmount = platformAmount.add(dto.getPlatformPrice().multiply(new BigDecimal(a.getNum())));
                costAmount = costAmount.add(dto.getPrimeCosts().multiply(new BigDecimal(a.getNum())));
                infoAmount = infoAmount.add(dto.getServiceFee().multiply(new BigDecimal(a.getNum())));
                OrdersProduct ordersProduct = new OrdersProduct();
                ordersProduct.setOrderSn(orderSn);
                ordersProduct.setPartnerSn(cashierDto.getSn());
                ordersProduct.setPartnerId(partnerId);
                ordersProduct.setPartnerProductId(a.getProductId());
                ordersProduct.setProductName(dto.getProductName());
                ordersProduct.setProductNum(a.getNum());
                ordersProduct.setProductTypeNo(dto.getProductTypeNo());
                ordersProduct.setProductTypeName(dto.getProductTypeName());
                ordersProduct.setProductDescription(dto.getProductDescription());
                ordersProduct.setProductCoverImage(dto.getProductCoverImage());
                ordersProduct.setPrimeCosts(dto.getPrimeCosts());
                ordersProduct.setSalePrice(dto.getSalePrice());
                ordersProduct.setProductModel(dto.getProductModel());
                ordersProduct.setProductId(dto.getProductId());
                ordersProduct.setPlatformPrice(dto.getPlatformPrice());
                ordersProduct.setProductSalePrice(dto.getProductSalePrice());
                ordersProducts.add(ordersProduct);
            }
            ReceivingAddressDto receivingAddressDto = cashierDto.getReceivingAddress();
            //--物流信息
            OrdersLogistics ordersLogistics = new OrdersLogistics();
            ordersLogistics.setOrderSn(orderSn);
            ordersLogistics.setPartnerSn(cashierDto.getSn());
            ordersLogistics.setPartnerId(partnerId);
            ordersLogistics.setReceiver(receivingAddressDto.getName());
            ordersLogistics.setReceiverAddress(receivingAddressDto.getAddress());
            ordersLogistics.setReceiverProvince(receivingAddressDto.getProvince());
            ordersLogistics.setReceiverCity(receivingAddressDto.getCity());
            ordersLogistics.setReceiverArea(receivingAddressDto.getArea());
            ordersLogistics.setReceiverMobile(receivingAddressDto.getMobile());
            ordersLogistics.setDeliveryStatus(DeliveryStatusEnums.DONT_DELIVERY.getStatus());
            ordersLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DONT_DELIVERY.getStatus());
            PartnerRestResult partnerRestResult = contentService.findPartnerByIdResultMap(Lists.newArrayList(partnerId)).get(partnerId);
            //--拼接order
            Orders orders = new Orders();
            orders.setOrderSn(orderSn);
            orders.setPlatformAmount(platformAmount);
            orders.setCostAmount(costAmount);
            orders.setPartnerSn(cashierDto.getSn());
            orders.setPartnerId(partnerId);
            orders.setAmount(amount);
            orders.setInfoAmount(infoAmount);
            orders.setBuyTime(new Date());
            orders.setStatus(OrdersStatus.WAIT_PAY.getStatus());
            orders.setOrderStatus(OrdersFinishStatus.PROCESSING.getStatus());
            List<PaymentRecord> paymentRecords = pay(partnerRestResult, orders);
            orderManager.insert(orders, ordersProducts, ordersLogistics, paymentRecords);
            CashierResult cashierResult = new CashierResult();
            cashierResult.setOrderSn(orderSn);
            cashierResult.setSn(cashierDto.getSn());
            cashierResult.setGoodsDtos(goodsDtoList);
            sendOrdersExecRecord(orderSn, partnerId);
            return cashierResult;
        }
    }

    /**
     * 支付
     *
     * @param partner
     * @param orders
     * @return
     */
    public List<PaymentRecord> pay(PartnerRestResult partner, Orders orders) {
        List<PaymentRecord> list = new ArrayList<>();
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setSn(orders.getPartnerSn());
        paymentRecord.setOrderSn(orders.getOrderSn());
        paymentRecord.setPaymentType(PaymentTypeEnums.PAY.getType());
        paymentRecord.setAmount(orders.getAmount());
        paymentRecord.setPayRoute(PayRouteEnums.SELF.getRoute());
        paymentRecord.setFrozen(true);
        paymentRecord.setPartnerId(orders.getPartnerId());
        list.add(paymentRecord);
        PaymentRecord paymentRecord2 = new PaymentRecord();
        paymentRecord2.setSn(orders.getPartnerSn());
        paymentRecord2.setOrderSn(orders.getOrderSn());
        paymentRecord2.setPaymentType(PaymentTypeEnums.INFO.getType());
        paymentRecord2.setAmount(orders.getInfoAmount());
        paymentRecord2.setPayRoute(PayRouteEnums.SELF.getRoute());
        paymentRecord2.setFrozen(true);
        paymentRecord2.setPartnerId(orders.getPartnerId());
        list.add(paymentRecord2);
        return list;
    }


    @Override
    public void delivery(String sn) {
        Orders orders = findOrdersByPartnerSn(sn);
        if (orders == null) {
            throw new BizException("查找不到相关订单信息");
        }
        Orders updateO = new Orders(orders.getId());
        updateO.setStatus(OrdersStatus.CONFIRM_SEND.getStatus());
        updateO.setDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateO.setConfirmTime(new Date());
        OrdersLogistics ordersLogistics = findOrdersLogisticsByPartnerSn(sn);
        OrdersLogistics updateOl = new OrdersLogistics(ordersLogistics.getId());
        updateOl.setDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        update(updateO, updateOl);
    }

    public void update(Orders update, OrdersLogistics update2) {
        orderManager.update(update, update2);
    }

    @Override
    public Orders findOrdersByPartnerSn(String partnerSn) {
        return orderManager.findOrdersByPartnerSn(partnerSn);
    }

    @Override
    public Orders findOrdersByOrderSn(String orderSn) {
        return orderManager.findOrdersByOrderSn(orderSn);
    }

    @Override
    public OrdersLogistics findOrdersLogisticsByPartnerSn(String partnerSn) {
        return orderManager.findOrdersLogisticsByPartnerSn(partnerSn);
    }

    @Override
    public OrdersLogistics findOrdersLogisticsByOrderSn(String orderSn) {
        return orderManager.findOrdersLogisticsByOrderSn(orderSn);
    }

    public Map<Long, ThirdpartyProductDto> findTpResultMap(List<Long> ppIds) {
        List<ThirdpartyProductDto> tpList = contentService.detailList(ppIds);
        if (tpList == null) {
            throw new BizException("产品不可以为空!");
        }
        Map<Long, ThirdpartyProductDto> result = new HashMap<>();
        tpList.forEach(a -> result.put(a.getId(), a));
        return result;
    }

    @Override
    public List<OrdersResult> searchOrders(SearchOrdersDto dto, Page page) {
        Integer count = orderManager.searchOrdersCount(dto);
        page.setCount(count);
        if (page.isQuery()) {
            List<OrdersResult> results = new ArrayList<>();
            List<OrdersAndLogistics> list = orderManager.searchOrders(dto, page);
            if (StringUtils.isNotEmpty(list)) {
                for (int i = 0; i < list.size(); i++) {
                    OrdersAndLogistics ordersAndLogistics = list.get(i);
                    OrdersResult ordersResult = dtoConvert.convertOrdersResult(ordersAndLogistics);
                    OrdersLogisticsResult ordersLogisticsResult = dtoConvert.convertOrdersLogisticsResult(ordersAndLogistics);
                    ordersResult.setOrdersLogisticsResult(ordersLogisticsResult);
                    results.add(ordersResult);
                }
            }
            return results;
        }
        return null;
    }

    @Override
    public OrdersResult findOrdersResult(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        List<OrdersProduct> ordersProducts = orderManager.findOrdersProductsByOrderSn(orderSn);
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        OrdersResult ordersResult = dtoConvert.convertOrdersResult(orders);
        List<OrdersProductResult> ordersProductResults = dtoConvert.convertOrdersProductResult(ordersProducts);
        ordersResult.setOrdersProductResults(ordersProductResults);
        OrdersLogisticsResult ordersLogisticsResult = dtoConvert.convertOrdersLogisticsResult(ordersLogistics);
        ordersResult.setOrdersLogisticsResult(ordersLogisticsResult);
        return ordersResult;
    }

    @Override
    public void businessDelivery(LogisticsDto dto) {
        Orders orders = orderManager.findOrdersByOrderSn(dto.getOrderSn());
        if (orders.getOrderStatus() == OrdersStatus.RECEIVED.getStatus()) {
            throw new BizException("订单已经签收，不可更改物流信息");
        }
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(dto.getOrderSn());
        Orders updateOrder = new Orders(orders.getId());
        updateOrder.setStatus(OrdersStatus.DELIVERED.getStatus());
        updateOrder.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateOrder.setBusinessDeliveryTime(new Date());
        OrdersLogistics updateLogistics = new OrdersLogistics(ordersLogistics.getId());
        updateLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateLogistics.setLogistics(dto.getLogistics());
        updateLogistics.setLogisticsSn(dto.getLogisticsSn());
        updateLogistics.setLogisticsStatus(dto.getLogisticsStatus());
        ordersLogistics.setLogisticsAmount(dto.getLogisticsAmount());
        update(updateOrder, updateLogistics);
        sendOrdersExecRecord(orders.getOrderSn(), orders.getPartnerId());
        sendStatusExecRecord(orders.getOrderSn());
    }

    @Override
    public void businessDelivery(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        if (orders == null) {
            throw new BizException("订单不存在.");
        }
        if (orders.getOrderStatus() == OrdersStatus.RECEIVED.getStatus()) {
            throw new BizException("订单已经签收，不可更改物流信息");
        }
        Orders updateOrder = new Orders(orders.getId());
        updateOrder.setBusinessDeliveryTime(new Date());
        updateOrder.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        updateOrder.setStatus(OrdersStatus.DELIVERED.getStatus());
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        OrdersLogistics updateOrdersLogistics = new OrdersLogistics(ordersLogistics.getId());
        updateOrdersLogistics.setBusinessDeliveryStatus(DeliveryStatusEnums.DELIVERY.getStatus());
        update(updateOrder, updateOrdersLogistics);
        sendStatusExecRecord(orderSn);
    }

    @Override
    public void receive(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        Orders updateOrder = new Orders(orders.getId());
        updateOrder.setEndTime(new Date());
        updateOrder.setStatus(OrdersStatus.RECEIVED.getStatus());
        updateOrder.setOrderStatus(OrdersFinishStatus.FINISHED.getStatus());
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        OrdersLogistics updateOrdersLogistics = new OrdersLogistics(ordersLogistics.getId());
        updateOrdersLogistics.setLogisticsStatus(OrdersStatus.RECEIVED.getStatusText());
        update(updateOrder, updateOrdersLogistics);
        sendStatusExecRecord(orderSn);
    }

    /**
     * 保存订单状态推送
     *
     * @param orderSn
     */
    public void sendStatusExecRecord(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        RecordDto recordDto = new RecordDto();
        OrderStatusPushVo vo = new OrderStatusPushVo();
        vo.setOrderSn(orderSn);
        vo.setSn(orders.getPartnerSn());
        vo.setStatus(orders.getStatus());
        try {
            recordDto.setData(objectMapper.writeValueAsString(vo));
        } catch (JsonProcessingException e) {
            throw new BizException("Json转字符串异常", e);
        }
        recordDto.setInterfaceType(InterfaceTypeEnums.SYNC_ORDER_STATUS.getType());
        recordDto.setPartnerId(orders.getPartnerId());
        contentService.create(recordDto);
    }

    public OrderStatusPushVo findOrderStatusPushVo(String partnerSn) {
        Orders orders = orderManager.findOrdersByPartnerSn(partnerSn);
        OrderStatusPushVo vo = new OrderStatusPushVo();
        vo.setOrderSn(orders.getOrderSn());
        vo.setSn(orders.getPartnerSn());
        vo.setStatus(orders.getStatus());
        return vo;
    }

    /**
     * 发送订单推送
     *
     * @param orderSn
     * @param partnerId
     */
    public void sendOrdersExecRecord(String orderSn, Long partnerId) {
        OrderPushVo orderPushVo = findOrderPushVo(orderSn);
        RecordDto recordDto = new RecordDto();
        try {
            recordDto.setData(objectMapper.writeValueAsString(orderPushVo));
        } catch (JsonProcessingException e) {
            throw new BizException("Json转字符串异常", e);
        }
        recordDto.setInterfaceType(InterfaceTypeEnums.SYNC_ORDER_INFO.getType());
        recordDto.setPartnerId(partnerId);
        contentService.create(recordDto);
    }

    /**
     * 拉取订单信息
     *
     * @param orderSn
     * @return
     */
    public OrderPushVo findOrderPushVo(String orderSn) {
        Orders orders = orderManager.findOrdersByOrderSn(orderSn);
        OrdersLogistics ordersLogistics = orderManager.findOrdersLogisticsByOrderSn(orderSn);
        List<OrdersProduct> ordersProducts = orderManager.findOrdersProductsByOrderSn(orderSn);

        OrderPushVo orderPushVo = dtoConvert.convert(orders);
        orderPushVo.setSn(orders.getPartnerSn());
        List<OrderProductPushVo> orderProductPushVos = dtoConvert.convert(ordersProducts);
        OrdersLogisticsPushVo ordersLogisticsPushVo = dtoConvert.convert(ordersLogistics);
        orderPushVo.setProducts(orderProductPushVos);
        orderPushVo.setLogistics(ordersLogisticsPushVo);
        return orderPushVo;
    }

    @Override
    public List<DeliveryInfo> statistics(Integer days) {
        Calendar calendar = Calendar.getInstance();
        Date end = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date start = calendar.getTime();
        List<OrderStatistics> deliveryList = orderManager.findOrderStatistics(start, end);
        if (deliveryList != null) {
            List<Long> partnerIds = new ArrayList<>();
            deliveryList.forEach(a -> {
                if (!partnerIds.contains(a.getPartnerId())) {
                    partnerIds.add(a.getPartnerId());
                }
            });
            Map<Long, PartnerRestResult> partnerMap = StringUtils.isNotEmpty(partnerIds) ? contentService.findPartnerByIdResultMap(partnerIds) : new HashMap<>(0);
            Map<String, DeliveryInfo> resultMap = new HashMap<>();
            deliveryList.forEach(a -> {
                String tmp = a.getOrderDate() + a.getPartnerId();
                DeliveryInfo deliveryInfo = resultMap.get(tmp);
                if (resultMap.get(tmp) == null) {
                    deliveryInfo = new DeliveryInfo();
                    resultMap.put(tmp, deliveryInfo);

                }
                deliveryInfo.setDate(a.getOrderDate());
                deliveryInfo.setPartnerId(a.getPartnerId());
                PartnerRestResult partnerRestResult = partnerMap.get(a.getPartnerId());
                deliveryInfo.setPartnerName(partnerRestResult != null ? partnerRestResult.getName() : "");
                DeliveryStatusEnums deliveryStatusEnums = DeliveryStatusEnums.get(a.getDeliveryStatus());
                switch (deliveryStatusEnums) {
                    case DELIVERY: {
                        deliveryInfo.setDeliveryAndAmount(a.getNum() + "/" + a.getAmount());
                        break;
                    }
                    case DONT_DELIVERY: {
                        deliveryInfo.setWaitDeliveryAndAmount(a.getNum() + "/" + a.getAmount());
                        break;
                    }
                    default:
                        break;
                }
            });
            List<DeliveryInfo> result = new ArrayList<>(resultMap.values());
            return result;
        }
        return null;
    }

    @Override
    public List<PaymentRecord> searchPaymentRecord(PaymentRecordSearchDto dto, Page page) {
        Integer count = orderManager.searchPaymentRecordCount(dto);
        page.setCount(count);
        if (page.isQuery()) {
            return orderManager.searchPaymentRecord(dto, page);
        }
        return null;
    }

    @Override
    public int insertSelective(PaymentRecord record) {
        return orderManager.insertSelective(record);
    }

    @Override
    public PaymentRecord selectPaymentRecordByPrimaryKey(Long id) {
        return orderManager.selectPaymentRecordByPrimaryKey(id);
    }

    @Override
    public int updateByPrimaryKeySelective(PaymentRecord record) {
        return orderManager.updateByPrimaryKeySelective(record);
    }

    public int insertSelective(Account record) {
        return orderManager.insertSelective(record);
    }

    public Account selectAccountByPrimaryKey(Long id) {
        return orderManager.selectAccountByPrimaryKey(id);
    }

    public int updateByPrimaryKeySelective(Account record) {
        return orderManager.updateByPrimaryKeySelective(record);
    }

    public Account findAccountByPartnerId(Long partnerId) {
        return orderManager.findAccountByPartnerId(partnerId);
    }


    @Override
    @Lock(key = "PAYMENT_RECORD_#{paymentRecord.partnerId}")
    public void addRecord(PaymentRecord paymentRecord) {
        paymentRecord.setPayRoute(PayRouteEnums.SELF.getRoute());
        paymentRecord.setFrozen(false);
        orderManager.calc(paymentRecord);
    }

    @Override
    public PaymentRecord findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(Long partnerId, Byte paymentType, String orderSn, Boolean frozen) {
        return orderManager.findRecordByPartnerIdAndTypeAndOrderSnAndFrozen(partnerId, paymentType, orderSn, frozen);
    }

    @Override
    public List<EmsLogistics> export(SearchOrdersDto dto) {
        List<OrdersAndLogistics> list = orderManager.searchAllOrders(dto);
        if (StringUtils.isNotEmpty(list)) {
            Map<String, StringBuilder> map = getRemark(list);
            List<EmsLogistics> data = new ArrayList<>();
            for (OrdersAndLogistics a : list) {
                EmsLogistics emsLogistics = new EmsLogistics();
                emsLogistics.setAddress(a.getReceiverProvince() + " " + a.getReceiverCity() + " " + a.getReceiverArea() + " " + a.getReceiverAddress());
                emsLogistics.setDh(a.getOrderSn());
                emsLogistics.setMobile(a.getReceiverMobile());
                emsLogistics.setName(a.getLogistics());
                StringBuilder tmp = map.get(a.getOrderSn());
                emsLogistics.setRemark(tmp != null ? tmp.toString() : "");
                data.add(emsLogistics);
            }
            return data;
        }
        return null;
    }

    private Map<String, StringBuilder> getRemark(List<OrdersAndLogistics> list) {
        List<String> orderSns = new ArrayList<>();
        list.forEach(a -> orderSns.add(a.getOrderSn()));
        List<OrdersProduct> ordersProducts = orderManager.findOrdersProductByOrderSns(orderSns);
        Map<String, StringBuilder> remarkMap = new HashMap<>();
        if (StringUtils.isNotEmpty(ordersProducts)) {
            ordersProducts.forEach(a -> {
                StringBuilder tmp = remarkMap.get(a.getOrderSn());
                if (tmp == null) {
                    tmp = new StringBuilder();
                }
                tmp.append(" | ");
                tmp.append(a.getProductId());
                tmp.append("*");
                tmp.append(a.getProductNum());
            });
        }
        return remarkMap;
    }

    @Override
    public void importEms(MultipartFile file) throws BizException {
        checkFile(file);
        Workbook workbook = getWorkBook(file);
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new BizException("找不到excel sheet");
        }
        int end = sheet.getLastRowNum();
        int start = sheet.getFirstRowNum();
        List<OrdersLogisticsInfo> list = new ArrayList<>();
        for (int rowNum = start + 1; rowNum <= end; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            String yjh = getCellValue(row.getCell(0));
            String dh = getCellValue(row.getCell(1));
            String name = getCellValue(row.getCell(10));
            String mobile = getCellValue(row.getCell(12));
            OrdersLogisticsInfo emsInfo = new OrdersLogisticsInfo();
            emsInfo.setOrderSn(dh);
            emsInfo.setLogistics(LogisticsEnum.EMS.getLogistics());
            emsInfo.setLogisticsSn(yjh);
            emsInfo.setLogisticsStatus(OrdersStatus.DELIVERED.getStatusText());
            list.add(emsInfo);
            if (list.size() >= SIZE) {
                orderManager.updateLogisticsInfo(list);
                list.clear();
            }
        }
        if (list.size() > 0) {
            orderManager.updateLogisticsInfo(list);
            list.clear();
        }
        log.info("导入完成.");
    }

    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(XLS)) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(XLSX)) {
                //2007
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return workbook;
    }

    private static Workbook getWorkBook(InputStream inputStream, String fileName) {
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith(XLS)) {
                //2003
                workbook = new HSSFWorkbook(inputStream);
            } else if (fileName.endsWith(XLSX)) {
                //2007
                workbook = new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }
        //判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: //数字
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: //空值
                cellValue = "";
                break;
            case Cell.CELL_TYPE_ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    private static void checkFile(MultipartFile file) throws BizException {
        //判断文件是否存在
        if (null == file) {
            log.error("文件不存在！");
            throw new BizException("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            log.error(fileName + "不是excel文件");
            throw new BizException(fileName + "不是excel文件");
        }
    }

    @Override
    public void emsStyle(HttpServletResponse response, List<EmsLogistics> data) {
        InputStream inputStream = this.getClass().getResourceAsStream("/export.xls");
        OutputStream outputStream = null;
        try {
            Workbook workbook = getWorkBook(inputStream, "export.xls");
            outputStream = response.getOutputStream();
            Sheet sheet = workbook.getSheetAt(0);
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    EmsLogistics tmp = data.get(i);
                    Row row = sheet.createRow(i + 1);
                    Cell dhCell = row.createCell(1);
                    dhCell.setCellValue(tmp.getDh());
                    Cell fromNameCell = row.createCell(3);
                    fromNameCell.setCellValue(applicationProperties.getSenderInfo().getName());
                    Cell fromMobileCell = row.createCell(4);
                    fromMobileCell.setCellValue(applicationProperties.getSenderInfo().getMobile());
                    Cell fromAddressCell = row.createCell(6);
                    fromAddressCell.setCellValue(applicationProperties.getSenderInfo().getAddress());
                    // 收件人信息 12 -
                    Cell nameCell = row.createCell(12);
                    nameCell.setCellValue(tmp.getName());
                    Cell mobileCell = row.createCell(13);
                    mobileCell.setCellValue(tmp.getMobile());
                    Cell addressCell = row.createCell(15);
                    addressCell.setCellValue(tmp.getAddress());
                }
            }
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("执行export失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("export.xls 流关闭错误 {}", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    log.error("导出输出流关闭错误 {}", e);
                }
            }
        }
    }

    /**
     * // 保留num的位数
     * //　 0 代表前面补充0
     * // num 代表长度为4
     * // d 代表参数为正数型
     *
     * @param code
     * @param num
     * @return
     */
    private String autoGenericCode(int code, int num) {
        String result = "";

        result = String.format("%0" + num + "d", code + 1);

        return result;
    }

    @Override
    @Async
    public void orderReceive() {
        final int days = 5;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            List<String> list = orderManager.findOrdersByTimeAndDelivered(calendar.getTime());
            for (int i = 0; i < list.size(); i++) {
                receive(list.get(i));
            }
            redisDataManager.delete(Constants.RedisKey.LOCK_PREFIX + "FINISH_ORDER");
            log.info("订单结束程序，正常结束");
        } catch (Exception e) {
            log.error("结束订单报错, {}", e);
        }
    }
}
