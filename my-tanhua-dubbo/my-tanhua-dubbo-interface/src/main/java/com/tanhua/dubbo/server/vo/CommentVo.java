package com.tanhua.dubbo.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 评论
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentVo implements Serializable {
    private static final long serialVersionUID = -2105385689859184204L;

    private String id; //评论id
    private String avatar; //头像
    private String nickname; //昵称
    private String content; //评论
    private String createDate; //评论时间: 08:27
    private Integer likeCount; //点赞数
    private Integer hasLiked; //是否点赞（1是，0否）

}