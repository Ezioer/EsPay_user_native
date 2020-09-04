package com.easou.androidsdk.util;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Window;
import android.view.WindowManager;

import com.easou.androidsdk.romutils.RomUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NotchUtil {
    public static final int FLAG_NOTCH_SUPPORT = 0x00010000;

    public static void fullscreenUseStatus(Activity activity) {
        try {
            if (Build.VERSION.SDK_INT == 26 || Build.VERSION.SDK_INT == 27) {
                Window window = activity.getWindow();
                if (RomUtils.checkIsHuaweiRom()) {
                    if (isHWNotchScreen(window)) {
                        setFullScreenWindowLayoutInDisplayCutout(window);
                    }
                } else if (RomUtils.checkIsMiuiRom()) {
                    if (isXMNotchScreen(window)) {
                        fullScreenUseStatusXM(activity);
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    public static boolean isHWNotchScreen(Window window) {
        boolean isNotchScreen = false;
        try {
            ClassLoader cl = window.getContext().getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            isNotchScreen = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            ESdkLog.d("hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            ESdkLog.d("hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            ESdkLog.d("hasNotchInScreen Exception");
        } finally {
            return isNotchScreen;
        }
    }

    public static void setFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("addHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT);
        } catch (Exception e) {
            ESdkLog.d("hw add notch screen flag api error");
        }
    }

    public static boolean isXMNotchScreen(Window window) {
        return "1".equals(SystemProperties.getInstance().get("ro.miui.notch"));
    }

    public static void fullScreenUseStatusXM(Activity activity) {
        //开启配置
        int FLAG_NOTCH = 0x00000100 | 0x00000200 | 0x00000400;
        try {
            Method method = Window.class.getMethod("addExtraFlags", int.class);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            method.invoke(activity.getWindow(), FLAG_NOTCH);
        } catch (Exception e) {

        }
    }
}

