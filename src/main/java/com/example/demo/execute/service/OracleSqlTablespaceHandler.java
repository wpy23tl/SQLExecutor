package com.example.demo.execute.service;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OracleSqlTablespaceHandler {

    private static final Pattern TABLESPACE_PATTERN = Pattern.compile(
            "\\s+TABLESPACE\\s+[\\w]+",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 根据目标表空间处理SQL
     * @param originalSql 原始SQL
     * @param targetTablespace 目标表空间（可为null）
     * @return 处理后的SQL
     */
    public String handleTablespace(String originalSql, String targetTablespace) {
        if (StringUtils.isBlank(originalSql)) {
            return originalSql;
        }

        String processedSql = originalSql;

        // 判断SQL中是否包含TABLESPACE子句
        Matcher matcher = TABLESPACE_PATTERN.matcher(originalSql);

        if (matcher.find()) {
            if (StringUtils.isNotBlank(targetTablespace)) {
                // 方案B：替换为目标表空间
                processedSql = matcher.replaceAll(" TABLESPACE " + targetTablespace);
            } else {
                // 方案A：删除TABLESPACE子句
                processedSql = matcher.replaceAll("");
            }
        } else {
            // 原SQL中没有TABLESPACE，如果配置了目标表空间，尝试添加
            if (StringUtils.isNotBlank(targetTablespace)) {
                processedSql = addTablespaceClause(originalSql, targetTablespace);
            }
        }

        return processedSql;
    }

    /**
     * 智能添加TABLESPACE子句
     */
    private String addTablespaceClause(String sql, String tablespace) {
        String upperSql = sql.toUpperCase().trim();

        // CREATE TABLE 语句
        if (upperSql.startsWith("CREATE TABLE")) {
            // 在第一个 '(' 前添加
            int idx = sql.indexOf('(');
            if (idx > 0) {
                return sql.substring(0, idx) + " TABLESPACE " + tablespace + " " + sql.substring(idx);
            }
        }

        // CREATE INDEX 语句
        if (upperSql.startsWith("CREATE INDEX") || upperSql.startsWith("CREATE UNIQUE INDEX")) {
            // 在最后添加
            return sql.trim() + " TABLESPACE " + tablespace;
        }

        // ALTER TABLE ADD ... 不需要表空间

        return sql;
    }
}
