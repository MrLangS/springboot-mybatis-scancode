package com.faceos.springbootmybatiscode.dao;

import com.faceos.springbootmybatiscode.domain.CodeScanner;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ScanerDao {
    /**
     * 添加设备
     * @param scanner
     * @return
     */
    Long insert(CodeScanner scanner);

    /**
     * 根据设备id查找
     * @param devId
     * @return
     */
    CodeScanner findById(@Param("devId") String devId);

    /**
     * 根据设备dsn更改设备名称
     * @param name
     * @param dsn
     * @return
     */
    Long updateScanName(@Param("name") String name, @Param("dsn") String dsn);

    /**
     * 单位绑定设备
     *
     * @param map 参数map
     * @return
     */
    Long bindScanByClient(Map map);

    /**
     * 根据设备id和单位id删除绑定关系
     *
     * @param dsn
     * @param clientId
     * @return
     */
    Long deleteBind(@Param("dsn") String dsn,@Param("clientId") Integer clientId);

    /**
     * 根据dsn和clientId获取绑定关系
     * @param dsn
     * @param clientId
     * @return
     */
    Map getBind(@Param("dsn") String dsn,@Param("clientId") Integer clientId);

    /**
     * 根据单位id删除绑定关系
     *
     * @param clientId
     * @return
     */
    Long deleteBindsByClientId(Integer clientId);

    /**
     * 单位绑定批量设备
     * @param devs
     * @param clientId
     * @return
     */
    Long bindScanners(@Param("devs") List devs,@Param("clientId")Integer clientId);

    /**
     * 根据单位id查找设备
     *
     * @param clientId 单位id
     * @return
     */
    List<CodeScanner> findByClientId(@Param("clientId") Integer clientId);

}
