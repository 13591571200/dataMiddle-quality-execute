package com.yss.datamiddle.quality.execute.core.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @author jiafupeng
 * @create 2020/11/19 15:04
 * @desc BeanCopy工具
 **/
public class BeanCopyUtil {

    private BeanCopyUtil(){
        // 防止实例化工具类
        throw new AssertionError("No " + BeanCopyUtil.class.getName() + " instances for you !");
    }

    /**
     * 描述: 封装BeanUtils.copyProperties, 实现类拷贝
     * @param source 源对象
     * @param targetClass 目标对象Class
     * @param <T> 目标对象
     * @return
     */
    public static <T> T copyBean(Object source, Class<T> targetClass){
        if (source == null){
            return null;
        }

        try {
            T target = (T)targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }
}
