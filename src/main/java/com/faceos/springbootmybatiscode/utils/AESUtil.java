package com.faceos.springbootmybatiscode.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * AESUtil
 * aes工具类
 *
 * @author lang
 * @date 2019-07-08
 */
public class AESUtil {
    private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

    /**
     * 密钥
     * iv的长度是16个字节
     * 签名key的长度是32个字节，长度固定
     * 签名生成算法为sha1(iv + aes256(data).base64 + key), 长度为40位
     *
     * 公共协议里面数据部分,生成方式： iv + aes256(data)，这里的aes256(data)是加密后的字节数据直接拼上iv组成的数据包包体。
     * 然后整体做base64编码
     * 这里需要和签名中的区分开来，签名中的aes256(data).base64是base64的字符串去拼接iv和签名key
     */
    public static final String IV = "szFz2pZsy3bAqKJy";
    public static final String ENCRYPTION_KEY = "IR8cQ9wzm46NJ81RSWN9FjOPqdWruVls";
    public static final String SIGNATURE_KEY = "d9CXVjmdl3Xz5mfpZWqKOEdqTjBrVyX8";
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param password 加密密码
     * @param iv 使用CBC模式，需要一个向量iv，可增加加密算法的强度
     * @return 加密数据
     */
    public static byte[] encrypt(String content, String password,String iv) {
        try {
            //创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //密码key(超过16字节即128bit的key，需要替换jre中的local_policy.jar和US_export_policy.jar，否则报错：Illegal key size)
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("utf-8"),KEY_ALGORITHM);

            //向量iv
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));

            //初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivParameterSpec);

            //加密
            byte[] byteContent = content.getBytes("utf-8");
            byte[] result = cipher.doFinal(byteContent);

            return result;
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }

        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content 密文
     * @param password 密码
     * @param iv 使用CBC模式，需要一个向量iv，可增加加密算法的强度
     * @return 明文
     */
    public static String decrypt(byte[] content, String password,String iv) {

        try {
            //创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //密码key
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("utf-8"),KEY_ALGORITHM);

            //向量iv
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));

            //初始化为解密模式的密码器
            cipher.init(Cipher.DECRYPT_MODE,keySpec,ivParameterSpec);

            //执行操作
            byte[] result = cipher.doFinal(content);

            return new String(result,"utf-8");
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
        }

        return null;
    }

    /**
     * 删除数组元素
     *
     * @param index
     * @param array
     * @return
     */
    public static String[] delete(int index, String[] array) {
        String[] arrNew = new String[array.length - 1];
        for (int i = index; i < array.length - 1; i++) {
            array[i] = array[i + 1];
        }
        System.arraycopy(array, 0, arrNew, 0, arrNew.length);
        return arrNew;
    }

    /**
     * 为字符串数组元素添加头
     * @param head
     * @param array
     * @return
     */
    public static void addHead(String head, String[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = head + array[i];
        }
    }

    public static JSONObject strArr2Json(String[] str) {
        JSONObject obj = new JSONObject();
        for (String item : str) {
            int index = item.indexOf("=");
            obj.put(item.substring(0,index),item.substring(index + 1));
        }
        return obj;
    }

    /**
     * SHA1加密
     *
     * @param data 要加密的字符串
     * @return 加密的字符串
     */
    public static String sha1(String data) {
        String aes = getAESData(data);
        StringBuffer signString = new StringBuffer();
        signString.append(IV).append(aes).append(SIGNATURE_KEY);

        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(signString.toString().replaceAll("\r|\n", "").getBytes());
            byte[] messageDigest = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString().toUpperCase();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获得AES加密数据的base64编码
     *
     * @param str
     * @return
     */
    public static String getAESData(String str){
        byte[] data = encrypt(str,ENCRYPTION_KEY,IV);
        return Encodes.encodeBase64(data);
    }

    /**
     * 加密原始数据，并返回BASE64编码之后的数据
     *
     * @param str
     * @return
     */
    public static String getData(String str){
        byte[] data = encrypt(str,ENCRYPTION_KEY,IV);
        byte[] iv = IV.getBytes();
        byte[] packageData = ArrayUtils.addAll(iv,data);
        return Encodes.encodeBase64(packageData);
    }
}
