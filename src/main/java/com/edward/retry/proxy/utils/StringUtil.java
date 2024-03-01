package com.edward.retry.proxy.utils;

import com.alibaba.fastjson.JSON;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class StringUtil {

    public static final Logger log = LoggerFactory.getLogger(StringUtil.class);

    private static Long randomIndex = System.currentTimeMillis() / 100000;
    private static char[] dicArray = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String regEx = "[\u4e00-\u9fa5]";
    private static final Pattern pat = Pattern.compile(regEx);
    private static final String MOBILE_REGEX = "^(0|86|17951)?(13[0-9]|15[012356789]|166|17[3678]|18[0-9]|14[57])[0-9]{8}$";


    /**
     * 判断字符串是否为空
     *
     * @Author zhouq
     * @Date 2017/10/30 16:23
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    /**
     * 判断字符串非空
     *
     * @Author zhouq
     * @Date 2017/10/30 16:24
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * @param str
     * @return
     */
    public static boolean isAnyEmpty(String... str) {
        for (String s : str) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNoneEmpty(String... str) {
        return !isAnyEmpty(str);
    }

    /**
     * MD5加密
     *
     * @Author zhouq
     * @Date 2017/10/30 16:22
     */
    public static String encodeMD5(String s) {
        return encodeMD5(s,null);
    }

    /**
     * MD5加盐加密
     */
    public static String encodeMD5(String s,String salt) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            log.error("error", ex);
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        md.update(s.getBytes());
        if(isNotEmpty(salt)){
            md.update(salt.getBytes());
        }
        byte[] datas = md.digest();
        int len = datas.length;
        char str[] = new char[len * 2];
        int k = 0;
        for (int i = 0; i < len; i++) {
            byte byte0 = datas[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }


    /**
     * 生成 HMACSHA256
     *
     * @param data 待处理数据
     * @param key  密钥
     * @return 加密结果
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * 判断是否包含符号或者汉字
     *
     * @Author zhouq
     * @Date 2017/10/30 16:22
     */
    public static boolean ifContainSymbolOrChinese(String input) {
        for (int i = 0; i < input.length(); i++) {//符号
            char bb = input.charAt(i);
            if ("`¬!\"£$%^*()~=#{}[];':,./?/*-_+，·@#￥……&。、‘！".contains(bb + "")) {
                return false;
            }
        }
        Matcher matcher = pat.matcher(input);//汉字
        if (matcher.find()) {
            return false;
        }
        return true;
    }


    /**
     * 判断是否全都为数字
     *
     * @Author zhouq
     * @Date 2017/10/30 16:20
     */
    public static boolean ifAllNumbers(String input) {
        boolean isNum = input.matches("[0-9]+");
        return isNum;
    }


    /**
     * 获取随机字符串
     *
     * @Author zhouq
     * @Date 2017/10/30 16:19
     */
    public static String randomString(int length) {
        String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    public static String randomNum(int length) {
        String ALLCHAR = "0123456789";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
    }

    /**
     * 获取文件的后缀名
     *
     * @param filename
     * @return
     */
    public static String getExt(String filename) {
        String ext = null;
        int index = filename.lastIndexOf(".");
        if (index > 0) {
            ext = filename.substring(index + 1).toLowerCase();
        }
        return ext;
    }


    /**
     * 本地安全的 随机数算法，最小长度为 5，小于5的会忽略
     *
     * @return
     */
    public synchronized static String localSafeRandomString(int len) {
        randomIndex++;
        StringBuffer sb = new StringBuffer(Long.toString(randomIndex, 36));
        while (len > sb.length()) {
            sb.append("0");
        }
        return sb.toString();
    }

    /**
     * 去空白
     *
     * @param str
     * @return
     */
    public static String trim(String str) {
        if (str == null) {
            return "";
        } else {
            return str.trim();
        }
    }


    /**
     * 格式化字符串，左边补0，长度一共为len
     *
     * @param str
     * @return
     */
    public static String formatStr(String str, int size) {
        StringBuilder sb = new StringBuilder(str);
        int len = sb.length();
        for (int i = 0; i < size - len; i++) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }


    public static String valueOf(Object obj) {
        if (obj == null) {
            return "";
        }
        if ((obj instanceof String)) {
            String strObj = obj.toString().trim();
            if ("null".equalsIgnoreCase(strObj)) {
                return "";
            }
            return strObj;
        }
        if ((obj instanceof BigDecimal)) {
            BigDecimal bigObj = (BigDecimal) obj;
            return bigObj.toString();
        }

        return obj.toString().trim();
    }




    public static String toJsonStr(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static Object jsonToObj(String json, Class t) {
        return JSON.parseObject(json, t);
    }

    public static List jsonToArray(String json, Class t) {
        return JSON.parseArray(json, t);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public synchronized static String initTokenId() {
        StringBuffer tk = new StringBuffer(uuid());
        int l = tk.length();
        for (int i = l; i < 48; i++) {
            int x = new Random().nextInt(36);
            tk.append(dicArray[x % dicArray.length]);
        }
        return tk.toString().toUpperCase();
    }

    /**
     * 如果不是手机号则抛出异常
     * @param mobile 手机号
     */
    public static boolean isMobile(String mobile) {
        if (isEmpty(mobile) || !Pattern.matches(MOBILE_REGEX, mobile)) {
            return false;
        }
        return true;
    }


    public static String encodeMD5(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        md.update(bytes);
        byte[] datas = md.digest();
        int len = datas.length;
        char str[] = new char[len * 2];
        int k = 0;
        for (int i = 0; i < len; i++) {
            byte byte0 = datas[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}
