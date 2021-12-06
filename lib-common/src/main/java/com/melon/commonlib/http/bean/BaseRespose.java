package info.emm.commonlib.http.bean;

/**
 * Created by Z on 2017/11/10.
 */
public class BaseRespose<T> {


    private int result=-1;
    private String message;
    private T data;

    public int getCode() {
        return result;
    }

    public void setCode(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
