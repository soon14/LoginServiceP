package com.drumbeat.service.login.constant;

/**
 * Created by ZuoHailong on 2019/10/16.
 */
public enum ResultCode {
    /**
     * 账号密码登录失败
     */
    ERROR_LOGIN_ACCOUNT,
    /**
     * 二维码扫描失败
     */
    ERROR_QRCODE_SCAN,
    /**
     * 二维码数据验证失败
     */
    ERROR_QRCODE_VERIFY,
    /**
     * 扫码登录失败
     */
    ERROR_QRCODE_LOGIN,
    /**
     * 取消扫码登录
     */
    CANCEL_LOGIN_QRCODE,


    /********************************************************** 入参验证 ********************************************************/
    /**
     * appId is null
     */
    ERROR_NULL_APPID,
    /**
     * account is null
     */
    ERROR_NULL_ACCOUNT,
    /**
     * password is null
     */
    ERROR_NULL_PASSWORD,
}