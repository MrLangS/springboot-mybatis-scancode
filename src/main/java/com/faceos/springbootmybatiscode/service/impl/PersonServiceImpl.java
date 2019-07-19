package com.faceos.springbootmybatiscode.service.impl;

import com.faceos.springbootmybatiscode.dao.DoorPersonDao;
import com.faceos.springbootmybatiscode.domain.DoorPerson;
import com.faceos.springbootmybatiscode.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PersonServiceImpl
 * 人员服务实现类
 *
 * @author 杨舜
 * @date 2019-07-04
 */
@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private DoorPersonDao personDao;
    @Override
    public DoorPerson findById(Integer id) {
        return personDao.findById(id);
    }
}
