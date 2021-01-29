package com.yss.datamiddle.quality.execute.core.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @desc 检查规则-校验级别
 * @author jiafupeng
 * @create 2020/11/24 15:39
 * @update 2020/11/24 15:39
 **/
public enum CheckRuleCheckLevelEnum {

    /**
     * 校验级别：1-库，2-表，3-字段
     */
    TABLE(2, "表"),
    FIELD(3, "字段"),
    CUSTOM(4, "自定义");

    CheckRuleCheckLevelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDescByCode(int code){
        CheckRuleCheckLevelEnum[] values = CheckRuleCheckLevelEnum.values();
        for(CheckRuleCheckLevelEnum entry : values){
            if(entry.getCode() == code){
                return entry.getDesc();
            }
        }

        return "unknow check level: " + code;
    }
}
