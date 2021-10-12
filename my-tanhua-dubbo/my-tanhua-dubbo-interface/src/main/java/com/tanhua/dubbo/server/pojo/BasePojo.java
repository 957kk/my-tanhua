package com.tanhua.dubbo.server.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class BasePojo implements Serializable {
    private static final long serialVersionUID = 362498820763181265L;
    @TableField(fill = FieldFill.INSERT) //MP自动填充
    private Date created;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;
}
