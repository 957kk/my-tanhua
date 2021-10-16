package com.tanhua.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tanhua.common.pojo.User;
import com.tanhua.common.pojo.UserInfo;
import com.tanhua.common.utils.UserThreadLocal;
import com.tanhua.dubbo.server.api.QuanZiApi;
import com.tanhua.dubbo.server.api.VideoApi;
import com.tanhua.dubbo.server.enums.CommentType;
import com.tanhua.dubbo.server.pojo.Video;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.dubbo.server.vo.PageResult;
import com.tanhua.dubbo.server.vo.VideoVo;
import com.tanhua.server.service.QuanZiService;
import com.tanhua.server.service.UserInfoService;
import com.tanhua.server.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-16 19:19
 **/
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {
    @Reference(version = "1.0.0")
    private VideoApi videoApi;
    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;
    @Autowired
    protected FastFileStorageClient storageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private QuanZiService quanZiService;


    @Override
    public Boolean saveVideo(MultipartFile picFile, MultipartFile videoFile) {
        if (!ObjectUtil.isAllNotEmpty(picFile, videoFile)) {
            return false;
        }
        User user = UserThreadLocal.get();
        try {
            Video video = new Video();
            video.setText("1");
            video.setCreated(System.currentTimeMillis());
            // picFile.transferTo(new File());

            //上传视频到FastDFS中
            StorePath storePath = this.storageClient.uploadFile(videoFile.getInputStream(),
                    videoFile.getSize(),
                    StrUtil.subAfter(videoFile.getOriginalFilename(), '.', true),
                    null);
            video.setVideoUrl(fdfsWebServer.getWebServerUrl() + storePath.getFullPath());
            String videoId = this.videoApi.saveVideo(video);
            return StrUtil.isNotEmpty(videoId);
        } catch (Exception e) {
            log.error("上传小视频出错~ userId = " + user.getId() + ", file = " + videoFile.getOriginalFilename(), e);
        }
        return null;
    }

    @Override
    public PageResult queryVideoList(Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        PageInfo<Video> pageInfo = this.videoApi.queryVideoList(user.getId(), page, pageSize);
        List<Video> records = pageInfo.getRecords();
        if (ObjectUtil.isEmpty(records)) {
            return pageResult;
        }
        //查询用户信息
        List<Object> userIds = CollUtil.getFieldValues(records, "userId");
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoByUserIdList(userIds);
        List<VideoVo> videoVoList = new ArrayList<>();
        for (Video record : records) {
            VideoVo videoVo = new VideoVo();
            videoVo.setUserId(record.getUserId());
            videoVo.setCover(record.getPicUrl());
            videoVo.setVideoUrl(record.getVideoUrl());
            videoVo.setId(record.getId().toHexString());
            videoVo.setSignature("我就是我~");

            videoVo.setCommentCount(Convert.toInt(this.quanZiApi.queryCommentCount(videoVo.getId())));
            videoVo.setHasFocus(this.videoApi.isFollowUser(user.getId(), videoVo.getUserId()) ? 1 : 0);
            videoVo.setHasLiked(this.quanZiApi.queryUserIsLike(user.getId(), videoVo.getId()) ? 1 : 0);
            videoVo.setLikeCount(Convert.toInt(this.quanZiApi.queryCount(videoVo.getId(),CommentType.LIKE)));



            videoVo.setCover("");
            videoVo.setCommentCount(0);
            videoVo.setHasFocus(0);
            videoVo.setHasLiked(0);
            videoVo.setLikeCount(0);
            videoVo.setSignature("");

            for (UserInfo userInfo : userInfoList) {
                if (ObjectUtil.equal(userInfo.getUserId(), videoVo.getUserId())) {
                    videoVo.setNickname(userInfo.getNickName());
                    videoVo.setAvatar(userInfo.getLogo());
                    break;
                }
            }
            videoVoList.add(videoVo);
        }
        pageResult.setItems(videoVoList);
        return pageResult;
    }

    @Override
    public Long likeComment(String videoId) {
        User user = UserThreadLocal.get();
        Boolean result = this.quanZiApi.likeComment(user.getId(), videoId);
        if (result) {
            return this.quanZiApi.queryCount(videoId, CommentType.LIKE);
        }
        return null;
    }

    @Override
    public Long disLikeComment(String videoId) {
        User user = UserThreadLocal.get();
        Boolean result = this.quanZiApi.disLikeComment(user.getId(), videoId);
        if (result) {
            return this.quanZiApi.queryCount(videoId, CommentType.LIKE);
        }
        return null;
    }


    @Override
    public Boolean saveComment(String videoId, String content) {
        return this.quanZiService.saveComments(videoId, content);
    }

    @Override
    public PageResult queryCommentList(String videoId, Integer page, Integer pageSize) {
        return this.quanZiService.queryCommentList(videoId, page, pageSize);
    }
    /**
     * 关注用户
     *
     * @param userId
     * @return
     */
    @Override
    public Boolean followUser(Long userId) {
        User user = UserThreadLocal.get();
        return this.videoApi.followUser(user.getId(), userId);
    }

    /**
     * 取消关注
     *
     * @param userId
     * @return
     */
    @Override
    public Boolean disFollowUser(Long userId) {
        User user = UserThreadLocal.get();
        return this.videoApi.disFollowUser(user.getId(), userId);
    }
}

