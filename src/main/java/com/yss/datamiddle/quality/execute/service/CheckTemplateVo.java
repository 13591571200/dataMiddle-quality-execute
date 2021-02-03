package com.yss.datamiddle.quality.execute.service;

import lombok.Data;

@Data
public class CheckTemplateVo {

    private Integer id;
    private String tempName;
    private Byte tempType;
    private Integer dimensionId;
    private String suitEngine;
    private Byte checkLevel;
    private String description;
    private String version;
    private String tempSql;
    private Short proportion;
}