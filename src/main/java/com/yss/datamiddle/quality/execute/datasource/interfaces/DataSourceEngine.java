package com.yss.datamiddle.quality.execute.datasource.interfaces;

import com.yss.datamiddle.quality.execute.datasource.entity.DataSourceEntity;

import javax.sql.DataSource;

/**
 * @author jiafupeng
 * @desc 基础连接
 * @create 2020/12/7 15:24
 * @update 2020/12/7 15:24
 **/
public interface DataSourceEngine {

    DataSource getDataSource(DataSourceEntity connEntity);
}
