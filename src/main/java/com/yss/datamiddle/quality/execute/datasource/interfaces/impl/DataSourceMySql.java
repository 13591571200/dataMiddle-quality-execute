package com.yss.datamiddle.quality.execute.datasource.interfaces.impl;

import com.yss.datamiddle.quality.execute.datasource.interfaces.BaseDataSourceEngine;

import java.text.MessageFormat;

/**
 * @author jiafupeng
 * @desc MySql连接
 * @create 2020/12/7 15:24
 * @update 2020/12/7 15:24
 **/
public class DataSourceMySql extends BaseDataSourceEngine {

    private final static String MYSQL_URL_FORMAT = "jdbc:mysql://{0}:{1}/{2}?serverTimezone=GMT%2b8&useUnicode=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatements=true";

    private final static String MYSQL_DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    @Override
    protected String getUrl(String ip, String port, String database) {
        return MessageFormat.format(MYSQL_URL_FORMAT, ip, port, database);
    }

    @Override
    protected String getDriverClass() {
        return MYSQL_DRIVER_CLASS;
    }
}
