package com.yss.datamiddle.quality.execute.datasource.enums;

/**
 * @author jiafupeng
 * @desc 连接枚举
 * @create 2020/12/7 15:27
 * @update 2020/12/7 15:27
 **/
public enum DataSourceEnum {

    MYSQL("db-mysql"),
    ORACLE("db-oracle"),
    HIVE("db-hive");

    private String value;

    DataSourceEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
