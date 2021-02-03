package com.yss.datamiddle.quality.execute.service;

import org.springframework.stereotype.Component;

/**
 * @author jiafupeng
 * @desc 检查规则-sql替换处理类
 * @create 2020/12/8 14:30
 * @update 2020/12/8 14:30
 **/
@Component
public class SqlReplaceHandle {

    private static final String TABLE = "${table}";
    private static final String COLUMN = "${column}";

    public String getReplaceSqlColumnOne(String templateSql, String tableName, String columnName){
        return getReplaceSqlOne(getReplaceSqlTableOne(templateSql, tableName), COLUMN, columnName);
    }

    public String getReplaceSqlTableOne(String templateSql, String tableName){
        return getReplaceSqlOne(templateSql, TABLE, tableName);
    }

    private String getReplaceSqlOne(String templateSql, String target, String replacement){
        return templateSql.replace(target, replacement);
    }
}
