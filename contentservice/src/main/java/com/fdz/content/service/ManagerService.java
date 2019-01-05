package com.fdz.content.service;

import com.fdz.content.domain.Manager;

public interface ManagerService {

    Manager findManagerByUserName(String userName);

    Manager selectManagerByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Manager manager);

    int insertSelective(Manager record);

}
