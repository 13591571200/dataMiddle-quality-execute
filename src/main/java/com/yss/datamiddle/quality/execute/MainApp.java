package com.yss.datamiddle.quality.execute;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.yss.datamiddle.quality.execute.core.entity.CheckRuleInnerConfigPO;
import com.yss.datamiddle.quality.execute.core.entity.CheckRulePO;
import com.yss.datamiddle.quality.execute.core.enums.CheckRuleCheckLevelEnum;
import com.yss.datamiddle.quality.execute.core.util.ThresholdCompareUtil;
import com.yss.datamiddle.quality.execute.log.Log;
import com.yss.datamiddle.quality.execute.message.service.MessageServiceImpl;
import com.yss.datamiddle.quality.execute.message.service.dto.SendMessageDTO;
import com.yss.datamiddle.quality.execute.scheduler.entity.ProcessInstanceEntity;
import com.yss.datamiddle.quality.execute.service.ScriptService;
import com.yss.datamiddle.quality.execute.util.SqlExecuteUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiafupeng
 * @desc 执行入口
 * @create 2020/12/7 14:31
 * @update 2020/12/7 14:31
 **/
public class MainApp {

    public static void main(String[] args) {
//        String QualityConnId = args[0];
//        System.out.println("param[0] => " + QualityConnId);
//        String checkRuleInnerConfigId = args[1];
//        System.out.println("param[1] => " + checkRuleInnerConfigId);
//        String projectName = args[2];
//        System.out.println("param[2] => " + projectName);
//        entrance(QualityConnId , checkRuleInnerConfigId, projectName);
        entrance("205","467", "dataMiddleQuality");
    }

