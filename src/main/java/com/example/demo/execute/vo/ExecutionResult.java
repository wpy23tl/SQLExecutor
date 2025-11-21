package com.example.demo.execute.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutionResult {
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

