package com.tanhua.dubbo.server.api;

import com.tanhua.dubbo.server.enums.CommentType;
import com.tanhua.dubbo.server.pojo.Comment;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageInfo;

public interface QuanZiApi {

    /**
     * 查询好友动态
     *
     * @param userId 用户id
     * @param page 当前页数
     * @param pageSize 每一页查询的数据条数
     * @return
     */
    PageInfo<Publish> queryPublishList(Long userId, Integer page, Integer pageSize);

    /**
     * 发布动态
     *
     * @param publish
     * @return 发布成功返回动态id
     */
    String savePublish(Publish publish);

    /**
     * 查询推荐动态
     *
     * @param userId 用户id
     * @param page 当前页数
     * @param pageSize 每一页查询的数据条数
     * @return
     */
    PageInfo<Publish> queryRecommendPublishList(Long userId, Integer page, Integer pageSize);

    /**
     * 是否已点赞并且返回本条的点赞数量
     * @param userId
     * @param publishId
     * @return
     */
    Boolean likeComment(Long userId, String publishId);


    String getCommentRedisKeyPrefix(String publishId);

    String getCommentUserLikeRedisKeyPrefix(Long userId);

    /**
     * 发表评论
     *
     * @param userId
     * @param publishId
     * @param content
     * @return
     */
    Boolean saveComment(Long userId, String publishId, String content);

    Comment queryCommentById(String publishId);

    Publish queryPublishById(String publishId);

    Boolean disLikeComment(Long userId, String publishId);

    Long queryCount(String publishId, CommentType commentType);

    Boolean loveComment(Long id, String publishId);

    Boolean disLoveComment(Long id, String publishId);

    PageInfo<Comment> queryCommentList(String publishId, Integer page, Integer pageSize);

    boolean queryUserIsLike(Long id, String id1);
}
