package com.tanhua.server.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.utils.RelativeDateFormat;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.pojo.Publish;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.dubbo.server.vo.PageResult;
import com.tanhua.dubbo.server.vo.QuanZiVo;
import com.tanhua.server.service.QuanZiService;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-13 20:19
 **/
@Service
public class QuanZiServiceImpl implements QuanZiService {

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

  /*  @Autowired
    private PicUploadService picUploadService;*/

    @Override
    public PageResult queryPublishList(Integer page, Integer pageSize) {
        //分析：通过dubbo中的服务查询用户的好友动态
        //通过mysql查询用户的信息，回写到结果对象中（QuanZiVo）

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //直接从ThreadLocal中获取对象
        User user = UserThreadLocal.get();

        //通过dubbo查询数据
        PageInfo<Publish> pageInfo = this.quanZiApi.queryPublishList(user.getId(), page, pageSize);
        List<Publish> records = pageInfo.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }

        pageResult.setItems(this.fillQuanZiVo(records));
        return pageResult;
    }

    /**
     * 填充用户信息
     *
     * @param userInfo
     * @param quanZiVo
     */
    @Override
    public void fillUserInfoToQuanZiVo(UserInfo userInfo, QuanZiVo quanZiVo){
        BeanUtil.copyProperties(userInfo, quanZiVo, "id");
        quanZiVo.setGender(userInfo.getSex().name().toLowerCase());
        quanZiVo.setTags(StringUtils.split(userInfo.getTags(), ','));

        quanZiVo.setCommentCount(0); //TODO 评论数
        quanZiVo.setDistance("1.2公里"); //TODO 距离
        quanZiVo.setHasLiked(0); //TODO 是否点赞（1是，0否）
        quanZiVo.setLikeCount(0); //TODO 点赞数
        quanZiVo.setHasLoved(0); //TODO 是否喜欢（1是，0否）
        quanZiVo.setLoveCount(0); //TODO 喜欢数
    }

    /**
     * 根据查询到的publish集合填充QuanZiVo对象
     *
     * @param records
     * @return
     */
    @Override
    public List<QuanZiVo> fillQuanZiVo(List<Publish> records){
        List<QuanZiVo> quanZiVoList = new ArrayList<>();
        records.forEach(publish -> {
            QuanZiVo quanZiVo = new QuanZiVo();
            quanZiVo.setId(publish.getId().toHexString());
            quanZiVo.setTextContent(publish.getText());
            quanZiVo.setImageContent(publish.getMedias().toArray(new String[]{}));
            quanZiVo.setUserId(publish.getUserId());
            quanZiVo.setCreateDate(RelativeDateFormat.format(new Date(publish.getCreated())));

            quanZiVoList.add(quanZiVo);
        });

        //查询用户信息
        List<Object> userIds = CollUtil.getFieldValues(records, "userId");
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoByUserIdList(userIds);
        for (QuanZiVo quanZiVo : quanZiVoList) {
            //找到对应的用户信息
            for (UserInfo userInfo : userInfoList) {
                if(quanZiVo.getUserId().longValue() == userInfo.getUserId().longValue()){
                    this.fillUserInfoToQuanZiVo(userInfo, quanZiVo);
                    break;
                }
            }
        }

        return quanZiVoList;
    }

    /**
     * 发布动态
     *
     * @param textContent
     * @param location
     * @param latitude
     * @param longitude
     * @param multipartFile
     * @return
     */
    @Override
    public String savePublish(String textContent,
                              String location,
                              String latitude,
                              String longitude,
                              MultipartFile[] multipartFile) {
        //查询当前的登录信息
        User user = UserThreadLocal.get();

        Publish publish = new Publish();
        publish.setUserId(user.getId());
        publish.setText(textContent);
        publish.setLocationName(location);
        publish.setLatitude(latitude);
        publish.setLongitude(longitude);
        publish.setSeeType(1);

        List<String> picUrls = new ArrayList<>();
        picUrls.add("adadad");
        //图片上传
       /* for (MultipartFile file : multipartFile) {
            PicUploadResult picUploadResult = this.picUploadService.upload(file);
            picUrls.add(picUploadResult.getName());
        }*/

        publish.setMedias(picUrls);
        return this.quanZiApi.savePublish(publish);
    }

    @Override
    public PageResult queryRecommendPublishList(Integer page, Integer pageSize) {
        //分析：通过dubbo中的服务查询系统推荐动态
        //通过mysql查询用户的信息，回写到结果对象中（QuanZiVo）

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);

        //直接从ThreadLocal中获取对象
        User user = UserThreadLocal.get();

        //通过dubbo查询数据
        PageInfo<Publish> pageInfo = this.quanZiApi.queryRecommendPublishList(user.getId(), page, pageSize);
        List<Publish> records = pageInfo.getRecords();
        if (CollUtil.isEmpty(records)) {
            return pageResult;
        }

        pageResult.setItems(this.fillQuanZiVo(records));
        return pageResult;
    }
}
