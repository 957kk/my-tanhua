package com.tanhua.dubbo.server.enums;

import java.io.Serializable;

/**
 * 评论类型：1-点赞，2-评论，3-喜欢
 */
public enum CommentType implements Serializable {

    LIKE(1), COMMENT(2), LOVE(3);

    int type;

    CommentType(int type) {
        this.type = type;
    }

    private static final long serialVersionUID = -2105385689859184204L;

    public int getType() {
        return type;
    }
}