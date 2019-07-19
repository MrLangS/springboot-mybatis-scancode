package com.faceos.springbootmybatiscode.service.impl;

import com.faceos.springbootmybatiscode.dao.ScanerDao;
import com.faceos.springbootmybatiscode.domain.CodeScanner;
import com.faceos.springbootmybatiscode.service.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ScannerServiceImpl
 * 实现类
 *
 * @author 杨舜
 * @date 2019-07-05
 */
@Service
public class ScannerServiceImpl implements ScannerService {
    @Autowired
    private ScanerDao scanerDao;

    @Override
    public Long insert(CodeScanner scanner) {
        return scanerDao.insert(scanner);
    }

    @Override
    public CodeScanner findById(String devId) {
        return scanerDao.findById(devId);
    }

    @Override
    public Long bindScanByClient(Map map) {
        return scanerDao.bindScanByClient(map);
    }

    @Override
    public List<CodeScanner> findByClientId(Integer clientId) {
        return scanerDao.findByClientId(clientId);
    }

    @Override
    public Long bindScanners(List devs, Integer clientId) {
        return scanerDao.bindScanners(devs,clientId);
    }

    @Override
    public Long deleteBind(String dsn, Integer clientId) {
        return scanerDao.deleteBind(dsn,clientId);
    }

    @Override
    public Map getBind(String dsn, Integer clientId) {
        return scanerDao.getBind(dsn,clientId);
    }

    @Override
    public Long deleteBindsByClientId(Integer clientId) {
        return scanerDao.deleteBindsByClientId(clientId);
    }

    @Override
    public Long updateScanName(String name, String dsn) {
        return scanerDao.updateScanName(name,dsn);
    }
}
