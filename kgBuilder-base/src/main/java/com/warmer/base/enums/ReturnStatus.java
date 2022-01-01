package com.warmer.base.enums;

import lombok.Getter;

@Getter
public enum ReturnStatus {
    Success("操作成功", 200),
    OPERAFAIL("操作失败", 201),
    DISTINCT("存在重复数据", 202),
    FILENOTFIND("文件找不到", 203),
    NoUser("用户不存在", 204),
    ContentError("内容格式错误", 205),
    RECORD_NOT_FOUND("记录不存在", 206),
    ValidateFailure("参数验证错误", 400),
    SessionTimeout("会话超时", 401),
    LoginFailure("登录失败", 402),
    NoRight("权限不足",403),
    InvalidCSRF("无效的保护令牌",404),
    NoIntentResult("意图识别没有结果",405),
    MoreFieldIntentResult("意图命中多个字段",408),
    RequestMissingBodyError("缺少请求体!",1004),
    ParameterMissingError("确少必要请求参数!",1003),
    ParameterError("请求参数有误!",1002),
    NotFountResource("没有找到相关资源!",1001),
    Error("服务器错误", 500);


    private final String name;

    private final Integer value;

    private ReturnStatus(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
    @Override
    public String toString() {
        return value.toString();
    }

    public static ReturnStatus valueOf(int value) {
        //手写的从int到enum的转换函数
        switch (value) {
            case 200:
                return Success;
            case 201:
                return OPERAFAIL;
            case 202:
                return DISTINCT;
            case 203:
                return FILENOTFIND;
            case 204:
                return NoUser;
            case 205:
                return ContentError;
            case 206:
                return RECORD_NOT_FOUND;
            case 401:
                return SessionTimeout;
            case 402:
                return LoginFailure;
            case 403:
                return NoRight;
            case 500:
                return Error;
            default:
                return null;
        }
    }
}
