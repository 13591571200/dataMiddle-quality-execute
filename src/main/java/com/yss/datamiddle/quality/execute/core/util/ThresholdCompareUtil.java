package com.yss.datamiddle.quality.execute.core.util;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jiafupeng
 * @desc 阈值比较工具 1-等于 2-大于 3-小于 4-大于等于 5-小于等于 6-不等于
 * @create 2020/12/9 13:42
 * @update 2020/12/9 13:42
 **/
public class ThresholdCompareUtil {

    public static boolean compare(Integer thresholdCompareType, long checkValue, long threshold){
        if(1 == thresholdCompareType){
            return checkValue == threshold;
        }

        if(2 == thresholdCompareType){
            return checkValue > threshold;
        }

        if(3 == thresholdCompareType){
            return checkValue < threshold;
        }

        if(4 == thresholdCompareType){
            return checkValue >= threshold;
        }

        if(5 == thresholdCompareType){
            return checkValue <= threshold;
        }

        if(6 == thresholdCompareType){
            return checkValue != threshold;
        }

        throw new RuntimeException();
    }

    public static boolean compare(String expression, long checkValue){
        Expression compile = AviatorEvaluator.compile(expression);
        Map<String, Object> map = new HashMap<>();
        map.put("result", checkValue);
        return (Boolean)compile.execute(map);
    }

    public static String getExpression(Integer thresholdCompareType, Integer threshold){
        String compareStr = "unknow compare symbol";
        if(1 == thresholdCompareType){
            compareStr = "==";
        }

        if(2 == thresholdCompareType){
            compareStr = ">";
        }

        if(3 == thresholdCompareType){
            compareStr = "<";
        }

        if(4 == thresholdCompareType){
            compareStr = ">=";
        }

        if(5 == thresholdCompareType){
            compareStr = "<=";
        }

        if(6 == thresholdCompareType){
            compareStr = "!=";
        }

        return "result " + compareStr + " " + threshold;
    }
}
