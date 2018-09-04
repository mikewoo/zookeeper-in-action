package com.mikewoo.server.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Eric Gui
 * @date 2018/9/4
 */
@Data
public class SettingConfig {

    public final static String ADD_OPS = "add";
    public final static String UPDATE_OPS = "update";
    public final static String DELETE_OPS = "delete";

    // 需要配置的类型，1：MySQL DataSource 2：Redis 3： Kafka
    @JsonProperty("type")
    private int type;

    // 配置操作： "add"： 新增配置， "update"： 更新配置， "delete"： 删除配置
    @JsonProperty("ops")
    private String ops;

    // 配置文件下载地址，新增和更新时必须提供，删除配置时值为""
    @JsonProperty("url")
    private String url;

    // 管理员备注
    @JsonProperty("remark")
    private String remark;

}
