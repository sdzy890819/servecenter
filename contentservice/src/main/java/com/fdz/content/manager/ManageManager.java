package com.fdz.content.manager;

import com.fdz.content.domain.Manager;
import com.fdz.content.mapper.ManagerMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Repository
@Transactional
public class ManageManager {
    @Resource
    private ManagerMapper managerMapper;

    public Manager findManagerByUserName(String userName) {
        return managerMapper.findManagerByUserName(userName);
    }

    public int updateByPrimaryKeySelective(Manager manager) {
        return managerMapper.updateByPrimaryKeySelective(manager);
    }

    public Manager selectManagerByPrimaryKey(Long id) {
        return managerMapper.selectByPrimaryKey(id);
    }

    public int insertSelective(Manager record) {
        return managerMapper.insertSelective(record);
    }
}
