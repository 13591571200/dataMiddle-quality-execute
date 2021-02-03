package com.yss.datamiddle.quality.execute.service;

/**
 * @author jiafupeng
 * @desc 连接类型枚举
 * @create 2020/11/23 18:12
 * @update 2020/11/23 18:12
 **/
public enum ConnTypeEnum {

    /**
     * 连接类型枚举
     */
    HIVE("db-hive", "hive"),
    ORACLE("db-oracle", "oracle");

    private final String name;
    private final String shortName;

    ConnTypeEnum(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public static String getShortNameByName(String name){
        for(ConnTypeEnum entry : ConnTypeEnum.values()){
            if(entry.getName().equals(name)){
                return entry.getShortName();
            }
        }
        return "unKnow ConnType";
    }
}
