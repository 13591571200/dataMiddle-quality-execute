package com.yss.datamiddle.quality.execute.service;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @desc 检查规则-校验级别
 * @author jiafupeng
 * @create 2020/11/24 15:39
 * @update 2020/11/24 15:39
 **/
public enum CheckRuleCheckLevelEnum {

    /**
     * 校验级别：1-库，2-表，3-字段, 4自定义
     */
    TABLE(2, "表"),
    FIELD(3, "字段"),
    CUSTOM(4, "自定义");

    private int code;
    private String desc;

    CheckRuleCheckLevelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean contains(int interval){
        return Arrays.stream(values()).map(CheckRuleCheckLevelEnum::getCode).collect(Collectors.toList()).contains(interval);
    }
}
