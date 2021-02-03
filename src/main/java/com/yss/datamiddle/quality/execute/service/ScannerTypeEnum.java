package com.yss.datamiddle.quality.execute.service;

/**
 * @author jiafupeng
 * @desc 扫描范围枚举
 * @create 2020/12/11 14:57
 * @update 2020/12/11 14:57
 **/
public enum ScannerTypeEnum {

    /**
     * 扫描范围枚举：1-全部 2-按条件
     */
    ALL(1),
    CONDITION(2);

    private final int value;

    ScannerTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
