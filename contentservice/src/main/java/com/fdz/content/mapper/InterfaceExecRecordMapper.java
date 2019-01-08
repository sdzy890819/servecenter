package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.InterfaceExecRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InterfaceExecRecordMapper {

    String SQL = " id, create_time, modify_time, create_by, modify_by, remark, is_delete, partner_id, \n" +
            "    interface_url, interface_type, status, exec_time, data ";

    String TABLE = " interface_exec_record ";

    String RESULT_MAP = "BaseResultMap";

    String SELECT = "select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED;


    int deleteByPrimaryKey(Long id);

    int insert(InterfaceExecRecord record);

    int insertSelective(InterfaceExecRecord record);

    InterfaceExecRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InterfaceExecRecord record);

    int updateByPrimaryKey(InterfaceExecRecord record);

    int insertInterfaceExecRecordList(List<InterfaceExecRecord> list);

    List<InterfaceExecRecord> findRecordPartnerIdAndTypeAndStatus(@Param("list") List<Long> partnerId, @Param("type") byte type, @Param("status") byte status);

    @Select(SELECT + " and status = #{status} " + Constants.Sql.DEFAULT_ORDER_ASC)
    @ResultMap(RESULT_MAP)
    List<InterfaceExecRecord> queryRecordByStatus(@Param("status") byte status);
}