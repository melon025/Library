package info.emm.commonlib.http.interceptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

import info.emm.commonlib.http.bean.UserToken;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.Context.MODE_PRIVATE;

/**
 * <Pre>
 * 请求参数头封装
 * </Pre>
 *
 * @author baoy
 * @version 1.0
 * create by 15/7/16 下午2:38
 */
final class RequestParamsWrapper {

    private final static String tf = "yyyyMMddHHmmssSSS";


    private static final String PARAM_REQ_TIME = "X-Req-Time";//请求时间
    private static final String PARAM_DEVICETOKEN = "deviceno";//设备标识
    private static final String PARAM_DEVICETYPE = "deviceType";//设备类型 android ios pc
    private static final String SID = "sid";//登录返回cookie
    private static final String PARAM_USER_ID = "userid";//用户ID


    private String reqTime;          //请求时间
    private String sid;             //登录返回cookie
    private String deviceToken;     //设备唯一标识
    private String deviceType;     //设备类型
    //    private String token;            //唯一标识
    private String userId;           //用户ID
//    private String noncestr;         //随机6位字符串
//    private String clientType;       //客户端类型
//    private String appType;          //app类型

    /**
     * 请求参数的接口
     *
     * @param ctx context
     */
    RequestParamsWrapper(Context ctx) {

        this.reqTime = getRequestTime();
//        this.clientType = "mapp_android";
//        this.appType = "native";        //原生app调用值为: native
//
//        this.noncestr = "654321";
        UserToken userToken = UserToken.getInstance();

        //授权令牌，客户端如果未登录，则传递空值
        deviceToken = getUUID(ctx);
        deviceType = UserToken.getInstance().getDeviceType();
        sid = UserToken.getInstance().getSid();
        //用户唯一标识，客户端如果未登录，则传递空值
        this.userId = TextUtils.isEmpty(userToken.getUserID()) ? null : userToken.getUserID();
    }

    /**
     * GET请求参数的接口
     *
     * @param ctx context
     */
    RequestParamsWrapper(Context ctx, String urlParam) {

        this(ctx);
        if (urlParam == null || urlParam.length() == 0) {
            urlParam = "";
        }

//        this.sign = sign(urlParam);
    }

    /**
     * POST 请求参数的接口
     *
     * @param ctx
     * @param requestBody
     */
    RequestParamsWrapper(Context ctx, RequestBody requestBody) {
        this(ctx);
        StringBuilder paramString = new StringBuilder();
        if (requestBody instanceof FormBody) {
            FormBody formBody = (FormBody) requestBody;
            int paramSize = formBody.size();
            for (int i = 0; i < paramSize; i++) {
                paramString.append(formBody.name(i)).append("=").append(formBody.value(i));
                if (i < paramSize - 1) {
                    paramString.append("&");
                }
            }
//            this.sign = sign(paramString.toString());
        } else if (requestBody instanceof MultipartBody) {

        } else {

        }
    }


    public Headers getRequestHeaders(Headers headers) {
        Headers.Builder builder = headers.newBuilder();
//        builder.add(PARAM_APP, appType);
//        builder.add(PARAM_NONCESTR, noncestr);
//        builder.add(PARAM_OS, clientType);
        builder.add(PARAM_REQ_TIME, reqTime);
        if (sid != null)
            builder.add("Cookie", SID.concat("=").concat(sid));
        return builder.build();
    }

    @SuppressLint("SimpleDateFormat")
    private static String getRequestTime() {

        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();

        SimpleDateFormat format = new SimpleDateFormat(tf);
//        return format.format(date);
        return String.valueOf(date.getTime());
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getUserId() {
        return userId;
    }

    public RequestBody getRequestBody(RequestBody requestBody) {
        StringBuilder paramString = new StringBuilder();
        paramString.append(PARAM_DEVICETOKEN).append("=").append(deviceToken);
        if (!TextUtils.isEmpty(deviceType))
            paramString.append("&").append(PARAM_DEVICETYPE).append("=").append(deviceType);
        if (!TextUtils.isEmpty(userId))
            paramString.append("&").append(PARAM_USER_ID).append("=").append(userId);
        if (requestBody instanceof FormBody) {
            FormBody formBody = (FormBody) requestBody;
            int paramSize = formBody.size();
            for (int i = 0; i < paramSize; i++) {
                if (i < paramSize) {
                    paramString.append("&");
                }
                paramString.append(formBody.name(i)).append("=").append(formBody.value(i));
            }

        } else if (requestBody instanceof MultipartBody) {
            MultipartBody formBody = (MultipartBody) requestBody;


            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(formBody.type());
            if (!TextUtils.isEmpty(deviceType))
                builder.addFormDataPart(PARAM_DEVICETYPE, deviceType);
            if (!TextUtils.isEmpty(userId))
                builder.addFormDataPart(PARAM_USER_ID, userId);
            builder.addFormDataPart(PARAM_DEVICETOKEN, deviceToken);
            for (MultipartBody.Part part : formBody.parts()) {
                builder.addPart(part);
            }

            return builder.build();
        }


        return RequestBody.create(requestBody.contentType(), paramString.toString());
    }

    public HttpUrl getHttpUrl(HttpUrl httpUrl) {
        HttpUrl.Builder builder = httpUrl.newBuilder()
                .addQueryParameter(PARAM_DEVICETOKEN, this.deviceToken);
        if (!TextUtils.isEmpty(deviceType))
            builder.addQueryParameter(PARAM_DEVICETYPE, deviceType);
        if (!TextUtils.isEmpty(userId))
            builder.addQueryParameter(PARAM_USER_ID, this.userId);

        return builder.build();
    }

    /**
     * 得到全局唯一UUID
     */
    public String getUUID(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String m_szImei = "";

        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        String m_szDevIDShort = "35" + //we make this look like a valid IMEI

                Build.BOARD.length() % 10 +
                Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 +
                Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 +
                Build.HOST.length() % 10 +
                Build.ID.length() % 10 +
                Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 +
                Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 +
                Build.TYPE.length() % 10 +
                Build.USER.length() % 10; //13 digits


        String m_szWLANMAC = "";

		/*BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		String m_szBTMAC="";
		if(m_BluetoothAdapter!=null)
			m_szBTMAC = m_BluetoothAdapter.getAddress();*/

        String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
        // get md5 bytes
        byte p_md5Data[] = m.digest();
        // create a hex string
        String m_szUniqueID = "";
        for (int i = 0; i < p_md5Data.length; i++) {
            int b = (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF)
                m_szUniqueID += "0";
            // add number to string
            m_szUniqueID += Integer.toHexString(b);
        }   // hex string to uppercase
        m_szUniqueID = m_szUniqueID.toUpperCase();
        return m_szUniqueID;
    }
}
