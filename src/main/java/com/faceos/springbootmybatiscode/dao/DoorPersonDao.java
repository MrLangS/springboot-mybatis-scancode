package com.faceos.springbootmybatiscode.dao;

import com.faceos.springbootmybatiscode.domain.DoorPerson;
import org.apache.ibatis.annotations.Param;

public interface DoorPersonDao {

    /**
     * 根据id查找门禁人员
     * @param id
     * @return
     */
    DoorPerson findById(@Param("id") Integer id);
}
