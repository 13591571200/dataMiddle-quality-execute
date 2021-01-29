package com.yss.datamiddle.quality.execute.datasource.interfaces;

import com.yss.datamiddle.quality.execute.datasource.entity.DataSourceEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author jiafupeng
 * @desc 抽象数据连接
 * @create 2020/12/7 19:57
 * @update 2020/12/7 19:57
 **/
public abstract class BaseDataSourceEngine implements DataSourceEngine {

    public DataSource getDataSource(DataSourceEntity connEntity){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(getUrl(connEntity.getHostName(), connEntity.getPort(), connEntity.getDbName()));
        dataSource.setDriverClassName(getDriverClass());
        dataSource.setUsername(connEntity.getUsername());
        dataSource.setPassword(connEntity.getPassword());
        return dataSource;
    }

    protected abstract String getUrl(String ip, String port, String database);

    protected abstract String getDriverClass();
}
