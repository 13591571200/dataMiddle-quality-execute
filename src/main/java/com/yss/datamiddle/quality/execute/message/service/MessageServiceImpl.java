package com.yss.datamiddle.quality.execute.message.service;

import com.yss.datamiddle.enums.CommonEnum;
import com.yss.datamiddle.message.common.RetResult;
import com.yss.datamiddle.message.dto.message.PublishThemeDto;
import com.yss.datamiddle.message.rest.MsSdkMessageRest;
import com.yss.datamiddle.message.rest.impl.MsSdkMessageRestImpl;
import com.yss.datamiddle.quality.execute.core.util.ThresholdCompareUtil;
import com.yss.datamiddle.quality.execute.message.service.dto.SendMessageDTO;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * @author jiafupeng
 * @desc
 * @create 2020/12/30 15:11
 * @update 2020/12/30 15:11
 **/
public class MessageServiceImpl {

    public void send(Long themeId, String checkRuleName, String checkRuleInnerName, List<SendMessageDTO> sendMessageList){
        String messageName = MessageFormat.format("数据质量-检查结果告警 {0}", LocalDate.now().toString());
        StringBuilder messageContentStringBuilder = new StringBuilder();
        messageContentStringBuilder.append(MessageFormat.format("检查规则名称: {0} \r\n", checkRuleName));
        messageContentStringBuilder.append(MessageFormat.format("检查规则子规则名称: {0} \r\n", checkRuleInnerName));

        sendMessageList.stream().forEach(o -> {
            messageContentStringBuilder.append("\r\n");
            messageContentStringBuilder.append(MessageFormat.format("===> 检查规则字段: {0} \r\n", o.getCheckObjectName()));
            messageContentStringBuilder.append(MessageFormat.format("|-检查sql: {0} \r\n", o.getCheckSql()));
            messageContentStringBuilder.append(MessageFormat.format("|-检查结果: {0} \r\n", o.getCheckResultLong()));
            if(o.getWarnType() == 1){
                messageContentStringBuilder.append(MessageFormat.format("|-比较条件: {0} \r\n", ThresholdCompareUtil.getExpression(o.getThresholdCompareType(), o.getThreshold())));
            }

            if(o.getWarnType() == 2){
                messageContentStringBuilder.append(MessageFormat.format("|-比较条件: {0} \r\n", o.getCheckExpression()));
            }
        });

        doSend(themeId, messageName, messageContentStringBuilder.toString());
    }

    private void doSend(Long themeId, String messageName, String messageContent){
        MsSdkMessageRest msSdkMessageRest = new MsSdkMessageRestImpl();
        PublishThemeDto publishThemeDto = new PublishThemeDto();
        publishThemeDto.setHost("192.168.100.163:30250");
        publishThemeDto.setThemeId(themeId);
        publishThemeDto.setMessageType((byte)0);
        publishThemeDto.setMessageName(messageName);
        publishThemeDto.setMessageContent(messageContent);
        RetResult retResult = msSdkMessageRest.publishThemeMessage(publishThemeDto);
        System.out.println(retResult);
    }
}
