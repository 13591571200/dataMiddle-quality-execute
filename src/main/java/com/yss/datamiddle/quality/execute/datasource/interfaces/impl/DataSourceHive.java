package com.yss.datamiddle.quality.execute.datasource.interfaces.impl;

import com.yss.datamiddle.quality.execute.datasource.interfaces.BaseDataSourceEngine;

import java.text.MessageFormat;

/**
 * @author jiafupeng
 * @desc Hive连接
 * @create 2020/12/7 15:33
 * @update 2020/12/7 15:33
 **/
public class DataSourceHive extends BaseDataSourceEngine {

    private final static String HIVE_URL_FORMAT = "jdbc:hive2://{0}:{1}/{2}";

    private final static String HIVE_DRIVER_CLASS = "org.apache.hive.jdbc.HiveDriver";

    @Override
    protected String getUrl(String ip, String port, String database) {
        return MessageFormat.format(HIVE_URL_FORMAT, ip, port, database);
    }

    @Override
    protected String getDriverClass() {
        return HIVE_DRIVER_CLASS;
    }
}
