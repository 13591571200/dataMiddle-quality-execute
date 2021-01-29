package com.yss.datamiddle.quality.execute.core.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * @author jiafupeng
 * @desc
 * @create 2020/11/19 17:47
 * @update 2020/11/19 17:47
 **/
@Data
public class CheckRulePO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 文件夹id
     */
    private Long dirId;

    /**
     * 检查规则名称
     */
    private String name;

    /**
     * 检查规则描述
     */
    private String ruleDescribe;

    /**
     * 是否通知
     */
    private Integer notifyType;

    /**
     * 通知主题id
     */
    private Long notifyTopicId;

    /**
     * 工作流对应Id
     */
    private Integer processId;

    /**
     * 调度状态 0-下线 1-上线
     */
    private Integer processScheduleStatus;

    /**
     * 调度平台-定时管理ID
     */
    private Integer processScheduleId;

    /**
     * 检查规则调度最后一次上线时间
     */
    private LocalDateTime processScheduleLastOnlineTime;
}
