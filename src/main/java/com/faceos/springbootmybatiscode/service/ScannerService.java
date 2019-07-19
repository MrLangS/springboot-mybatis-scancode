package com.faceos.springbootmybatiscode.service;

import com.faceos.springbootmybatiscode.domain.CodeScanner;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ScannerService {
    /**
     * 添加设备
     * @param scanner 设备实体类
     *
     * @return
     */
    Long insert(CodeScanner scanner);

    /**
     * 根据设备id查找
     *
     * @param id 设备id
     * @return
     */
    CodeScanner findById(@Param("id") String id);

    /**
     * 根据设备dsn更改设备名称
     * @param name
     * @param dsn
     * @return
     */
    Long updateScanName(String name, String dsn);

    /**
     * 绑定设备
     *
     * @param map 参数map
     * @return
     */
    Long bindScanByClient(Map map);

    /**
     * 单位绑定批量设备
     * @param devs
     * @param clientId
     * @return
     */
    Long bindScanners(List devs,Integer clientId);

    /**
     * 根据dsn和clientId获取绑定关系
     * @param dsn
     * @param clientId
     * @return
     */
    Map getBind(String dsn,Integer clientId);

    /**
     * 根据单位id查找设备
     *
     * @param clientId 单位id
     * @return
     */
    List<CodeScanner> findByClientId(Integer clientId);

    /**
     * 根据设备id和单位id删除绑定关系
     *
     * @param dsn
     * @param clientId
     * @return
     */
    Long deleteBind(String dsn,Integer clientId);

    /**
     * 根据单位id删除绑定关系
     *
     * @param clientId
     * @return
     */
    Long deleteBindsByClientId(Integer clientId);
}
