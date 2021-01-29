package com.yss.datamiddle.quality.execute;

import com.google.common.collect.Lists;
import com.yss.datamiddle.message.common.RetResult;
import com.yss.datamiddle.message.dto.message.PublishThemeDto;
import com.yss.datamiddle.message.dto.subscribe.SubscribeAddDto;
import com.yss.datamiddle.message.dto.subscribe.SubscribeListDto;
import com.yss.datamiddle.message.dto.subscribe.TerminalDeatils;
import com.yss.datamiddle.message.dto.template.TemplateListDto;
import com.yss.datamiddle.message.dto.theme.ThemeListDto;
import com.yss.datamiddle.message.rest.MsSdkMessageRest;
import com.yss.datamiddle.message.rest.MsSdkSubscribeRest;
import com.yss.datamiddle.message.rest.MsSdkTemplateRest;
import com.yss.datamiddle.message.rest.MsSdkThemeRest;
import com.yss.datamiddle.message.rest.impl.MsSdkMessageRestImpl;
import com.yss.datamiddle.message.rest.impl.MsSdkSubscribeRestImpl;
import com.yss.datamiddle.message.rest.impl.MsSdkTemplateRestImpl;
import com.yss.datamiddle.message.rest.impl.MsSdkThemeRestImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jiafupeng
 * @desc
 * @create 2020/12/29 15:10
 * @update 2020/12/29 15:10
 **/
public class Main2 {


    private static void ThemeList(){
        MsSdkThemeRest msSdkThemeRest = new MsSdkThemeRestImpl();
        ThemeListDto themeListDto = new ThemeListDto();
        themeListDto.setPageNum(0);
        themeListDto.setPageSize(10);
        themeListDto.setHost("192.168.100.163:30250");
        RetResult retResult = msSdkThemeRest.list(themeListDto);
        System.out.println(retResult);
        Object data = retResult.getData();
        Map map = (Map)data;
        List list1 = (List)map.get("list");
        System.out.println(list1);
    }

    private static void detail(){
        MsSdkThemeRest msSdkThemeRest = new MsSdkThemeRestImpl();
        RetResult detail = msSdkThemeRest.detail(6L, "192.168.100.163:30250");
        System.out.println(detail);
    }

    private static void subList(){
        MsSdkSubscribeRest msSdkSubscribeRest = new MsSdkSubscribeRestImpl();
        SubscribeListDto subscribeListDto = new SubscribeListDto();
        subscribeListDto.setPageNum(0);
        subscribeListDto.setPageSize(10);
        subscribeListDto.setHost("192.168.100.163:30250");
        RetResult list = msSdkSubscribeRest.list(subscribeListDto);
        Map map = (Map)list.getData();
//        Map map = JSON.parseObject(data.toString(), Map.class);
        List list1 = (List)map.get("list");
        list1.stream().forEach(o -> System.out.println(o));
    }

    private static void subDel(){
        MsSdkSubscribeRest msSdkSubscribeRest = new MsSdkSubscribeRestImpl();
        RetResult delete = msSdkSubscribeRest.delete(11L, "192.168.100.163:30250");
        System.out.println(delete);
    }

    private static void subAdd(){
        MsSdkSubscribeRest msSdkSubscribeRest = new MsSdkSubscribeRestImpl();
        SubscribeAddDto subscribeAddDto = new SubscribeAddDto();
        subscribeAddDto.setHost("192.168.100.163:30250");
        subscribeAddDto.setAgreement("email");
        subscribeAddDto.setThemeId(6L);

        TerminalDeatils terminalDeatils = new TerminalDeatils();
        terminalDeatils.setTerminal("zhangchunyan@ysstech.com");
        terminalDeatils.setTerminalRemarks("张春艳测试");

        ArrayList<TerminalDeatils> list = Lists.newArrayList();
        list.add(terminalDeatils);
        subscribeAddDto.setTerminals(list);

        RetResult add = msSdkSubscribeRest.add(subscribeAddDto);
        System.out.println(add);
    }

    public static void templateList(){
        MsSdkTemplateRest msSdkTemplateRest = new MsSdkTemplateRestImpl();
        TemplateListDto templateListDto = new TemplateListDto();
        templateListDto.setPageNum(0);
        templateListDto.setPageSize(10);
        templateListDto.setHost("192.168.100.163:30250");
        RetResult list = msSdkTemplateRest.list(templateListDto);
        System.out.println(list);
    }

    public static void send(){
        MsSdkMessageRest msSdkMessageRest = new MsSdkMessageRestImpl();
        PublishThemeDto publishThemeDto = new PublishThemeDto();
        publishThemeDto.setHost("192.168.100.163:30250");
        publishThemeDto.setThemeId(6L);
        publishThemeDto.setMessageType((byte)0);
        publishThemeDto.setMessageName("测试 name");
        publishThemeDto.setMessageContent("测试 content");
        RetResult retResult = msSdkMessageRest.publishThemeMessage(publishThemeDto);
        System.out.println(retResult);
    }
}
