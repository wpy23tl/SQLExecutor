package com.example.demo.execute.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExecutionResult {

    private String dbName;
    private String ip;
    private boolean success;
    private String message;
    private Object data;

    public static ExecutionResult success(String dbName, String ip, String msg, Object data) {
        return new ExecutionResult(dbName, ip, true, msg, data);
    }

    public static ExecutionResult failure(String dbName, String ip, String msg) {
        return new ExecutionResult(dbName, ip, false, msg, null);
    }


}

