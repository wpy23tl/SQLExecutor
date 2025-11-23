package com.example.demo.execute.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SqlResultVO {


    // 3. 组装返回数据
    private String overallStatus;
    private Integer totalCount;
    private Integer successCount;
    private Integer failureCount;
    private List<ExecutionResult> results;
    private Date executeTime;


}
