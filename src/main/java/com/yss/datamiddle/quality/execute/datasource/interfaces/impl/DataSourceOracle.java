package com.yss.datamiddle.quality.execute.datasource.interfaces.impl;

import com.yss.datamiddle.quality.execute.datasource.interfaces.BaseDataSourceEngine;

import java.text.MessageFormat;

/**
 * @author jiafupeng
 * @desc Oracle连接
 * @create 2020/12/7 15:32
 * @update 2020/12/7 15:32
 **/
public class DataSourceOracle extends BaseDataSourceEngine {

    private final static String ORACLE_URL_FORMAT = "jdbc:oracle:thin:@//{0}:{1}/{2}";

    private final static String ORACLE_DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";

    @Override
    protected String getUrl(String ip, String port, String database) {
        return MessageFormat.format(ORACLE_URL_FORMAT, ip, port, database);
    }

    @Override
    protected String getDriverClass() {
        return ORACLE_DRIVER_CLASS;
    }
}
