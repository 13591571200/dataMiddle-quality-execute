package com.yss.datamiddle.quality.execute.util;

import com.alibaba.fastjson.JSON;
import com.yss.datamiddle.DataSource;
import com.yss.datamiddle.quality.execute.datasource.entity.DataSourceEntity;
import com.yss.datamiddle.quality.execute.datasource.util.DataSourceFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author jiafupeng
 * @desc Sql执行工具类
 * @create 2020/12/9 11:50
 * @update 2020/12/9 11:50
 **/
public class SqlExecuteUtil {

    public static Long selectOne(String connId, String sql){
        return getJdbcTemplate(connId).queryForObject(sql, Long.class);
    }

    public static <T> T selectOneRow(String connId, String sql, Class<T> tClass){
        RowMapper<T> rowMapper = new BeanPropertyRowMapper(tClass);
        List<T> list = getJdbcTemplate(connId).query(sql, rowMapper);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }

    public static List<Map<String, Object>> selectAll(String connId, String sql){
        return getJdbcTemplate(connId).queryForList(sql);
    }

    public static void insert(String connId, String sql){
        getJdbcTemplate(connId).execute(sql);
    }

    private static JdbcTemplate getJdbcTemplate(String connId){
        DataSource dataSource = new DataSource();
        DataSourceEntity dataSourceEntity = JSON.parseObject(dataSource.getConn(connId), DataSourceEntity.class);
        return new JdbcTemplate(DataSourceFactory.getDataSource(dataSourceEntity));
    }
}
