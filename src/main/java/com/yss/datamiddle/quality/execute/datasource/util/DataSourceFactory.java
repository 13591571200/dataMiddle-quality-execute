package com.yss.datamiddle.quality.execute.datasource.util;

import com.yss.datamiddle.quality.execute.datasource.entity.DataSourceEntity;
import com.yss.datamiddle.quality.execute.datasource.enums.DataSourceEnum;
import com.yss.datamiddle.quality.execute.datasource.interfaces.DataSourceEngine;
import com.yss.datamiddle.quality.execute.datasource.interfaces.impl.DataSourceHive;
import com.yss.datamiddle.quality.execute.datasource.interfaces.impl.DataSourceMySql;
import com.yss.datamiddle.quality.execute.datasource.interfaces.impl.DataSourceOracle;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author jiafupeng
 * @desc 连接工厂
 * @create 2020/12/7 15:25
 * @update 2020/12/7 15:25
 **/
public class DataSourceFactory {

    private static Map<String, Supplier<DataSourceEngine>> connMap = new HashMap<>();

    static {
        connMap.put(DataSourceEnum.MYSQL.getValue(), DataSourceMySql::new);
        connMap.put(DataSourceEnum.ORACLE.getValue(), DataSourceOracle::new);
        connMap.put(DataSourceEnum.HIVE.getValue(), DataSourceHive::new);
    }

    public static DataSource getDataSource(DataSourceEntity connEntity){
        DataSourceEngine dataSourceEngine = connMap.get(connEntity.getConnType()).get();
        return dataSourceEngine.getDataSource(connEntity);
    }
}
