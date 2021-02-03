package com.yss.datamiddle.quality.execute.service;

import java.text.MessageFormat;

/**
 * @author jiafupeng
 * @desc 拼接完整Sql
 * @create 2020/12/11 15:29
 * @update 2020/12/11 15:29
 **/
public class JointFullSqlHandle {

    private SqlReplaceHandle sqlReplaceHandle = new SqlReplaceHandle();

    public String getFullTableSql(String checkTemplateSql, String tableName, int scannerType, String scannerAreaSql){
        return getFullSql(sqlReplaceHandle.getReplaceSqlTableOne(checkTemplateSql, tableName), scannerType, scannerAreaSql);
    }

    public String getFullColumnSql(String checkTemplateSql, String tableName, String columnName, int scannerType, String scannerAreaSql){
        return getFullSql(sqlReplaceHandle.getReplaceSqlColumnOne(checkTemplateSql, tableName, columnName), scannerType, scannerAreaSql);
    }

    public String getFullCustomSql(String customSql, int scannerType, String scannerAreaSql){
        return getFullSql(customSql, scannerType, scannerAreaSql);
    }

    private String getFullSql(String sql, int scannerType, String scannerAreaSql){
        if (scannerType == ScannerTypeEnum.ALL.getValue()) {
            return sql;
        }

        if (scannerType == ScannerTypeEnum.CONDITION.getValue()) {
            return sql + " " + scannerAreaSql;
        }

        throw new RuntimeException(MessageFormat.format("异常的扫描范围: {0}", scannerType));
    }
}
