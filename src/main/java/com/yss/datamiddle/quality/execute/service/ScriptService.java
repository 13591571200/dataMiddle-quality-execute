package com.yss.datamiddle.quality.execute.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yss.datamiddle.DataSource;
import com.yss.datamiddle.enums.CommonEnum;
import com.yss.datamiddle.quality.execute.core.entity.CheckRuleInnerConfigPO;
import com.yss.datamiddle.quality.execute.datasource.entity.DataSourceEntity;
import com.yss.datamiddle.quality.execute.util.SqlExecuteUtil;
import com.yss.datamiddle.vo.DataConnVO;

import java.text.MessageFormat;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jiafupeng
 * @desc
 * @create 2021/1/29 10:14
 * @update 2021/1/29 10:14
 **/
public class ScriptService {

    private ScheduleScriptHandle scheduleScriptHandle = new ScheduleScriptHandle();
    private JointFullSqlHandle jointFullSqlHandle = new JointFullSqlHandle();
    private DataSource dataSource = new DataSource();
    private static int ERROR_DATA_COUNT  = 1000;

    public String getScriptJson(String QualityConnId, CheckRuleInnerConfigPO checkRuleInnerConfigPO){
        String userId = checkRuleInnerConfigPO.getUpdateUserid();
        Integer checkLevel = checkRuleInnerConfigPO.getCheckLevel();
        DataSourceEntity dataSourceEntity = JSON.parseObject(dataSource.getConn(checkRuleInnerConfigPO.getDataConnId().toString()), DataSourceEntity.class);
        if(CheckRuleCheckLevelEnum.TABLE.getCode() == checkLevel){
            CheckTemplateVo checkTemplateVo = SqlExecuteUtil.selectOneRow(QualityConnId, "select * from t_check_template where id = " + checkRuleInnerConfigPO.getCheckTemplateId(), CheckTemplateVo.class);
            String checkTemplateSql = checkTemplateVo.getTempSql();
            Map<String, Map<String, Object>> collect = JSON.parseArray(checkRuleInnerConfigPO.getCheckTableNameList(), String.class).stream().collect(Collectors.toMap(o -> o, o -> {
                String fullSql = jointFullSqlHandle.getFullTableSql(checkTemplateSql, o, checkRuleInnerConfigPO.getScannerType(), checkRuleInnerConfigPO.getScannerAreaSql());
                Map<String, Object> sqlMap = Maps.newHashMap();
                sqlMap.put("checkSql", fullSql);
                sqlMap.put("checkErrorSql", scheduleScriptHandle.getCheckErrorSql(fullSql, dataSourceEntity.getConnType(), ERROR_DATA_COUNT));
                sqlMap.put("totalCountSql", scheduleScriptHandle.getTotalCountSql(fullSql));
                sqlMap.put("insertResultSql", scheduleScriptHandle.getCheckResultSql(userId));
                return sqlMap;
            }));
            return JSON.toJSONString(collect);
        }

        if(CheckRuleCheckLevelEnum.FIELD.getCode() == checkLevel){
            CheckTemplateVo checkTemplateVo = SqlExecuteUtil.selectOneRow(QualityConnId, "select * from t_check_template where id = " + checkRuleInnerConfigPO.getCheckTemplateId(), CheckTemplateVo.class);
            String checkTemplateSql = checkTemplateVo.getTempSql();
            Map<Object, Map<String, Object>> collect = IntStream.range(0, JSON.parseArray(checkRuleInnerConfigPO.getCheckColumnNameList(), String.class).size()).mapToObj(index -> {
                String tableName = JSON.parseArray(checkRuleInnerConfigPO.getCheckTableNameList(), String.class).get(index);
                String columnName = JSON.parseArray(checkRuleInnerConfigPO.getCheckColumnNameList(), String.class).get(index);
                String name = tableName + "." + columnName;
                Map<String, Object> map = Maps.newHashMap();
                map.put("joinName", name);
                map.put("tableName", tableName);
                map.put("columnName", columnName);
                return map;
            }).collect(Collectors.toList()).stream().collect(Collectors.toMap(o -> o.get("joinName"), o -> {
                String fullSql = jointFullSqlHandle.getFullColumnSql(checkTemplateSql, o.get("tableName").toString(), o.get("columnName").toString(), checkRuleInnerConfigPO.getScannerType(), checkRuleInnerConfigPO.getScannerAreaSql());
                Map<String, Object> sqlMap = Maps.newHashMap();
                sqlMap.put("checkSql", fullSql);
                sqlMap.put("checkErrorSql", scheduleScriptHandle.getCheckErrorSql(fullSql, dataSourceEntity.getConnType(), ERROR_DATA_COUNT));
                sqlMap.put("totalCountSql", scheduleScriptHandle.getTotalCountSql(fullSql));
                sqlMap.put("insertResultSql", scheduleScriptHandle.getCheckResultSql(userId));
                return sqlMap;
            }));
            return JSON.toJSONString(collect);
        }

        if(CheckRuleCheckLevelEnum.CUSTOM.getCode() == checkLevel){
            String replaceSql = checkRuleInnerConfigPO.getCustomSql();
            String fullSql = jointFullSqlHandle.getFullCustomSql(replaceSql, checkRuleInnerConfigPO.getScannerType(), checkRuleInnerConfigPO.getScannerAreaSql());
            Map<String, Object> sqlMap = Maps.newHashMap();
            sqlMap.put("checkSql", fullSql);
            sqlMap.put("insertResultSql", scheduleScriptHandle.getCheckResultSql(userId));
            return JSON.toJSONString(sqlMap);
        }

        throw new RuntimeException();
    }
}
