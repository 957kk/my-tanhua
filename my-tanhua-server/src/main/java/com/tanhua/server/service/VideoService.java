package com.tanhua.server.service;

import com.tanhua.dubbo.server.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: my-tanhua
 * @description:
 * @author: xkZhao
 * @Create: 2021-10-16 18:50
 **/
public interface VideoService {
    Boolean saveVideo(MultipartFile picFile, MultipartFile videoFile);
    PageResult queryVideoList(Integer page, Integer pageSize);

    Long likeComment(String videoId);

    Long disLikeComment(String videoId);

    PageResult queryCommentList(String videoId, Integer page, Integer pageSize);

    Boolean saveComment(String videoId, String content);

    Boolean followUser(Long userId);

    Boolean disFollowUser(Long userId);

}
