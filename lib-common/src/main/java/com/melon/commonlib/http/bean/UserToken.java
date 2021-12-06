package info.emm.commonlib.http.bean;

/**
 * Created by Z on 2018/4/27.
 */
public class UserToken {

    private static UserToken instance;

    private String userID;
    private String phone;
    private String token;
    private String nickName;
    private String account;
    private String headerPic;
    private String sex;
    private String mail;
    private String userroleid;
    private String sid;
    private String version;
    private boolean isLoading = false;
    private String deviceType;

    public static UserToken getInstance() {
        if (instance == null) {
            instance = new UserToken();
        }
        return instance;
    }

    public UserToken(String userID, String phone, String token) {
        this.userID = userID;
        this.phone = phone;
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public UserToken() {
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserroleid() {
        return userroleid;
    }

    public void setUserroleid(String userroleid) {
        this.userroleid = userroleid;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    //登录成功设置token
    public void setToken(String token) {
        this.token = token;
    }

    public void release() {
        instance = null;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeaderPic() {
        return headerPic;
    }

    public void setHeaderPic(String headerPic) {
        this.headerPic = headerPic;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"userID\":\"")
                .append(userID).append('\"');
        sb.append(",\"phone\":\"")
                .append(phone).append('\"');
        sb.append(",\"token\":\"")
                .append(token).append('\"');
        sb.append(",\"nickName\":\"")
                .append(nickName).append('\"');
        sb.append(",\"headerPic\":\"")
                .append(headerPic).append('\"');
        sb.append(",\"sex\":\"")
                .append(sex).append('\"');
        sb.append(",\"mail\":\"")
                .append(mail).append('\"');
        sb.append(",\"userroleid\":\"")
                .append(userroleid).append('\"');
        sb.append('}');
        return sb.toString();
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isLoading() {

        return isLoading;
    }

    public void setUserToken(UserToken userToken) {
        instance = userToken;
    }


    public String getDeviceType() {
        return deviceType;
    }
    public void setDeviceType(String type) {
         deviceType=type;
    }
}