    private static void entrance(String QualityConnId, String checkRuleInnerConfigId, String projectName){
        LocalDateTime start00 = LocalDateTime.now();
        Log.info("[开始] - 检查规则数据库连接id: {0}, 检查规则子规则id: {1}", QualityConnId, checkRuleInnerConfigId);
        String checkRuleInnerSql = MessageFormat.format("select * from t_check_rule_inner_config where id = {0}", checkRuleInnerConfigId);
        Log.info("[SQL执行开始] - 查询检查规则子规则sql: {0}", checkRuleInnerSql);

        LocalDateTime start0 = LocalDateTime.now();
        CheckRuleInnerConfigPO checkRuleInnerConfigPO = SqlExecuteUtil.selectOneRow(QualityConnId, checkRuleInnerSql, CheckRuleInnerConfigPO.class);
        Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start0, LocalDateTime.now()).getSeconds()));
        Integer dataConnId = checkRuleInnerConfigPO.getDataConnId();
        Long checkRuleId = checkRuleInnerConfigPO.getCheckRuleId();
        Integer checkLevel = checkRuleInnerConfigPO.getCheckLevel();
        Log.info("[信息] - 检查规则子规则数据连接id: {0}, 检查规则id: {1}, 校验级别: {2}", dataConnId.toString(), checkRuleId.toString(), CheckRuleCheckLevelEnum.getDescByCode(checkLevel));

        String checkRuleSql = MessageFormat.format("select * from t_check_rule where id = {0}", checkRuleId);
        Log.info("[SQL执行开始] - 查询检查规则sql: {0}", checkRuleSql);

        LocalDateTime start1 = LocalDateTime.now();
        CheckRulePO checkRulePO = SqlExecuteUtil.selectOneRow(QualityConnId, checkRuleSql, CheckRulePO.class);
        Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start1, LocalDateTime.now()).getSeconds()));
        Integer processId = checkRulePO.getProcessId();
        Log.info("[信息] - 调度平台工作流id: {0}", processId.toString());

        ProcessInstanceEntity lastProcessInstance = getLastProcessInstance(processId, projectName);
        Log.info("[信息] - 工作流实例id:{0}, 工作流名称:{1}", lastProcessInstance.getId().toString(), lastProcessInstance.getName());

        List<SendMessageDTO> sendMessageList = Lists.newArrayList();
        ScriptService scriptService = new ScriptService();

        if(CheckRuleCheckLevelEnum.TABLE.getCode() == checkLevel || CheckRuleCheckLevelEnum.FIELD.getCode() == checkLevel){
            Log.info("[信息] - 校验级别: {0}", CheckRuleCheckLevelEnum.getDescByCode(checkLevel));
            Map<String, Map<String, String>> rootMap = JSON.parseObject(scriptService.getScriptJson(QualityConnId, checkRuleInnerConfigPO), Map.class);
            Log.info("[信息] - 检查规则子规则数量: {0}", String.valueOf(rootMap.size()));
            AtomicInteger index = new AtomicInteger();
            rootMap.entrySet().stream().forEach(o -> {
                String checkObjectName = o.getKey();
                Log.info("[信息] - <======================= 检查开始 {0}/{1} - 检查对象名称: {2} =======================>", String.valueOf(index.incrementAndGet()),String.valueOf(rootMap.size()),checkObjectName);
                Map<String, String> map = o.getValue();
                String errorDataJson = StringUtils.EMPTY;
                String checkSql = map.get("checkSql");
                Log.info("[SQL执行开始] - 检查sql: {0}", checkSql);
                LocalDateTime start2 = LocalDateTime.now();
                Long checkValue = SqlExecuteUtil.selectOne(dataConnId.toString(), checkSql);
                Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start2, LocalDateTime.now()).getSeconds()));
                Log.info("[信息] - 检查结果value: {0}", checkValue.toString());
                boolean checkWarn = ifTriggerWarn(checkRuleInnerConfigPO.getWarnType(), checkRuleInnerConfigPO.getThresholdCompareType(), checkRuleInnerConfigPO.getThreshold(), checkRuleInnerConfigPO.getWarnExpression(), checkValue);
                Log.info("[信息] - 是否告警: {0}", String.valueOf(checkWarn));
                if(checkWarn && checkRuleInnerConfigPO.getSaveErrorDataType() == 1){
                    Log.info("[信息] - 保存错误数据");
                    String checkErrorSql = map.get("checkErrorSql");
                    Log.info("[SQL执行开始] - 查询错误数据sql: {0}", checkErrorSql);
                    LocalDateTime start21 = LocalDateTime.now();
                    List<Map<String, Object>> errorDataMapList = SqlExecuteUtil.selectAll(dataConnId.toString(), checkErrorSql);
                    Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start21, LocalDateTime.now()).getSeconds()));
                    Log.info("[信息] - 查询错误数据数量: {0}", String.valueOf(errorDataMapList.size()));
                    ArrayList<Map<String, Object>> errorDataJsonTmpList = Lists.newArrayList();

                    for (int i = 0; i < checkRuleInnerConfigPO.getErrorDataCount() && i < errorDataMapList.size(); i++) {
                        errorDataJsonTmpList.add(errorDataMapList.get(i));
                        String errorDataJsonStr = JSON.toJSONString(errorDataJsonTmpList, SerializerFeature.WriteMapNullValue);
                        if(errorDataJsonStr.length() > 10000){
                            break;
                        }
                        errorDataJson = errorDataJsonStr;
                    }

                    sendMessageList.add(SendMessageDTO.builder()
                            .checkObjectName(checkObjectName)
                            .checkResultLong(checkValue)
                            .checkSql(checkSql)
                            .warnType(checkRuleInnerConfigPO.getWarnType())
                            .thresholdCompareType(checkRuleInnerConfigPO.getThresholdCompareType())
                            .threshold(checkRuleInnerConfigPO.getThreshold())
                            .build());
                }

                String totalCountSql = map.get("totalCountSql");
                Log.info("[SQL执行开始] - 检查总数sql: {0}", totalCountSql);
                LocalDateTime start3 = LocalDateTime.now();
                Long checkTotalNum = SqlExecuteUtil.selectOne(dataConnId.toString(), totalCountSql);
                Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start3, LocalDateTime.now()).getSeconds()));
                Log.info("[获取信息] - 检查总数: {0}", checkTotalNum.toString());

                String insertResultSql = map.get("insertResultSql");
                String insertSql = MessageFormat.format(insertResultSql, lastProcessInstance.getId().toString(), lastProcessInstance.getName(), checkRuleId.toString(), checkRuleInnerConfigId, checkObjectName, checkWarn ? 1 : 0, checkValue.toString(), checkTotalNum.toString(), errorDataJson);
                Log.info("[SQL执行开始] - 结果数据持久化");
                LocalDateTime start4 = LocalDateTime.now();
                SqlExecuteUtil.insert(QualityConnId, insertSql);
                Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start4, LocalDateTime.now()).getSeconds()));
            });
        }

        if(CheckRuleCheckLevelEnum.CUSTOM.getCode() == checkLevel){
            Log.info("[信息] - 校验级别: {0}", CheckRuleCheckLevelEnum.getDescByCode(checkLevel));
            Log.info("[信息] - <======================= 检查开始 - 自定义对象 =======================>");
            Map<String, Object> map = JSON.parseObject(scriptService.getScriptJson(QualityConnId, checkRuleInnerConfigPO), Map.class);
            String checkSql = map.get("checkSql").toString();
            Log.info("[SQL执行开始] - 检查sql: {0}", checkSql);
            LocalDateTime start5 = LocalDateTime.now();
            Long checkValue = SqlExecuteUtil.selectOne(dataConnId.toString(), checkSql);
            Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start5, LocalDateTime.now()).getSeconds()));
            Log.info("[信息] - 检查结果value: {0}", checkValue.toString());
            boolean checkWarn = ifTriggerWarn(checkRuleInnerConfigPO.getWarnType(), checkRuleInnerConfigPO.getThresholdCompareType(), checkRuleInnerConfigPO.getThreshold(), checkRuleInnerConfigPO.getWarnExpression(), checkValue);
            Log.info("[信息] - 是否告警: {0}", String.valueOf(checkWarn));
            if(checkWarn){
                sendMessageList.add(SendMessageDTO.builder()
                        .checkObjectName("default")
                        .checkResultLong(checkValue)
                        .checkSql(checkSql)
                        .warnType(checkRuleInnerConfigPO.getWarnType())
                        .checkExpression(checkRuleInnerConfigPO.getWarnExpression())
                        .build());
            }

            String insertResultSql = map.get("insertResultSql").toString();
            String insertSql = MessageFormat.format(insertResultSql, lastProcessInstance.getId().toString(), lastProcessInstance.getName(), checkRuleId.toString(), checkRuleInnerConfigId, "default", checkWarn ? 1 : 0, checkValue.toString(), 0, null);
            Log.info("[SQL执行开始] - 结果数据持久化");
            LocalDateTime start6 = LocalDateTime.now();
            SqlExecuteUtil.insert(QualityConnId, insertSql);
            Log.info("[SQL执行结束] - 耗时{0}秒", String.valueOf(Duration.between(start6, LocalDateTime.now()).getSeconds()));
        }

        try {
            if(checkRulePO.getNotifyType().intValue() == 1 &&  !CollectionUtils.isEmpty(sendMessageList)){
                Log.info("[邮件通知] - 通知主题id: {0}", checkRulePO.getNotifyTopicId().toString());
                new MessageServiceImpl().send(checkRulePO.getNotifyTopicId(), checkRulePO.getName(), checkRuleInnerConfigPO.getName(), sendMessageList);
                Log.info("[邮件通知] - 成功");
            }
        } catch (Exception e){
            Log.warn("[邮件通知] - 失败");
        }

        Log.info("[结束] - 总耗时{0}秒", String.valueOf(Duration.between(start00, LocalDateTime.now()).getSeconds()));
    }

    // 是否触发告警
    private static boolean ifTriggerWarn(Integer warnType, Integer thresholdCompareType, Integer Threshold, String WarnExpression, long checkValue){
        if(warnType == 1 && ThresholdCompareUtil.compare(thresholdCompareType, checkValue, Threshold)){
            return true;
        }

        if(warnType == 2 && ThresholdCompareUtil.compare(WarnExpression, checkValue)){
            return true;
        }

        return false;
    }

    private static ProcessInstanceEntity getLastProcessInstance(int processDefinitionId, String projectName){
        String url = MessageFormat.format("http://192.168.100.108:8010/api/scheduler/v2/projects/{0}/instance/list-paging?pageSize=1&pageNo=1&processDefinitionId={1}", projectName, String.valueOf(processDefinitionId));
        Log.info("[URL访问中] - 获取最新工作流实例信息URL: {0}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJkYXRhUGVybXMiOm51bGwsInRlbmFudE5hbWUiOiJoZW5naGUiLCJ1c2VyX25hbWUiOiJqaWFmdXBlbmciLCJzY29wZSI6WyJhcHAiXSwidGVuYW50SWQiOjEsInVzZXJUeXBlIjoiR0VORVJBTF9VU0VSIiwidGVuYW50Q29kZSI6ImhlbmdoZSIsInVzZXJOYW1lIjoiamlhZnVwZW5nIiwidXNlcklkIjoxMSwiYXV0aG9yaXRpZXMiOlsiYW5hbHlzaXM6dmFyaWFibGU6cmVtb3ZlIiwic3lzdGVtOnVzZXI6cmVzZXRQd2QiLCJhbmFseXNpczpwcm9qZWN0OmVkaXQiLCJhdGxhcyIsInN5c3RlbTptZW51Omxpc3QiLCJ6ZXBwZWxpbjpyZWFkIiwiYW5hbHlzaXM6ZGV2ZWxvcDpzdGF0dXMiLCJwcmVzdG86cmVhZCIsInJhbmdlcjp3cml0ZSIsImFuYWx5c2lzOnByb2plY3Q6c2NoZWR1bGUiLCJhbmFseXNpczpzeWM6bGlzdCIsInNwYXJrOndyaXRlIiwiYW5hbHlzaXM6dmFyaWFibGU6ZWRpdCIsImFuYWx5c2lzOmRldmVsb3A6cHVibGlzaCIsInN5c3RlbTp0ZW5hbnQ6YWRkIiwiZWxhc3RpY3NlYXJjaCIsImthZmthOnJlYWQiLCJmbGluazpyZWFkIiwiaGl2ZTp3cml0ZSIsImFuYWx5c2lzOmRldmVsb3A6cmVtb3ZlIiwic3lzdGVtOnVzZXI6Y2hhbmdlU3RhdHVzIiwiYW5hbHlzaXM6ZGltZW5zaW9uOmFkZCIsImFuYWx5c2lzOnByb2plY3Q6cmVtb3ZlIiwiYW5hbHlzaXM6c3ljOmFkZCIsInN5c3RlbTpkYXRhc291cmNlOnJlbW92ZSIsInpvb2tlZXBlciIsImthZmthbWFuYWdlciIsInNjaGVtYTp3cml0ZSIsInN5c3RlbTppbmRleDpnZXRQcm9qZWN0QnlQbGF0Zm9ybSIsInN5c3RlbTp0ZW5hbnQ6cmVtb3ZlIiwiYW5hbHlzaXM6bWFwcGluZzpsaXN0IiwiYW5hbHlzaXM6cm9vdDphZGQiLCJtZWR1c2E6d3JpdGUiLCJzeXN0ZW06cmVzb3VyY2U6bGlzdCIsImFuYWx5c2lzOnRhYmxlOmxpc3QiLCJhbmFseXNpczp0ZW1wbGF0ZTpsaXN0Iiwia2Fma2EiLCJ6b29rZWVwZXI6d3JpdGUiLCJmbHVtZTpyZWFkIiwic3Fvb3A6d3JpdGUiLCJzeXN0ZW06dWRmOnJlbW92ZSIsImZsdW1lOndyaXRlIiwicHJlc3RvIiwic3lzdGVtOnJlc291cmNlOmFkZCIsImFuYWx5c2lzOnN5YzpwdWJsaXNoIiwiaGl2ZSIsInNjaGVtYSIsInlhcm46cmVhZCIsInN5c3RlbTp0ZW5hbnQ6bGlzdCIsInN5c3RlbTpkYXRhc291cmNlOmFkZCIsImthZmthbWFuYWdlcjpyZWFkIiwic3lzdGVtOmluZGV4OmdldFNjaGVkdWxlU3RhdCIsInN5c3RlbTppbmRleDpnZXRPcGVyYXRpb25zU3RhdCIsImtlcmJlcm9zOndyaXRlIiwia2VyYmVyb3MiLCJzeXN0ZW06dXNlcjplZGl0Iiwic3lzdGVtOnJvbGU6ZWRpdCIsImthZmthOndyaXRlIiwic3lzdGVtOmRhdGFzb3VyY2U6ZWRpdCIsImFuYWx5c2lzOnJvb3Q6bGlzdCIsInNwYXJrIiwiYW5hbHlzaXM6dGFibGU6c3RhdHVzIiwiYW5hbHlzaXM6dGhlbWU6YWRkIiwiZGF0YXg6d3JpdGUiLCJhbmFseXNpczp0ZW1wbGF0ZTplZGl0Iiwic3lzdGVtOnJvbGU6YWRkIiwiYW5hbHlzaXM6cGlwZWxpbmU6ZWRpdCIsInNvbHI6cmVhZCIsImFuYWx5c2lzOmRldmVsb3A6ZWRpdCIsInN5c3RlbTpkZXB0OmVkaXQiLCJhbmFseXNpczpyb290OmltcG9ydCIsInpvb2tlZXBlcjpyZWFkIiwiaGl2ZTpyZWFkIiwic3lzdGVtOnVkZjpsaXN0IiwiYW5hbHlzaXM6cm9vdDpleHBvcnQiLCJ6ZXBwZWxpbiIsImthZmthbWFuYWdlcjp3cml0ZSIsInJhbmdlcjpyZWFkIiwic3lzdGVtOnJlc291cmNlOnJlbW92ZSIsInNjaGVtYTpyZWFkIiwic3lzOmFkbWluIiwiYXRsYXM6d3JpdGUiLCJ5YXJuIiwiaGRmczpyZWFkIiwic3lzdGVtOmRlcHQ6cmVtb3ZlIiwiZWxhc3RpY3NlYXJjaDp3cml0ZSIsImFuYWx5c2lzOnBpcGVsaW5lOmFkZCIsImFuYWx5c2lzOnN5YzpyZW1vdmUiLCJtZWR1c2EiLCJhbmFseXNpczp0aGVtZTpsaXN0IiwiZGF0YXg6cmVhZCIsImFuYWx5c2lzOnRhYmxlOmVkaXQiLCJwcmVzdG86d3JpdGUiLCJzeXN0ZW06dXNlcjphZGQiLCJhbmFseXNpczpwcm9qZWN0Omxpc3QiLCJhdGxhczpyZWFkIiwiYW5hbHlzaXM6c3ljOnN0YXR1cyIsImFuYWx5c2lzOnN5YzplZGl0IiwiYW5hbHlzaXM6dmFyaWFibGU6bGlzdCIsImFuYWx5c2lzOm1hcHBpbmc6ZWRpdCIsImZsdW1lIiwiYW5hbHlzaXM6cm9vdDpyZW1vdmUiLCJodWU6d3JpdGUiLCJ6ZXBwZWxpbjp3cml0ZSIsInN5c3RlbTppbmRleDpnZXRBbmFseXNpc1N0YXQiLCJoZGZzOndyaXRlIiwiZGF0YXgiLCJkb2xwaGluIiwiYW5hbHlzaXM6dGFibGU6YWRkIiwic3lzdGVtOnJlc291cmNlOmVkaXQiLCJmbGluazp3cml0ZSIsInN5c3RlbTp1ZGY6YWRkIiwiYW5hbHlzaXM6dGFibGU6cmVtb3ZlIiwiaHVlOnJlYWQiLCJzb2xyOndyaXRlIiwic3lzOmhvc3QiLCJzeXN0ZW06cm9sZTpjaGFuZ2VTdGF0dXMiLCJhbmFseXNpczpwaXBlbGluZTpzdGF0dXMiLCJtZWR1c2E6cmVhZCIsImZsaW5rIiwiaGJhc2U6cmVhZCIsInNxb29wOnJlYWQiLCJhbmFseXNpczpkaW1lbnNpb246cmVtb3ZlIiwiYW5hbHlzaXM6dGVtcGxhdGU6cmVtb3ZlIiwiYW5hbHlzaXM6ZGltZW5zaW9uOmVkaXQiLCJhbmFseXNpczp0aGVtZTpyZW1vdmUiLCJ5YXJuOndyaXRlIiwiYW5hbHlzaXM6cGlwZWxpbmU6cmVtb3ZlIiwiYW5hbHlzaXM6cGlwZWxpbmU6bGlzdCIsInN5c3RlbTp1c2VyOnJlbW92ZSIsInJhbmdlciIsInN5c3RlbTpyb2xlOmxpc3QiLCJzeXN0ZW06ZGVwdDpjaGFuZ2VTdGF0dXMiLCJkb2xwaGluOnJlYWQiLCJhbmFseXNpczpyb290OmVkaXQiLCJzcGFyazpyZWFkIiwic3lzdGVtOmRlcHQ6bGlzdCIsInN5c3RlbTp1ZGY6ZWRpdCIsImFuYWx5c2lzOnByb2plY3Q6c3RhdHVzIiwic29sciIsImFuYWx5c2lzOmRpbWVuc2lvbjpsaXN0Iiwia2VyYmVyb3M6cmVhZCIsImhiYXNlIiwiYW5hbHlzaXM6cHJvamVjdDphZGQiLCJkb2xwaGluOndyaXRlIiwic3lzdGVtOmRhdGFzb3VyY2U6bGlzdCIsInN5c3RlbTp1c2VyOmxpc3QiLCJhbmFseXNpczptYXBwaW5nOnJlbW92ZSIsInN5c3RlbTppbmRleDpnZXREZXZlbG9wU3RhdCIsInN5c3RlbTp0ZW5hbnQ6ZWRpdCIsImFuYWx5c2lzOmRldmVsb3A6bGlzdCIsImFuYWx5c2lzOnZhcmlhYmxlOmFkZCIsImhiYXNlOndyaXRlIiwiYW5hbHlzaXM6ZGV2ZWxvcDphZGQiLCJhbmFseXNpczptYXBwaW5nOmFkZCIsImFuYWx5c2lzOnRhYmxlOnB1Ymxpc2giLCJoZGZzIiwiaHVlIiwiYW5hbHlzaXM6dGhlbWU6ZWRpdCIsInNxb29wIiwiZWxhc3RpY3NlYXJjaDpyZWFkIiwiYW5hbHlzaXM6cGlwZWxpbmU6cHVibGlzaCIsInN5czptYW5hZ2VyIiwiYW5hbHlzaXM6dGVtcGxhdGU6YWRkIiwic3lzdGVtOmRlcHQ6YWRkIl0sImp0aSI6ImVjNjg2MDkxLTIwMWUtNDQwNy1iZmE3LWM5NzI5NmY3NWY5ZiIsImNsaWVudF9pZCI6IlhjV2ViQXBwIn0.LysOCdW6hiNrEmPyVEWKcVUnE3jrrJVLbSBFp02rzGyNrHf8JhDdDL_JzObzvKOMFII51o73vFx86ABA1uME4eM8PfXfLaD9D57RPz6XakkV_y1PhUqGslRDBoiS04DHUT65mFTfPgWoFcoiJ_BGPg1h1GFiYEIeekMrGbSjxzp294zNJAykavEFr6kpl2iUVI8rg8uIBTLYVkUXSfP9nVye83QmC4XA8Vk8yWoW0tyBIkdqdeZWtcAUUEQ05JYznSjpOY00kKIAKBx33wzIu_jRz9LT4NyXqwE7uU5F_xn4j2HuOw5tbO0Aul8-9G4Jelf_z61PF58WKsmVtbN1bw");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
        LocalDateTime start7 = LocalDateTime.now();
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        Log.info("[URL访问结束] - 耗时{0}秒", String.valueOf(Duration.between(start7, LocalDateTime.now()).getSeconds()));
        String body = exchange.getBody();
        Log.info("[信息] - responseBody: {0}", body);
        Map map = JSON.parseObject(body, Map.class);
        Map data = (Map)map.get("data");
        List list = (List)data.get("list");
        Map o = (Map)list.get(0);
        return ProcessInstanceEntity.builder()
                .id(Integer.parseInt(o.get("id").toString()))
                .name(o.get("name").toString())
                .build();
    }
}
