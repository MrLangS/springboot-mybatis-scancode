package com.faceos.springbootmybatiscode.controller;

import com.faceos.springbootmybatiscode.domain.CodeScanner;
import com.faceos.springbootmybatiscode.service.ScannerService;
import com.faceos.springbootmybatiscode.utils.Result;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScannerController
 * 扫码机控制类
 *
 * @author lang
 * @date 2019-07-08
 */
@SuppressWarnings("unchecked")
@RestController
public class ScannerController {
    private static final Logger logger = LoggerFactory.getLogger(ScannerController.class);

    @Autowired
    ScannerService scannerService;
    @Value("${socket.server.ip}")
    private String ip;
    @Value("${socket.server.port}")
    private int port;

    @GetMapping(value = "smart/get_ip")
    public Result getIpAndPort(@RequestParam(value = "dsn") String dsn) {
        Map data = new HashMap<String,Object>(2);
        data.put("ip","");
        data.put("port",0);
        if (scannerService.findById(dsn) != null) {
            data.put("ip",ip);
            data.put("port",port);
            return Result.success(data, 0, "ok");
        }
        return Result.error(data,405);
    }

    @RequestMapping(value = "api/scanner", method = RequestMethod.POST)
    public void createScanner(@RequestBody CodeScanner scanner) {
        if (scannerService.findById(scanner.getDeviceId()) == null) {
            scannerService.insert(scanner);
        }
    }

    @RequestMapping(value = "api/scanner", method = RequestMethod.PUT)
    public Result updateScanner(@RequestParam(value = "name") String name, @RequestParam(value = "dsn") String dsn) {
        Long back = scannerService.updateScanName(name,dsn);
        return back == 1 ? Result.success() : Result.error(500);
    }

    @RequestMapping(value = "api/scanner", method = RequestMethod.GET)
    public CodeScanner findById(@RequestParam(value = "devId") String devId) {
        return scannerService.findById(devId);
    }

    @RequestMapping(value = "api/binding",method = RequestMethod.POST)
    public void bindScanner(@RequestBody Map req) {
        scannerService.bindScanByClient(req);
    }

    @RequestMapping(value = "api/binding/{dsn}/{clientId}",method = RequestMethod.GET)
    public Map getBind(@PathVariable("dsn") String dsn,@PathVariable("clientId") Integer clientId) {
        return scannerService.getBind(dsn,clientId);
    }

    @RequestMapping(value = "api/bindings",method = RequestMethod.POST)
    public Result bindScanners(@RequestParam("dsn") List dsn,@RequestParam("clientId") Integer clientId ) {
        scannerService.deleteBindsByClientId(clientId);
        Long back = -1L;
        if (dsn.size() > 0){
            back = scannerService.bindScanners(dsn,clientId);
            logger.info("修改返回结果[{}]",back);
        }

        return back != 0 ? Result.success() : Result.error(500);
    }

    /*@RequestMapping(value = "api/bindings",method = RequestMethod.POST)
    public Result bindScanners(@RequestBody Map req) {
        List devs = (List<String>)req.get("dsn");
        Integer clientId = (Integer)req.get("clientId");
        scannerService.deleteBindsByClientId(clientId);
        Long back = -1L;
        if (devs.size() > 0){
            back = scannerService.bindScanners(devs,clientId);
            logger.info("修改返回结果[{}]",back);
        }

        return back != 0 ? Result.success() : Result.error(500);
    }*/

    @RequestMapping(value = "api/scanners", method = RequestMethod.GET)
    public List<CodeScanner> findByClientId(@RequestParam("clientId") Integer clientId) {
        return scannerService.findByClientId(clientId);
    }

    @RequestMapping(value = "api/binding",method = RequestMethod.DELETE)
    public Result deleteBind(@Param("dsn") String dsn, @Param("clientId") Integer clientId) {
        Long back = scannerService.deleteBind(dsn,clientId);
        return back == 1 ? Result.success() : Result.error(500);
    }

    @RequestMapping(value = "api/bindings/{clientId}",method = RequestMethod.DELETE)
    public void deleteBindByClientId(@PathVariable("clientId") Integer clientId) {
        scannerService.deleteBindsByClientId(clientId);
    }
}
