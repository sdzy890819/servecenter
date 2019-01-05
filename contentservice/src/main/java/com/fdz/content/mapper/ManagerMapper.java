package com.fdz.content.mapper;

import com.fdz.common.constant.Constants;
import com.fdz.content.domain.Manager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ManagerMapper {

    String SQL = "id, create_time, modify_time, create_by, modify_by, remark, is_delete, user_name, \n" +
            "    password, real_name";

    String RESULT_MAP = "BaseResultMap";

    String TABLE = " manager ";

    int deleteByPrimaryKey(Long id);

    int insert(Manager record);

    int insertSelective(Manager record);

    Manager selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Manager record);

    int updateByPrimaryKey(Manager record);


    @Select("select " + SQL + " from " + TABLE + Constants.Sql.NOT_DELETED + " and user_name = #{userName} order by id desc limit 1")
    @ResultMap(RESULT_MAP)
    Manager findManagerByUserName(@Param("userName") String userName);
}