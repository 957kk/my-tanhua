package com.tanhua.dubbo.server.api;

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

}
