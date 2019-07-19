package com.faceos.springbootmybatiscode.controller;

import com.faceos.springbootmybatiscode.domain.DoorPerson;
import com.faceos.springbootmybatiscode.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * PersonController
 *
 * @author lang
 * @date 2019-07-04
 */
@RestController
public class PersonController {
    @Autowired
    PersonService personService;

    public static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @RequestMapping(value = "api/person", method = RequestMethod.GET)
    public DoorPerson findOnePerson(@RequestParam(value = "id") Integer id){
        DoorPerson person = personService.findById(id);
        if (person != null){
            logger.info("已成功找到");
        } else {
            logger.info("未找到");
        }
        return person;
    }

}
