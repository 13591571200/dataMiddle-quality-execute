package com.yss.datamiddle.quality.execute.log;

import com.yss.datamiddle.quality.execute.MainApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * @author jiafupeng
 * @desc
 * @create 2020/12/25 16:15
 * @update 2020/12/25 16:15
 **/
public class Log {

    private static Logger log = LoggerFactory.getLogger(Log.class);

    public static void info(String pattern, String ... arguments){
        String format = MessageFormat.format(pattern, arguments);
        log.info(format);
    }

    public static void warn(String pattern, String ... arguments){
        String format = MessageFormat.format(pattern, arguments);
        log.warn(format);
    }
}
