package com.example.demo.execute.vo;

import lombok.Getter;

/**
 * 状态码枚举
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),

    // 服务器错误 5xx
    ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务错误 1xxx
    PARAM_ERROR(1001, "参数校验失败"),
    LOGIN_ERROR(1002, "用户名或密码错误"),
    USER_NOT_EXIST(1003, "用户不存在"),
    USER_EXISTED(1004, "用户已存在"),
    TOKEN_INVALID(1005, "Token无效或已过期"),
    PERMISSION_DENIED(1006, "权限不足");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
