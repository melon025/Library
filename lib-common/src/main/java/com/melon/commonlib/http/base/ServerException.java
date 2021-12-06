package info.emm.commonlib.http.base;

/**
 * des:服务器请求异常
 */
public class ServerException extends Exception {
    String message;
    private static final int ERROR_3001 = 3001;//服务器过期
    private static final int ERROR_3002 = 3002;//公司已经冻结
    private static final int ERROR_3003 = 3003;//课堂已删除或过期
    private static final int ERROR_4001 = 4001;//该公司不存在
    private static final int ERROR_4002 = 4002;//用户名或密码错误
    private static final int ERROR_4003 = 4003;//课堂名称不允许为空
    private static final int ERROR_4004 = 4004;//时间格式错误
    private static final int ERROR_4005 = 4005;//时间设置有误
    private static final int ERROR_4006 = 4006;//主席密码格式错误
    private static final int ERROR_4007 = 4007;//课堂不存在、课堂无效已被删除、课堂过期
    private static final int ERROR_4008 = 4008;//课堂密码错误
    private static final int ERROR_5000 = 5000;//主席权限已过期

    public ServerException(String result) {
        message =result;
    }

    @Override
    public String getMessage() {
        try {
            int errorCode = Integer.parseInt(message);
            switch (errorCode) {
                case ERROR_3001:
                    message = "服务器过期";
                    break;
                case ERROR_3002:
                    message = "公司已经冻结";
                    break;
                case ERROR_3003:
                    message = "课堂已删除或过期";
                    break;
                case ERROR_4001:
                    message = "该公司不存在";
                    break;
                case ERROR_4002:
                    message = "用户名或密码错误";
                    break;
                case ERROR_4003:
                    message = "课堂名称不允许为空";
                    break;


            }
        } catch (NumberFormatException e) {
        }
        return message;
    }

}
