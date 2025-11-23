package com.example.demo.execute.controller;

import com.example.demo.execute.vo.ExecutionResult;
import com.example.demo.execute.vo.ExecutorVO;
import com.example.demo.execute.service.MultiDataSourceSqlExecutorService;
import com.example.demo.execute.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/execute")
public class ExecutorController {

    private static final Logger log = LoggerFactory.getLogger(ExecutorController.class);
    @Autowired
    private MultiDataSourceSqlExecutorService sqlExecutor;


    @PostMapping("/executeSql")
    Result<List<ExecutionResult>> execute(@RequestBody ExecutorVO vo) {

        String sql = vo.getSql();

        sql = "create table sys_dept (\n" +
                "  dept_id           bigint(20)      not null auto_increment    comment '部门id',\n" +
                "  parent_id         bigint(20)      default 0                  comment '父部门id',\n" +
                "  ancestors         varchar(50)     default ''                 comment '祖级列表',\n" +
                "  dept_name         varchar(30)     default ''                 comment '部门名称',\n" +
                "  order_num         int(4)          default 0                  comment '显示顺序',\n" +
                "  leader            varchar(20)     default null               comment '负责人',\n" +
                "  phone             varchar(11)     default null               comment '联系电话',\n" +
                "  email             varchar(50)     default null               comment '邮箱',\n" +
                "  status            char(1)         default '0'                comment '部门状态（0正常 1停用）',\n" +
                "  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',\n" +
                "  create_by         varchar(64)     default ''                 comment '创建者',\n" +
                "  create_time \t    datetime                                   comment '创建时间',\n" +
                "  update_by         varchar(64)     default ''                 comment '更新者',\n" +
                "  update_time       datetime                                   comment '更新时间',\n" +
                "  primary key (dept_id)\n" +
                ") engine=innodb auto_increment=200 comment = '部门表';";

        List<ExecutionResult> list = sqlExecutor.executeOnAllDataSources(sql);

        return Result.success(list);

    }


}
