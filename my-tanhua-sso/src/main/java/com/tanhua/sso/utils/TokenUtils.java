package com.tanhua.sso.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.Map;

/**
 * @program: my-tanhua
 * @description: Jwts_token工具类
 * @author: xkZhao
 * @Create: 2021-10-10 10:33
 **/
public class TokenUtils {

    private TokenUtils() {
    }

    /**
     * 默认设置为12小时有效期
     */
    private static final Date DATE = new DateTime().plusYears(1).toDate();

    private static final String ERROR = "过期时间必须大于当前系统时间";

    /**
     * 生成token
     *
     * @param secret 密钥
     * @param claims token第二部分
     * @return token字符串
     */
    public static String buildToken(String secret, Map<String, Object> claims) {
        return Jwts.builder()
                //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .setClaims(claims)
                //设置加密方法和加密盐
                .signWith(SignatureAlgorithm.HS256, secret)
                //设置过期时间，12小时后过期
                .setExpiration(DATE)
                .compact();
    }

    /**
     * 生成token
     *
     * @param secret 密钥
     * @param claims token第二部分
     * @param date   过期时间
     * @return token字符串
     */
    public static String buildToken(String secret, Map<String, Object> claims, Date date) {
        long time = date.getTime();
        long now = System.currentTimeMillis();
        if (time < now) {
            return new Throwable(ERROR).toString();
        }
        return Jwts.builder()
                //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .setClaims(claims)
                //设置加密方法和加密盐
                .signWith(SignatureAlgorithm.HS256, secret)
                //设置过期时间，12小时后过期
                .setExpiration(date)
                .compact();
    }

    /**
     * 解析token
     *
     * @param secret 密钥
     * @param token  token值
     * @return 通过token解析到的数据
     */
    public static Map<String, Object> parseToken(String secret, String token) {
        // 通过token解析数据
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}

