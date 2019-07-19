package com.faceos.springbootmybatiscode.autostart;

import com.alibaba.fastjson.JSONObject;
import com.faceos.springbootmybatiscode.domain.CodeScanner;
import com.faceos.springbootmybatiscode.service.PersonService;
import com.faceos.springbootmybatiscode.service.ScannerService;
import com.faceos.springbootmybatiscode.utils.AESUtil;
import com.faceos.springbootmybatiscode.utils.ApplicationContextProvider;
import com.faceos.springbootmybatiscode.utils.Encodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.faceos.springbootmybatiscode.utils.AESUtil.strArr2Json;
import com.faceos.springbootmybatiscode.utils.TimeUtil;

/**
 * ServerThread
 * 服务器处理客户端会话的线程
 *
 * @author lang
 * @date 2019-07-09
 */
@SuppressWarnings("unchecked")
public class ServerThread implements Runnable {

    private PersonService personService;
    private ScannerService scannerService;

    private Socket socket = null;

    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    private static final String SCMD = "scmd";
    private static final String CMD = "cmd";
    private static final String HEART = "19";
    private static final String REGISTER = "16";
    private static final String TIMESTAMP = "17";
    private static final String BUSINESS = "48";
    private static final int TIME_DIFF = 3;
    public static final int AUTH_CMD = 10012;
    public static final int CODE_CMD = 10002;
    public static final int RESULT_CMD = 11000;

    public ServerThread(Socket socket) {
        logger.info("Create a new ServerThread");
        this.socket = socket;
        personService = ApplicationContextProvider.getBean(PersonService.class);
        scannerService = ApplicationContextProvider.getBean(ScannerService.class);
    }


