package com.yss.datamiddle.quality.execute.core.entity;

import lombok.Data;

import java.util.List;

/**
 * @author jiafupeng
 * @desc   数据质量-检查规则 内部规则配置PO
 * @create 2020/11/28 14:31
 * @update 2020/11/28 14:31
 **/
@Data
public class CheckRuleInnerConfigPO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 检查规则-内部规则名称
     */
    private String name;

    /**
     * 检查规则ID(父id)
     */
    private Long checkRuleId;

    /**
     * 校验级别 (1-库级 2-表级 3-字段级 4-自定义)
     */
    private Integer checkLevel;

    /**
     * 数据连接id
     */
    private Integer dataConnId;

    /**
     * 数据连接表名称
     */
    private String dataConnTableName;

    /**
     * 检查对象表名称集合
     */
    private String checkTableNameList;

    /**
     * 检查对象列名称集合
     */
    private String checkColumnNameList;

    /**
     * 检查模板id
     */
    private Integer checkTemplateId;

    /**
     * 自定义sql
     */
    private String customSql;

    /**
     * 扫描范围类型 1-全部 2-条件
     */
    private Integer scannerType;

    /**
     * 扫描范围-条件sql
     */
    private String scannerAreaSql;

    /**
     * 告警条件类型 1-阈值比较 2-表达式
     */
    private Integer warnType;

    /**
     * 阈值比较方式 1-等于 2-大于 3-小于 4-大于等于 5-小于等于 6-不等于
     */
    private Integer thresholdCompareType;

    /**
     * 告警阈值
     */
    private Integer threshold;

    /**
     * 告警表达式
     */
    private String warnExpression;

    /**
     * 是否生成异常数据 0-不生成 1-生成
     */
    private Integer saveErrorDataType;

    /**
     * 异常数据生成条数
     */
    private Integer errorDataCount;

    /**
     * 执行SQL
     */
    private String scriptJson;

    /**
     * 修改用户id
     */
    private String updateUserid;
}
