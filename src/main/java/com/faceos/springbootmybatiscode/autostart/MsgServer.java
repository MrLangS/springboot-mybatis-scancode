package com.faceos.springbootmybatiscode.autostart;

import com.alibaba.fastjson.JSONObject;
import com.faceos.springbootmybatiscode.utils.AESUtil;
import com.faceos.springbootmybatiscode.utils.Encodes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static com.faceos.springbootmybatiscode.utils.AESUtil.strArr2Json;

/**
 * MsgServer
 * socket服务端
 *
 * @author lang
 * @date 2019-07-08
 */
public class MsgServer {
    private static final String SCMD = "scmd";
    private static final String HEART = "19";
    private static final String TIMESTAMP = "17";

    /*public static void main(String[] args) {
        try {
            MsgServer server = new MsgServer();
            server.setUpServer(8001);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void setUpServer(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        while (true) {
            Socket client = server.accept();
            System.out.println("客户端IP：" + client.getRemoteSocketAddress());
            processMesage(client);
        }
    }

    /**
     * 获取客户端发送的数据
     *
     * @param client
     * @throws IOException
     */
    private void processMesage(Socket client) throws IOException {
        InputStream ins = null;
        OutputStream out = null;
        try {
            ins = client.getInputStream();
            out = client.getOutputStream();

            byte[] buffer = new byte[1024];
            StringBuffer strbuf = new StringBuffer();
            int n, available;
            int i = 1;
            boolean flag = true;
            while (true) {

                while ((n = ins.read(buffer)) > 0) {
                    strbuf.append(new String(buffer, 0, n));
                    System.out.println("第" + i + "次获取包");

                    System.out.println(strbuf.toString());
                    processData(strbuf.toString(), out);
                    strbuf.delete(0, strbuf.length());
                    i++;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            if (ins != null) {
                ins.close();
            }
            if (out != null) {
                out.close();
            }
        }

    }

    /**
     * 处理客户端发送的数据，并返回心跳包
     *
     * @param dataStr
     * @param out
     */
    private void processData(String dataStr, OutputStream out) throws IOException {

        String[] strArr = dataStr.split("&");
        JSONObject jbj = strArr2Json(strArr);
        String data = jbj.getString("data");
        System.out.println(jbj);
        if (jbj.getString(SCMD).equals(HEART)) {
            System.out.println("发送心跳包");
            sendHeart(jbj, out);
        }

        if (jbj.getString(SCMD).equals(TIMESTAMP)) {
            System.out.println("同步时间戳");
            sendTimestamp(jbj, out);
        }

        //解密数据
        byte[] bt = Encodes.decodeBase64(data);
        System.out.println("数据:");
        byte[] bytedata = Arrays.copyOfRange(bt, 16, bt.length);
        System.out.println(AESUtil.decrypt(bytedata, AESUtil.ENCRYPTION_KEY, AESUtil.IV));
        System.out.println("---------------------------------------------");
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
        timestamp.put("timestamp",System.currentTimeMillis());
        String data = timestamp.toJSONString();

        String replyData = AESUtil.getData(data);
        String replySign = AESUtil.sha1(data);
        String reply = getReplyMsg(jbj, replyData, replySign);
        System.out.println(reply);

        out.write(reply.getBytes());
        out.flush();
    }

    /**
     * 发送心跳包
     *
     * @param jbj
     * @param out
     * @throws IOException
     */
    private void sendHeart(JSONObject jbj, OutputStream out) throws IOException {
        String replyData = AESUtil.getData("{}");
        String replySign = AESUtil.sha1("{}");
        String reply = getReplyMsg(jbj, replyData, replySign);
        System.out.println(reply);

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
