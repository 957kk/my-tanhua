package com.tanhua.dubbo.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 关注用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "follow_user")
@Builder
public class FollowUser implements java.io.Serializable{

    private static final long serialVersionUID = 3148619072405056052L;
    /**
     *主键id
     */
    private ObjectId id;
    /**
     *用户id
     */
    private Long userId;
    /**
     *关注的用户id
     */
    private Long followUserId;
    /**
     *关注时间
     */
    private Long created;
}
