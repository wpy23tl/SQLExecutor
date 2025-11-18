package com.example.demo.execute;

import io.micrometer.common.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class MultiDataSourceSqlExecutor {

    @Autowired
    @Qualifier("jdbcTemplate1")
    private JdbcTemplate jdbcTemplate1;

    @Autowired
    @Qualifier("jdbcTemplate2")
    private JdbcTemplate jdbcTemplate2;

    @Autowired
    @Qualifier("jdbcTemplate3")
    private JdbcTemplate jdbcTemplate3;

    private List<JdbcTemplate> getAllTemplates() {
        return Arrays.asList(jdbcTemplate1, jdbcTemplate2, jdbcTemplate3);
    }

    /**
     * 并发执行SQL到所有数据源
     */
    public Map<String, Object> executeOnAllDataSources(String sql) {
        // 1. SQL校验
        validateSql(sql);

        // 2. 并发执行
        List<JdbcTemplate> templates = getAllTemplates();
        List<CompletableFuture<ExecutionResult>> futures = new ArrayList<>();

        for (int i = 0; i < templates.size(); i++) {
            final int index = i;
            CompletableFuture<ExecutionResult> future = CompletableFuture.supplyAsync(() -> {
                return executeSingleDataSource(templates.get(index), "DB" + (index + 1), sql);
            });
            futures.add(future);
        }

        // 3. 收集结果
        List<ExecutionResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // 4. 返回汇总
        return buildResponse(results);
    }

    private ExecutionResult executeSingleDataSource(JdbcTemplate template, String dbName, String sql) {
        try {
            if (sql.trim().toUpperCase().startsWith("SELECT")) {
                // 查询语句
                List<Map<String, Object>> data = template.queryForList(sql);
                return ExecutionResult.success(dbName, "查询成功", data);
            } else {
                // DDL/DML语句
                int rows = template.update(sql);
                return ExecutionResult.success(dbName, "执行成功，影响行数：" + rows, null);
            }
        } catch (Exception e) {
            return ExecutionResult.failure(dbName, e.getMessage());
        }
    }

    /**
     * SQL安全校验
     */
    private void validateSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("SQL不能为空");
        }

        String upperSql = sql.trim().toUpperCase();

        // 禁止危险操作
        List<String> dangerousKeywords = Arrays.asList(
                "DROP DATABASE", "TRUNCATE", "DELETE FROM.*WHERE.*1.*=.*1"
        );

        for (String keyword : dangerousKeywords) {
            if (upperSql.matches(".*" + keyword + ".*")) {
                throw new SecurityException("禁止执行危险SQL：" + keyword);
            }
        }
    }

    /**
     * 构建响应结果
     */
    private Map<String, Object> buildResponse(List<ExecutionResult> results) {
        Map<String, Object> response = new HashMap<>();

        // 1. 统计成功和失败数量
        long successCount = results.stream().filter(ExecutionResult::isSuccess).count();
        long failureCount = results.size() - successCount;

        // 2. 判断总体执行状态
        String overallStatus;
        if (successCount == results.size()) {
            overallStatus = "全部成功";
        } else if (successCount == 0) {
            overallStatus = "全部失败";
        } else {
            overallStatus = "部分成功";
        }

        // 3. 组装返回数据
        response.put("overallStatus", overallStatus);
        response.put("totalCount", results.size());
        response.put("successCount", successCount);
        response.put("failureCount", failureCount);
        response.put("details", results);
        response.put("executeTime", LocalDateTime.now());

        // 4. 如果有失败的，汇总错误信息
        if (failureCount > 0) {
            List<String> errors = results.stream()
                    .filter(r -> !r.isSuccess())
                    .map(r -> r.getDbName() + ": " + r.getMessage())
                    .collect(Collectors.toList());
            response.put("errors", errors);
        }

        return response;
    }


}

@Data
@AllArgsConstructor
class ExecutionResult {
    private String dbName;
    private boolean success;
    private String message;
    private Object data;

    public static ExecutionResult success(String dbName, String msg, Object data) {
        return new ExecutionResult(dbName, true, msg, data);
    }

    public static ExecutionResult failure(String dbName, String msg) {
        return new ExecutionResult(dbName, false, msg, null);
    }





}

