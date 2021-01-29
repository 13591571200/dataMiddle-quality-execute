package com.yss.datamiddle.quality.execute.datasource.entity;

import lombok.Data;

/**
 * @author jiafupeng
 * @desc 数据库连接实体类
 * @create 2020/12/7 15:40
 * @update 2020/12/7 15:40
 **/
@Data
public class DataSourceEntity {

    private String hostName;
    private String port;
    private String dbName;
    private String username;
    private String password;
    private String connType;
}
