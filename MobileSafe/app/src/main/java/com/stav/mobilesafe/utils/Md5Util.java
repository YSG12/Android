package com.stav.mobilesafe.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2017/6/11.
 */

public class Md5Util {

    /**
     * 给指定字符串按照md5算法加密
     * @param pwd  添加加密的密码 加盐处理
     * @return  md5后的字符串
     */
    public static String encoder(String pwd) {
        try {
            //0.加盐
            pwd = pwd + "mobilesafe";
            //1.指定加密算法类型
            MessageDigest digest = MessageDigest.getInstance("Md5");
            //2.将需要加密的字符中转换成byte类型的数组，然后进行随机哈希过程
            byte[] bs = digest.digest(pwd.getBytes());
            //3.玄幻遍历bs，然后让其生成32位字符串，固定写法
            //4.拼接字符串过程
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bs){
                //int类型的i需要转换成16进制字符
                int i= b & 0xff;
                String  hexString = Integer.toString(i);

                if (hexString.length() < 2){
                    hexString = "0"+hexString;
                }

                stringBuffer.append(hexString);
            }

            return stringBuffer.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
