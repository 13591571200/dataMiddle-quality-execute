package com.yss.datamiddle.quality.execute.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiafupeng
 * @desc 工作流实例entity
 * @create 2020/12/15 16:58
 * @update 2020/12/15 16:58
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstanceEntity {

    private Integer id;

    private String name;
}
