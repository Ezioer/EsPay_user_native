package com.easou.androidsdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.easou.androidsdk.data.Constant;
import com.easou.androidsdk.data.ESConstant;
import com.easou.androidsdk.login.service.CodeConstant;
import com.easou.androidsdk.login.service.LoginNameInfo;
import com.easou.androidsdk.login.service.AuthBean;
import com.easou.androidsdk.login.service.LoginBean;
import com.easou.androidsdk.ui.UIHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    public static AuthBean getAuthBean(Context context, String appid) {
        String gsonStr = FileHelper
                .readFile(Constant.getLoginInfoFile(context));
        if (null == gsonStr || "".equals(gsonStr)) {
            gsonStr = FileHelper.readFile(Constant.getSLoginInfoFile(appid));
            if (null == gsonStr || "".equals(gsonStr))
                return null;
        }
        AuthBean bean = GsonUtil.fromJson(gsonStr, AuthBean.class);
        return bean;
    }

    public static String getTokenFromSD(String appid) {
        try {
            String gsonStr = FileHelper.readFile(Constant.getSLoginInfoFile(appid));
            if (null == gsonStr || "".equals(gsonStr))
                return null;
            return gsonStr;
        } catch (Exception e) {
            ESdkLog.d(e.toString());
        }
        return "";
    }

    public static void saveH5TokenToCard(String token, String appid) {
        try {
            FileHelper.writeFile(new File(Constant.SdcardPath.CACHE_SAVEPATH + "/" + appid + ".txt"), token);
        } catch (Exception e) {
            ESdkLog.d(e.toString());
        }
    }

    public static String getEsDeviceID(Context mContext) {

        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_DEVICE_ID, 0);
        String esDevID = settings.getString(Constant.ES_DEV_ID, "").toString();

        if (TextUtils.isEmpty(esDevID)) {
            String devID = Tools.getDeviceBrand() + Tools.getSystemModel() +
                    Tools.getSystemVersion() + System.currentTimeMillis();
            esDevID = Md5SignUtils.sign(devID, readPropertiesValue(mContext, Constant.KEY));
            saveEsDeviceID(mContext, esDevID);
        }

        return esDevID;
    }

    public static void saveEsDeviceID(Context mContext, String devID) {

        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_DEVICE_ID, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.ES_DEV_ID, devID);
        editor.commit();
    }

    public static String getH5Token(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String token = settings.getString(Constant.ES_TOKEN, "").toString();
        if (token.isEmpty() || null == settings || null == token) {
            settings = mContext.getSharedPreferences(mContext.getPackageName() + Constant.ES_H5_TOKEN, 0);
            token = settings.getString(Constant.ES_TOKEN, "").toString();
        }
        return token;
    }

    public static void saveH5Token(Context mContext, String token) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.ES_TOKEN, token);
        editor.commit();
    }

    public static void saveUInfo(String u, Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("U", u);
        editor.commit();
    }

    public static String getUInfo(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String info = settings.getString("U", "");
        return info;
    }


    public static void saveLoginInfo(String info, Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.LOGIN_INFO, info);
        editor.commit();
    }

    public static LoginBean getLoginInfo(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String info = settings.getString(Constant.LOGIN_INFO, "");
        if (info.isEmpty()) {
            return null;
        }
        return GsonUtil.fromJson(info, LoginBean.class);
    }

    public static String getServiceUrl(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String info = settings.getString("serviceurl", "");
        return info;
    }

    public static void saveServiceUrl(Context mContext, String info) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("serviceurl", info);
        editor.commit();
    }

    public static String getPlayerId(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String info = settings.getString("playerid", "");
        return info;
    }

    public static void savePlayerId(Context mContext, String id) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("playerid", id);
        editor.commit();
    }

    public static String getServerId(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String info = settings.getString("serverid", "");
        return info;
    }

    public static void saveServerId(Context mContext, String id) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("serverid", id);
        editor.commit();
    }

    public static int getNationIdentity(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        int state = settings.getInt("nationidentity", 0);
        return state;
    }

    public static void saveNationIdentity(Context mContext, int state) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("nationidentity", state);
        editor.commit();
    }

    public static int getLogState(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        int state = settings.getInt("logstate", 0);
        return state;
    }

    public static void saveLogState(Context mContext, int state) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("logstate", state);
        editor.commit();
    }

    public static int isShowMoney(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        int info = settings.getInt("showmoney", 0);
        return info;
    }

    public static void saveShowMoney(Context mContext, int isShow) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("showmoney", isShow);
        editor.commit();
    }

    public static void saveLoginNameInfo(String info, Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.LOGIN_NAME, info);
        editor.commit();
    }

    public static List<LoginNameInfo> getLoginNameInfo(Context mContext) {
        SharedPreferences settings = mContext.getSharedPreferences(Constant.ES_H5_TOKEN, 0);
        String info = settings.getString(Constant.LOGIN_NAME, "");
        if (info.isEmpty()) {
            return new ArrayList<LoginNameInfo>();
        }

        return new Gson().fromJson(info, new TypeToken<List<LoginNameInfo>>() {
        }.getType());
    }

    public static String getYMDfromIdNum(String id) {
        String year = "";
        String month = "";
        String day = "";
        //正则匹配身份证号是否是正确的，15位或者17位数字+数字/x/X
        if (id.matches("^\\d{15}|\\d{17}[\\dxX]$")) {
            year = id.substring(6, 10);
            month = id.substring(10, 12);
            day = id.substring(12, 14);
            if (id.length() == 15) {
                year = "19" + id.substring(6, 8);
                month = id.substring(8, 10);
                day = id.substring(10, 12);
            }
        } else {
            return "";
        }
        return year + "-" + month + "-" + day;
    }

    public static int getAge(String id) {
        try {
            String year = "";
            //正则匹配身份证号是否是正确的，15位或者17位数字+数字/x/X
            if (id.matches("^\\d{15}|\\d{17}[\\dxX]$")) {
                year = id.substring(6, 10);
                if (id.length() == 15) {
                    year = "19" + id.substring(6, 8);
                }
            } else {
                System.out.println("身份证号码不匹配！");
                return 0;
            }
            Calendar date = Calendar.getInstance();
            String currentYear = String.valueOf(date.get(Calendar.YEAR));
            return Integer.valueOf(currentYear) - Integer.valueOf(year);
        } catch (Exception e) {
            return 0;
        }
    }


    public static boolean isIDNumber(String IDNumber) {
        if (IDNumber == null || "".equals(IDNumber)) {
            return false;
        }
        // 定义判别用户身份证号的正则表达式（15位或者18位，最后一位可以为字母）
        String regularExpression = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|" +
                "(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
        //假设18位身份证号码:41000119910101123X  410001 19910101 123X
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //(18|19|20)                19（现阶段可能取值范围18xx-20xx年）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
        //[0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
        //$结尾

        //假设15位身份证号码:410001910101123  410001 910101 123
        //^开头
        //[1-9] 第一位1-9中的一个      4
        //\\d{5} 五位数字           10001（前六位省市县地区）
        //\\d{2}                    91（年份）
        //((0[1-9])|(10|11|12))     01（月份）
        //(([0-2][1-9])|10|20|30|31)01（日期）
        //\\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
        //$结尾


        boolean matches = IDNumber.matches(regularExpression);

        //判断第18位校验值
        if (matches) {

            if (IDNumber.length() == 18) {
                try {
                    char[] charArray = IDNumber.toCharArray();
                    //前十七位加权因子
                    int[] idCardWi = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
                    //这是除以11后，可能产生的11位余数对应的验证码
                    String[] idCardY = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
                    int sum = 0;
                    for (int i = 0; i < idCardWi.length; i++) {
                        int current = Integer.parseInt(String.valueOf(charArray[i]));
                        int count = current * idCardWi[i];
                        sum += count;
                    }
                    char idCardLast = charArray[17];
                    int idCardMod = sum % 11;
                    if (idCardY[idCardMod].toUpperCase().equals(String.valueOf(idCardLast).toUpperCase())) {
                        return true;
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

        }
        return matches;
    }

    /**
     * 判定输入的是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    public static boolean isCharChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 校验String是否全是中文
     *
     * @param name 被校验的字符串
     * @return true代表全是汉字
     */
    public static boolean checkNameChinese(String name) {
        boolean res = true;
        char[] cTemp = name.toCharArray();
        for (int i = 0; i < name.length(); i++) {
            if (!isCharChinese(cTemp[i])) {
                res = false;
                break;
            }
        }
        return res;
    }

    /**
     * 检测某个应用是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断当前是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkUseable(Context context) {
        // ConnectivityManager cm = (ConnectivityManager) context
        // .getSystemService(Context.CONNECTIVITY_SERVICE);
        // if (cm == null)
        // return false;
        // NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if (networkInfo != null && networkInfo.isAvailable() &&
        // networkInfo.isConnectedOrConnecting()) {
        // return true;
        // }
        // return false;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 是否挂载了sdcard
     *
     * @return
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取VersionName
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo pkg;
        try {
            pkg = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pkg.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public static String formatTime(Long time) {
        long value = time;//361
        Long seconds = value % 60;
        value /= 60;
        Long minutes = value % 60;
        value /= 60;
        Long hours = value % 24;
        Long day = value / 24;
        return String.format("%d天%d时%d分%d秒", day, hours, minutes, seconds);
    }

    public static String formatDate(long time) {
        String YMDHMS = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(YMDHMS);
        return format.format(new Date(time));
    }

    public static boolean isNotNullOrEmpty(String string) {
        if (string == null) {
            return false;
        }
        if (string.isEmpty() || string.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取VersionCode
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo pkg;
        try {
            pkg = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return pkg.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    /**
     * 获取渠道号Channel Id（存在Mainfest文件中）
     *
     * @return
     */
    public static String getMetaDataItem(Context context, String key) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString(key);
            return msg;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 根据字段名得到QN参数
     *
     * @param context
     * @return
     */
    public String getQN(Context context) {
        // TODO: 从Mainfest文件中读取QN
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString("UMENG_CHANNEL");
            return msg;
        } catch (Exception e) {
            return "none";
        }
    }

    /**
     * 显示提示信息
     *
     * @param context
     * @param msg
     * @param handler
     */
    public static void postShowMsg(final Context context, final String msg,
                                   Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示自定义位置的Toast
     *
     * @param context
     * @param msg
     * @param handler
     * @param gravity
     */
    public static void postShowMsg(final Context context, final String msg,
                                   Handler handler, final int gravity) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast t = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                t.setView(UIHelper.createToastView(context, msg));
                t.setGravity(gravity, 0, 50);
                t.show();
            }
        });
    }

    /**
     * 得到Preference对象
     *
     * @param context
     * @return
     */
    public static SharedPreferences getSettings(Context context) {
        SharedPreferences preference = context.getSharedPreferences(
                Constant.KEY_NEED_NOTICE_BIND_PHONE, Context.MODE_PRIVATE);
        return preference;
    }


    /**
     * 从URL中得到ticket
     *
     * @param url
     * @return
     */
    public static String getTicket(String url) {
        int start = url.indexOf("ticket=") + "ticket=".length();
        String queryString = url.substring(start);
        int end = queryString.indexOf("&");
        if (end == -1) {
            return queryString;
        } else {
            return queryString.substring(0, end);
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // Easou Pay !
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 从Properties文件中读取配置信息
     *
     * @param key：参数名称
     */
    public static String readPropertiesValue(Context _context, String key) {
        Properties prop = new Properties();
        InputStream is = null;
        String str = "ZKX";
        try {
            is = _context.getAssets().open("client.properties");
            prop.load(is);
            str = prop.getProperty(key);
            if (null == str) {
                str = "ZKX";
            }
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            return str;
        }
    }


    /**
     * 获取待签名字符串
     */
    public static String getStringForSign(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        TreeMap<String, String> treeMap = new TreeMap<String, String>(map);
        if (treeMap != null) {
            for (Map.Entry<String, String> entity : treeMap.entrySet()) {
                if (entity.getKey() != null && entity.getValue() != null) {
                    sb.append(entity.getKey()).append("=")
                            .append(String.valueOf(entity.getValue()))
                            .append("&");
                }
            }
        }
        if (sb.length() > 0) {// 删除最后的&符
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }


    /**
     *
     */
    public static String encoder(String value)
            throws UnsupportedEncodingException {
        // 转中文
        String enUft = URLEncoder.encode(value, "UTF-8");
        java.net.URLDecoder urlDecoder = new java.net.URLDecoder();
        String str = urlDecoder.decode(enUft, "UTF-8");
        return str;
    }

    public static int uniLength(String value) {
        int valueLength = 0;
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            char c = value.charAt(i);
            // 判断是否为中文字符
            if (isChinese(c)) {
                // 中文字符长度为2
                valueLength += 2;
            } else {
                // 其他字符长度为1
                valueLength += 1;
            }
        }
        // 进位取整
        return valueLength;
    }

    public static boolean isHalfChar(String str) {
        if (str == null) {
            return false;
        } else {
            String regex = "[\u0000-\u00FF]+";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(str);
            boolean validate = m.matches();
            return validate;
        }
    }

    private static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 从资源文件中读取图片
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(Context context, String fileName) {
        AssetManager am = context.getAssets();
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = am.open(fileName);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return bitmap;
    }

    /**
     * 得到点9图
     *
     * @param fileName
     * @return
     */
    public static NinePatchDrawable getNinePatchDrawable(Context context,
                                                         String fileName) {
        AssetManager am = context.getAssets();

        InputStream is = null;
        try {
            if (fileName.contains(".9")) {
                is = am.open(fileName);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                byte[] chunk = bitmap.getNinePatchChunk();
                NinePatchDrawable patchy = new NinePatchDrawable(bitmap, chunk,
                        new Rect(), null);
                return patchy;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取9path图形
     *
     * @param context
     * @param fileName
     * @return
     */
    public static Drawable get9Path(Context context, String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(fileName);
            String srcName = fileName;
            Drawable d = NinePatchDrawable.createFromStream(is, srcName);
            return d;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    is = null;
                }
            }
        }
        return null;
    }
    // 来自StartESPayCenter
    //
    // /**
    // * 得到带有Cookie信息的url
    // *
    // * @return
    // */
    // public String buildUrl(Context context, TradeInfo info) {
    // String[] list = getToken(context);
    // if (list == null) {
    // list = new String[] { "", "" };
    // }
    // String visitUrl = "";
    // try {
    // // String titleUrl = Constant.DOMAIN + Tools.getHostName() + Constant.URL_PAY_TRADE + "EASOUTGC=" + list[0]
    // // + "&U=" + list[1] + "&redirectUrl=";
    // String titleUrl = Constant.DOMAIN + Tools.getHostName() + Constant.URL_PAY_TRADE
    // +
    // "EASOUTGC=TGT-47992-vFnlrTjf2cBE55rdydqohoaWlJIDFWFdah7ZfnD7j9yOLlZc2S-sso"
    // + "&redirectUrl=";
    //
    // String bodyUrl = Constant.DOMAIN + Tools.getHostName() + Constant.URL_PAY + "appId="
    // + URLEncoder.encode(info.getAppId(), "UTF-8")
    // + "&partnerId="
    // + URLEncoder.encode(info.getPartenerId(), "UTF-8")
    // + "&tradeId="
    // + URLEncoder.encode(info.getTradeId(), "UTF-8")
    // + "&tradeName="
    // + URLEncoder.encode(info.getTradeName(), "UTF-8")
    // + "&tradeDesc="
    // + URLEncoder.encode(info.getTradeDesc(), "UTF-8")
    // + "&reqFee=" + URLEncoder.encode(info.getReqFee(), "UTF-8")
    // + "&notifyUrl="
    // + URLEncoder.encode(info.getNotifyUrl(), "UTF-8")
    // + "&redirectUrl="
    // + URLEncoder.encode(info.getRedirectUrl(), "UTF-8")
    // + "&sign=" + URLEncoder.encode(getSign(info), "UTF-8")
    // + "&separable="
    // + URLEncoder.encode(info.getSeparable(), "UTF-8")
    // + "&payerId="
    // + URLEncoder.encode(info.getPayerId(), "UTF-8") + "&qn="
    // + URLEncoder.encode(info.getQN(), "UTF-8") + "&extInfo="
    // + URLEncoder.encode(info.getExectInfo(), "UTF-8");
    //
    // String bode = URLEncoder.encode(bodyUrl, "UTF-8");
    // visitUrl = titleUrl + bode;
    // } catch (UnsupportedEncodingException e) {
    // Lg.e(e.toString());
    // }
    // return visitUrl;
    // }

    // /**
    // * 取得签名内容
    // *
    // * @param info
    // * @return
    // */
    // private static String getSign(TradeInfo info) {
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("appId", info.getAppId());
    // map.put("partnerId", info.getPartenerId());
    // map.put("tradeId", info.getTradeId());
    // map.put("tradeName", info.getTradeName());
    // map.put("tradeDesc", info.getTradeDesc());
    // map.put("reqFee", info.getReqFee());
    // map.put("notifyUrl", info.getNotifyUrl());
    // map.put("separable", info.getSeparable());
    // map.put("payerId", info.getPayerId());
    // map.put("qn", info.getQN());
    // // map.put("extInfo",extInfo);
    // map.put("redirectUrl", info.getRedirectUrl());
    //
    // String temp = CommonUtils.getStringForSign(map);
    // return CommonUtils.md5(temp);
    // }
    //
    // /**
    // * 获取Token信息
    // *
    // * @return
    // */
    // private static String[] getToken(Context context) {
    // String gsonStr = FileHelper
    // .readFile(Constant.getLoginInfoFile(context));
    // if (null == gsonStr || "".equals(gsonStr)) {
    // gsonStr = FileHelper.readFile(Constant.getSDLoginInfoFile());
    // if (null == gsonStr || "".equals(gsonStr))
    // return null;
    // }
    // AuthBean bean = GsonUtil.fromJson(gsonStr, AuthBean.class);
    // if (bean != null && bean.getToken() != null
    // && !"".equals(bean.getToken().getToken())) {
    // String[] strings = new String[] { bean.getToken().getToken(),
    // bean.getU().getU() };
    // return strings;
    // }
    // return null;
    // }

    /**
     * 根据字段名得到QN参数
     *
     * @param type
     * @param context
     * @return
     */
    public String getQN(String type, Context context) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = info.metaData.getString("UMENG_CHANNEL");
            return msg;
        } catch (Exception e) {
            return "none";
        }
    }

    /**
     * 取得配置文件的输入流
     *
     * @return
     */
    public static InputStream getPropertiesFileInputStream(Context _context) {
        try {
            return _context.getAssets().open("client.properties");
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return null;
        // return
        // StartESPayCenter.class.getResourceAsStream("/client/client.properties");
    }

    /**
     * 得到宜支付的App-Agent
     *
     * @return
     */
    public static Map<String, String> getPaySDKWebViewAgent() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("App-Agent", "AndroidSDKEasouPay");
        return map;
    }

    /**
     * 得到用户中心的App-Agent
     *
     * @return
     */
    public static Map<String, String> getAccountSDKWebViewAgent() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("App-Agent", "AndroidSDKEasouAccount");
        return map;
    }

    /**
     * 产生一个随机的字符串
     *
     * @return
     */
    public static String getRandomString(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
