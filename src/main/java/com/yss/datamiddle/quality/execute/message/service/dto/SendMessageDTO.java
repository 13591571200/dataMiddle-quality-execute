package com.yss.datamiddle.quality.execute.message.service.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author jiafupeng
 * @desc
 * @create 2020/12/30 15:32
 * @update 2020/12/30 15:32
 **/
@Data
@Builder
public class SendMessageDTO {

    private String checkObjectName;
    private String checkSql;
    private Long checkResultLong;
    private Integer warnType;
    private Integer thresholdCompareType;
    private Integer threshold;
    private String checkExpression;

}
