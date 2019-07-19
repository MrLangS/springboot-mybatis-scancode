package com.faceos.springbootmybatiscode.service;

import com.faceos.springbootmybatiscode.domain.DoorPerson;

public interface PersonService {
    DoorPerson findById(Integer id);
}
