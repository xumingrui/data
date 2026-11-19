package com.huaneng.data.util;

import lombok.Getter;

/**
 * @author mingr
 * @since 2022/12/2 9:29
 */
@Getter
public enum ErrCodeEnum {
    PARAM_VALID_ERROR(3000, "参数校验失败"), API_OTHER_ERROR(-1, "其他错误，比如API命令不存在"), API_OK(0, "成功"),
    API_USEREXIST(8, "其他错误，用户已经存在"),

    API_UIDINVALID(9, "用户名字错误"), API_USRNOTFOUND(19, "用户不存在"), API_DOMAINNOTEXIST(20, "域不存在"),
    API_PASSWORD_ERR(35, "密码错误"), API_SESIDNOTFOUND(28, "Session Id不存在"), API_PARAM_ERR(39, "参数错误"),
    API_DB_ERR(42, "数据库出错"), API_INVALID_UDATTR(43, "无效的UD属性"), API_MAILBOX_EXIST(44, "MAILBOX已经存在"),
    API_MAILBOX_NOT_FOUND(45, "MAILBOX不存在"), API_PIDINVALID(46, "PID无效"), API_QUOTAERR(47, "容量错误"),
    API_SES_ERR(48, "session出错"), API_COSNOTFOUND(50, "COS 不存在"), API_ORTNOTFOUND(51, "组织不存在"),

    ;

    private int code;
    @Getter
    private String message;

    ErrCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String message(int code) {
        for (ErrCodeEnum codeEnum : ErrCodeEnum.values()) {
            if (codeEnum.getCode() == code) {
                return codeEnum.getMessage();
            }
        }
        return "";
    }

}