    @Override
    public void run() {
        InputStream ins = null;
        OutputStream out = null;
        try {
            ins = socket.getInputStream();
            out = socket.getOutputStream();

            byte[] buffer = new byte[1024];
            StringBuffer strbuf = new StringBuffer();
            int n;
            int i = 1;
            while (true) {

                while ((n = ins.read(buffer)) > 0) {
                    strbuf.append(new String(buffer, 0, n));

                    logger.info("第[{}]次获取包", i);
                    processData(strbuf.toString(), out);
                    strbuf.delete(0, strbuf.length());
                    i++;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 处理客户端发送的数据
     *
     * @param dataStr
     * @param out
     */
    private void processData(String dataStr, OutputStream out) throws IOException {

        String[] strArr = dataStr.split("&");
        JSONObject jbj = strArr2Json(strArr);
        String data = jbj.getString("data");

        String scmd = jbj.getString(SCMD);
        switch (scmd) {
            case REGISTER:
                logger.info("设备注册信息");
                rigisterDevice(jbj, out, data);
                break;
            case HEART:
                logger.info("发送心跳包>>>");
                sendHeart(jbj, out);
                break;
            case TIMESTAMP:
                logger.info("同步时间戳>>>");
                sendTimestamp(jbj, out);
                break;
            case BUSINESS:
                logger.info("处理业务信息");
                handleBusiness(jbj, out, data);
                break;
            default:
                decrypt(data);
        }

        logger.info("---------------------------------------------");
    }

    /**
     * 注册设备
     *
     * @param data
     */
    private void rigisterDevice(JSONObject jbj, OutputStream out, String data) throws IOException {
        String recvData = decrypt(data);

        CodeScanner scanner = JSONObject.parseObject(recvData, CodeScanner.class);
        if (scannerService.findById(scanner.getDsn()) == null) {
            scannerService.insert(scanner);
            logger.info("添加设备");
        }

        sendFeedback(jbj, out, "{}");
        logger.info("发送设备注册回包");
    }

    /**
     * 处理业务信息
     *
     * @param jbj
     * @param out
     * @param data
     */
    private void handleBusiness(JSONObject jbj, OutputStream out, String data) throws IOException {
        String recvData = decrypt(data);
        JSONObject recvJbj = JSONObject.parseObject(recvData);

        int cmd = recvJbj.getIntValue(CMD);
        switch (cmd) {
            case AUTH_CMD:
                logger.info("发送鉴权>>>");
                sendAuthen(jbj, out);
                break;
            case CODE_CMD:
                logger.info("二维码信息>>>");
                sendFeedback(jbj, out, recvJbj);
                break;
            default:
                logger.warn("其他");
        }
    }

    /**
     * 发送鉴权包
     *
     * @param jbj
     * @param out
     * @throws IOException
     */
    private void sendAuthen(JSONObject jbj, OutputStream out) throws IOException {
        JSONObject data = new JSONObject();
        JSONObject feedback = new JSONObject();
        data.put("auth", 1);
        feedback.put("data", data);
        feedback.put("cmd", AUTH_CMD);

        String backmsg = feedback.toJSONString();

        logger.info("响应消息>>>[{}]", backmsg);

        sendFeedback(jbj, out, backmsg);
    }

    /**
     * 扫码响应
     *
     * @param jbj
     * @param out
     * @param codedata
     * @throws IOException
     */
    private void sendFeedback(JSONObject jbj, OutputStream out, JSONObject codedata) throws IOException {
        JSONObject data = new JSONObject();
        JSONObject feedback = new JSONObject();

        String dsn = codedata.getString("dsn");
        JSONObject qrcode = null;
        try {
            qrcode = codedata.getJSONObject("data").getJSONObject("qrcode");
        } catch (Exception e) {
            logger.error("二维码数据格式不正确:[{}]",e);
        }
        String backmsg = null;

        if(qrcode != null) {
            int id = qrcode.getIntValue("id");
            String time = qrcode.getString("time");
            int check = qrcode.getIntValue("check");
            switch (check) {
                case 1:
                    logger.info("接收绑定设备二维码数据");
                    backmsg = handleBindCode(dsn, id, data, feedback);
                    break;
                case 2:
                    logger.info("接收通行设备二维码数据");
                    backmsg = handlePassageCode(dsn, id, time, data, feedback);
                    break;
                default:
                    logger.info("无效二维码");
                    add(data, feedback, 10001, "操作失败，请重试");
                    backmsg = feedback.toJSONString();
            }
        } else {
            logger.info("无效二维码");
            add(data, feedback, 10001, "操作失败，请重试");
            backmsg = feedback.toJSONString();
        }


        logger.info("响应消息>>>[{}]", backmsg);

        sendFeedback(jbj, out, backmsg);
    }

    /**
     * 处理绑定二维码
     *
     * @param dsn
     * @param id
     * @param data
     * @param feedback
     * @return
     */
    private String handleBindCode(String dsn, Integer id, JSONObject data, JSONObject feedback) {
        Map bind = scannerService.getBind(dsn, id);
        if (bind == null) {
            Map map = new HashMap<String, Object>(2);
            map.put("dsn", dsn);
            map.put("id", id);
            scannerService.bindScanByClient(map);
            logger.info("添加设备绑定");

            add(data, feedback, 0, "扫码成功");
        } else {
            add(data, feedback, 10001, "操作失败，请重试");
        }
        return feedback.toJSONString();
    }

    /**
     * 处理通行二维码
     *
     * @param dsn
     * @param id
     * @param data
     * @param feedback
     * @return
     */
    private String handlePassageCode(String dsn, Integer id, String time, JSONObject data, JSONObject feedback) {
        Date currentTime = new Date();
        Date time1 = TimeUtil.getDateWithDateString(time);
        int diff = TimeUtil.getMinuteDiffByTime(time1,currentTime);
        logger.info("二维码时间相差[{}]",diff);
        if(diff >= TIME_DIFF){
            logger.warn("二维码过期");
            add(data, feedback, 10001, "操作失败，请重试");
        } else {
            Map bind = scannerService.getBind(dsn, id);
            if (bind != null) {
                logger.info("已成功找到");
                add(data, feedback, 0, "确认成功");
            } else {
                logger.info("未找到");
                add(data, feedback, 10001, "操作失败，请重试");
            }
        }

        return feedback.toJSONString();
    }

    private void add(JSONObject data, JSONObject feedback, int code, String msg) {
        data.put("code", code);
        data.put("err_msg", msg);
        feedback.put("cmd", RESULT_CMD);
        feedback.put("data", data);
    }

    /**
     * 解密数据
     *
     * @param data
     * @return
     */
    private String decrypt(String data) {
        byte[] bt = Encodes.decodeBase64(data);
        byte[] bytedata = Arrays.copyOfRange(bt, 16, bt.length);
        String realdata = AESUtil.decrypt(bytedata, AESUtil.ENCRYPTION_KEY, AESUtil.IV);

        logger.info("解密数据>>>[{}]", realdata);
        return realdata;
    }

    /**
     * 同步时间戳
     *
     * @param jbj
     * @param out
     * @throws IOException
     */
    private void sendTimestamp(JSONObject jbj, OutputStream out) throws IOException {
        JSONObject timestamp = new JSONObject();
        timestamp.put("timestamp", System.currentTimeMillis());
        String data = timestamp.toJSONString();

        sendFeedback(jbj, out, data);
    }

    /**
     * 发送心跳包
     *
     * @param jbj
     * @param out
     * @throws IOException
     */
    private void sendHeart(JSONObject jbj, OutputStream out) throws IOException {
        sendFeedback(jbj, out, "{}");
    }

    private void sendFeedback(JSONObject jbj, OutputStream out, String feedback) throws IOException {
        String replyData = AESUtil.getData(feedback);
        String replySign = AESUtil.sha1(feedback);
        String reply = getReplyMsg(jbj, replyData, replySign);
        out.write(reply.getBytes());
        out.flush();
    }

    private String getReplyMsg(JSONObject jbj, String data, String sign) {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append("proVer=").append(jbj.getString("proVer"))
                .append("&appId=").append(jbj.getString("appId"))
                .append("&appVer=").append(jbj.getString("appVer"))
                .append("&scmd=").append(jbj.getString("scmd"))
                .append("&msgId=").append(jbj.getString("msgId"))
                .append("&deviceId=").append(jbj.getString("deviceId"))
                .append("&repush=").append("false")
                .append("&data=").append(data)
                .append("&sign=").append(sign);

        return strbuf.toString().replaceAll("\r|\n", "");
    }
}
