package com.fdz.content.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdz.common.aspect.ann.Lock;
import com.fdz.common.enums.InterfaceExecStatus;
import com.fdz.common.enums.InterfaceTypeEnums;
import com.fdz.common.exception.BizException;
import com.fdz.common.utils.Page;
import com.fdz.common.utils.StringUtils;
import com.fdz.content.constants.ContentConstants;
import com.fdz.content.domain.InterfaceExecRecord;
import com.fdz.content.domain.Partner;
import com.fdz.content.rest.ThirdpartyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class JobServiceImpl implements JobService {

    @Resource
    private PartnerService partnerService;

    @Resource
    private ProductService productService;

    @Resource
    private ThirdpartyClient thirdpartyClient;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    @Lock(key = ContentConstants.Redis.JOB_SYNC_LOCK, time = 10)
    public void execRecord() {
        List<InterfaceExecRecord> list = partnerService.queryRecordByStatus(InterfaceExecStatus.WAIT.getStatus());
        if (StringUtils.isNotEmpty(list)) {
            log.info("执行等待区的同步列表数据, 总条数: {}", list.size());
            sync(list);
        } else {
            log.info("等待区没有可执行的列表，尝试修补执行过程中的数据.");
            list = partnerService.queryRecordByStatus(InterfaceExecStatus.PROCESSING.getStatus());
            if (StringUtils.isNotEmpty(list)) {
                log.info("执行修复处理中的同步列表数据, 总条数: {}", list.size());
                sync(list);
            } else {
                log.info("未找到需要修复的数据, Perfect!!!");
            }
        }
        list = partnerService.queryRecordByStatus(InterfaceExecStatus.FAIL.getStatus());
        if (StringUtils.isNotEmpty(list)) {
            log.info("执行二次重试的同步列表数据, 总条数: {}", list.size());
            sync(list);
        } else {
            log.info("未找到需要二次重试的数据, Perfect!!!");
        }
    }

    /**
     * 开始执行
     *
     * @param list
     */
    private void sync(List<InterfaceExecRecord> list) {
        list.forEach(a -> {
            try {
                byte lastStatus = a.getStatus();
                a.setStatus(InterfaceExecStatus.PROCESSING.getStatus());
                partnerService.updateByPrimaryKeySelective(a);
                InterfaceTypeEnums type = InterfaceTypeEnums.get(a.getInterfaceType());
                Object data = null;
                switch (type) {
                    case SYNC_ORDER_STATUS: {

                        break;
                    }
                    case SYNC_ORDER_INFO: {

                        break;
                    }
                    case SYNC_PARTNER_PRODUCT: {
                        log.info("{}, start partner id: {}", type.getText(), a.getPartnerId());
                        Page page = new Page(1, 1000);
                        data = productService.list(page);
                        break;
                    }
                    default: {
                        log.info("当前类型不被识别, 或没有对应的操作, 类型: {}", type);
                        break;
                    }
                }
                if (data != null) {
                    Partner partner = partnerService.selectPartnerByPrimaryKey(a.getPartnerId());
                    if (partner == null) {
                        throw new BizException("合作伙伴已经不存在了，无法同步。请检查是否有问题.");
                    }
                    boolean bool = thirdpartyClient.sync(a.getInterfaceUrl(), partner.getUniqueKey(), data, partner.getPublicKey());
                    log.info("合作机构数据同步结果, {}", bool);
                    if (bool) {
                        a.setStatus(InterfaceExecStatus.SUCCESS.getStatus());
                        partnerService.updateByPrimaryKeySelective(a);
                    } else {
                        if (lastStatus == InterfaceExecStatus.FAIL.getStatus()) {
                            log.info("同步失败, 不再重试.");
                            a.setStatus(InterfaceExecStatus.FAIL_FINISH.getStatus());
                            partnerService.updateByPrimaryKeySelective(a);
                        } else {
                            log.info("同步失败, 下次执行重试.");
                            a.setStatus(InterfaceExecStatus.FAIL.getStatus());
                            partnerService.updateByPrimaryKeySelective(a);
                        }
                    }
                }
            } catch (BizException e) {
                log.error("业务错误, 主动抛出错误. {}", e);
            } catch (Exception e) {
                log.error("未知错误， 错误为: {}", e);
            }
        });

    }
}
