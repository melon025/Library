package info.emm.commonlib.http.base;

/**
 * Created by Administrator on 2017/3/7.
 */

public class ErrorRespose {
    public String name;
    public String msg;
    public String code;
    public String status;
    public String type;

    @Override
    public String toString() {
        return "ErrorRespose{" +
                "name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
