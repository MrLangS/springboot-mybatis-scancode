package com.faceos.springbootmybatiscode.autostart;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * StartService
 * 自动启动
 *
 * @author lang
 * @date 2019-07-04
 */
@Component
@Order(value = 1)
public class StartService  implements ApplicationRunner {

    @Value("${socket.server.port}")
    private int port;

    /**
     * 创建线程池
     */
    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("Thread-Pool-%d").build();
    private static ExecutorService pool = new ThreadPoolExecutor(5, 5,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public static final Logger logger = LoggerFactory.getLogger(StartService.class);
    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        logger.info("自动启动服务>>>>>>>>>>>");
        logger.info("开始监听socket连接>>端口:"+port);
        /*MsgServer server = new MsgServer();
        server.setUpServer(port);*/
        ServerSocket serverSocket = null;
        int num = 0;

        try{
            serverSocket = new ServerSocket(port);
            //使用循环方式一直等待客户端连接
            while (true) {
                num++;
                Socket accept = serverSocket.accept();
                logger.info("客户端IP：" + accept.getRemoteSocketAddress());
                pool.execute(new ServerThread(accept));
                /*new Thread(new ServerThread(accept),"Client "+num).start();*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.error("-----> serverSocket closed:",e);
            }
        }
    }
}
