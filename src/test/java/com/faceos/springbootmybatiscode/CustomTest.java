package com.faceos.springbootmybatiscode;

import com.alibaba.fastjson.JSONObject;
import com.faceos.springbootmybatiscode.autostart.ServerThread;
import com.faceos.springbootmybatiscode.domain.HttpClientResult;
import com.faceos.springbootmybatiscode.service.PersonService;
import com.faceos.springbootmybatiscode.service.ScannerService;
import com.faceos.springbootmybatiscode.service.impl.PersonServiceImpl;
import com.faceos.springbootmybatiscode.service.impl.ScannerServiceImpl;
import com.faceos.springbootmybatiscode.utils.ApplicationContextProvider;
import com.faceos.springbootmybatiscode.utils.HttpClientUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Test
 * 单元测试
 *
 * @author 杨舜
 * @date 2019-07-09
 */
public class CustomTest {
    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();

    private static ExecutorService pool = new ThreadPoolExecutor(5, 10,
            50L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    @Test
    public void testJson(){

        String str = "{\"cmd\":10002,\"data\":{\"auth\":1,\"qrcode\":\"eewfrwtr234234\",\"dsn\":\"9f04f0db20ab847e0c0b373970aa97e4\"}";
        JSONObject qrcode = null;
        try {
            JSONObject jbj = JSONObject.parseObject(str);
            qrcode = jbj.getJSONObject("data").getJSONObject("qrcode");
            System.out.println(qrcode);
        }catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("测试");
        System.out.println(qrcode);

    }

    @Test
    public void testConcurrent() {
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            pool.execute(new SubThread());
        }
    }

    @Test
    public void testHttpGet() throws Exception{
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", "7");
        HttpClientResult result = HttpClientUtils.doGet("http://127.0.0.1:8099/api/scanners", params);
        System.out.println(result.getContent());
    }

    @Test
    public void testHttpPost() throws Exception{
        /*Map<String, String> headers = new HashMap<>();
        headers.put("Cookie", "123");
        headers.put("Connection", "keep-alive");
        headers.put("Accept", "application/json");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
        Map<String, String> params = new HashMap<>();
        params.put("clientId", "123");
        params.put("dsn", "1,2,3");
        HttpClientResult result = HttpClientUtils.doPost("http://127.0.0.1:8099/api/bindings", params);
        System.out.println(result.getContent());
    }

    @Test
    public void testHttpDelete() throws Exception{
        Map<String, String> params = new HashMap<>();
        params.put("clientId", "1");
        params.put("dsn", "9f04f0db20ab847e0c0b373970aa97e4");
        HttpClientResult result = HttpClientUtils.doDelete("http://127.0.0.1:8099/api/binding", params);
        System.out.println(result.getContent());
    }

    @Test
    public void testList2String() {
        List<String> list = Arrays.asList("a","b","c","d");
        String str = String.join(",",list);
        System.out.println(str);
    }
}
class SubThread implements Runnable {
    @Override
    public void run() {
        try {
            System.out.println(Thread.currentThread().getName() + "正在执行。。。");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            //do nothing
        }
    }
}
