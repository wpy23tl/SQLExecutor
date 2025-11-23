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

        List<ExecutionResult> list = sqlExecutor.executeOnAllDataSources(sql);

        return Result.success(list);

    }


}
