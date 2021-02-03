package com.yss.datamiddle.quality.execute.service;

import org.springframework.stereotype.Component;

/**
 * @author jiafupeng
 * @desc   调度脚本处理类
 * @create 2020/12/11 14:50
 * @update 2020/12/11 14:50
 **/
@Component
public class ScheduleScriptHandle {

    private static final String SELECT = "select";
    private static final String FROM = "from";
    private static final String WHERE = "where";

    public String getCheckErrorSql(String checkSql, String connType, int errorDataCount){
        String select = checkSql.substring(0, checkSql.toLowerCase().indexOf(SELECT) + SELECT.length());
        String from = checkSql.substring(checkSql.toLowerCase().indexOf(FROM));
        String sql = select + " * " + from;
        if(ConnTypeEnum.ORACLE.getName().equals(connType)){
            if(from.toLowerCase().contains(WHERE)){
                sql += (" and ROWNUM <= " + errorDataCount);
            } else {
                sql += (" where ROWNUM <= " + errorDataCount);
            }
        }

        if(ConnTypeEnum.HIVE.getName().equals(connType)){
            sql += (" limit " + errorDataCount);
        }

        return sql;
    }

    public String getTotalCountSql(String checkSql){
        // 表 select count(1) from table
        // 列 select count(1) from table where column is null
        String select = checkSql.substring(0, checkSql.toLowerCase().indexOf(SELECT) + SELECT.length());
        String from = checkSql.substring(checkSql.toLowerCase().indexOf(FROM));
        if(from.contains(WHERE)){
            from = from.substring(0, from.indexOf(WHERE));
        }
        return select + " count(1) " + from;
    }

    public String getCheckResultSql(String userId){
        return "insert into t_check_result" +
                "(process_instance_id, process_instance_name, check_rule_id, check_rule_inner_id, check_rule_inner_object_name, check_warn, check_result_num, check_total_num, error_data_json,  create_time, update_time, create_userid, update_userid, del_flag, version_consumer)" +
                "values" +
                "({0}, ''{1}'', {2}, {3}, ''{4}'', {5}, {6}, {7}, ''{8}'', now(), now(), ''" + userId + "'', ''" + userId + "'', 0, null);";
    }
}
