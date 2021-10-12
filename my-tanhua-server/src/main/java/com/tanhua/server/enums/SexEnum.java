package com.tanhua.server.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;

/**
 * @program: my-tanhua
 * @description: SexEnum枚举类
 * @author: xkZhao
 * @Create: 2021-10-11 18:55
 **/
public enum SexEnum implements IEnum<Integer> {

    /**
     * 1代表男（man）
     */
    MAN(1, "男"),
    /**
     * 2代表女（women）
     */
    WOMAN(2, "女"),
    /**
     * 3代表未知（un）
     */
    UNKNOWN(3, "未知");

    /**
     * 接收到的值，存入数据库
     */
    private final int value;
    /**
     * 描述
     */
    private final String desc;

    SexEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }
}
