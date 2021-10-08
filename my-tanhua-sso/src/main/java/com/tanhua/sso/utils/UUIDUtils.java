package com.tanhua.sso.utils;

import java.util.UUID;

/**
 * @program: JDBC_ONE
 * @description:
 * @author: zhaoxuekai
 * @GitHub: 9527mmm
 * @Create: 2021-08-09 08:57
 **/
public class UUIDUtils {
    private UUIDUtils() {
    }

    /**
     * 获取32位uuid生成数；
     * @return
     */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();

    }

    /**
     * 获取uuid生成的纯数字
     *
     * @return
     */
    public static Integer getUUIDInOrderId() {
        Integer orderId = UUID.randomUUID().toString().hashCode();
        //String.hashCode() 值会为空
        orderId = orderId < 0 ? -orderId : orderId;
        return orderId;
    }

}
