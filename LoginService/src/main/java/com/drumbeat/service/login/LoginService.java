package com.drumbeat.service.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;
import com.drumbeat.service.login.bean.FailureBean;
import com.drumbeat.service.login.bean.LoginBean;
import com.drumbeat.service.login.bean.TenantBean;
import com.drumbeat.service.login.bean.UserInfoBean;
import com.drumbeat.service.login.config.ServiceConfig;
import com.drumbeat.service.login.utils.SharedPreferencesUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by ZuoHailong on 2019/10/17.
 */
public class LoginService {

    private static final String SP_TENANT_ID = "sp_tenant_id";
    private static ServiceConfig sConfig;

    public static void setConfig(ServiceConfig config) {
        if (sConfig == null) {
            synchronized (ServiceConfig.class) {
                if (sConfig == null) {
                    sConfig = config == null ? ServiceConfig.newBuilder().build() : config;
                }
            }
        }
    }

    /**
     * 查询初始化配置数据
     *
     * @return ServiceConfig 初始化配置的实体类对象
     */
    public static ServiceConfig getConfig() {
        // 保证sConfig不是null
        setConfig(null);
        return sConfig;
    }

    /**
     * 查询账户所在的租户集合
     *
     * @param account  手机号/账号/邮箱号/身份证号
     * @param callback
     */
    public static void getTenantList(@NonNull String account,
                                     @NonNull Callback<List<TenantBean.ResultBean>> callback) {
        ProcessControl.getTenantList(account, callback);
    }

    /**
     * 设置租户ID
     *
     * @param tenantId 租户ID
     */
    public static void setTenantId(String tenantId) {
        SharedPreferencesUtil.getInstance(Utils.getApp()).put(SP_TENANT_ID, tenantId);
    }

    /**
     * 获取已设置的租户ID
     *
     * @return
     */
    public static String getTenantId() {
        return SharedPreferencesUtil.getInstance(Utils.getApp()).getString(SP_TENANT_ID);
    }

    /**
     * 登录中台
     *
     * @param account
     * @param password
     * @param callback
     */
    public static void login(@NonNull String account, @NonNull String password, @NonNull Callback<LoginBean> callback) {
        String centralizerToken = getCentralizerToken(null);
        // 已有token，直接返回，不再登录，为了兼容宿主APP与独立APP同时存在的情况
        if (!TextUtils.isEmpty(centralizerToken)) {
            callback.onSuccess(new LoginBean().setToken(centralizerToken));
            return;
        }
        ProcessControl.login(LoginService.getConfig(), account, password, callback);
    }

    /**
     * 扫描二维码登录，用于Web页管理系统
     *
     * @param activity
     * @param userId   从二维码中扫出的userId
     * @param callback
     */
    public static void loginQrcode(@NonNull Activity activity, @NonNull String centralizerToken, @NonNull String userId, @NonNull LoginService.Callback callback) {
        ProcessControl.loginQrcode(activity, centralizerToken, userId, callback);
    }

    /**
     * 检查账户密码是否过期，若过期则必须修改密码
     *
     * @param centralizerToken 中台token
     * @param callback
     */
    public static void checkPasswordExpire(@NonNull String centralizerToken, @NonNull Callback<Boolean> callback) {
        ProcessControl.checkPasswordExpire(centralizerToken, callback);
    }

    /**
     * 修改密码
     */
    public static void modifyPassword(@NonNull String centralizerToken, @NonNull String oldPwd, @NonNull String newPwd, @NonNull Callback<Boolean> callback) {
        ProcessControl.modifyPwd(centralizerToken, oldPwd, newPwd, callback);
    }

    /**
     * 查询用户信息
     */
    public static void getUserInfo(@NonNull String centralizerToken, @NonNull Callback<UserInfoBean.ResultBean> callback) {
        ProcessControl.getUserInfo(centralizerToken, callback);
    }

    /**
     * 获取中台Token<br>
     * 供GhostApp使用<br/>
     * 独立App调用此方法无法获取到中台Token
     *
     * @param context Context
     * @return String 中台Token
     */
    public static String getCentralizerToken(@Nullable Context context) {
        return ProcessControl.getTokenFromCP(context);
    }

    public abstract static class Callback<T> {

        /**
         * 获取传入泛型的Type
         *
         * @return
         */
        public Type getSuccessType() {
            Type superClass = getClass().getGenericSuperclass();
            return ((ParameterizedType) superClass).getActualTypeArguments()[0];
        }

        public abstract void onSuccess(T success);

        public abstract void onFailure(FailureBean failure);
    }
}
