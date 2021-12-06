package info.emm.commonlib.http.base;

import android.content.Context;

import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import info.emm.commonlib.R;
import info.emm.commonlib.base.CommonApp;
import info.emm.commonlib.utils.NetWorkUtil;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by Z on 2017/3/6.
 */

public abstract class RxSubscriber<T> extends DisposableObserver<T> {


    private final String TAG = "RxSubscriber";
    private Context context;

    protected RxSubscriber(Context context) {
        this.context = context;
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onStart() {

    }
    @Override
    public void onError(Throwable e) {
//        Log.e(TAG, "onError: " + e.getMessage());
        if (!NetWorkUtil.isNetworkConnected(CommonApp.getAppContext())) {
            _onError(CommonApp.getAppContext().getString(R.string.network_nonet));

        } else if (e instanceof SocketTimeoutException) {
            _onError(CommonApp.getAppContext().getString(R.string.network_runtime));
        } else if (e instanceof TimeoutException || e instanceof ConnectException) {
            _onError(CommonApp.getAppContext().getString(R.string.servers_connection_error));
        } else if (e instanceof JsonSyntaxException) {
            _onError(CommonApp.getAppContext().getString(R.string.json_error));
            //假如导致这个异常触发的原因是服务器的问题，那么应该让服务器知道，所以可以在这里
            //选择上传原始异常描述信息给服务器
        } else if (e instanceof ServerException) {

            String message = e.getMessage();

            _onError(e.getMessage());


        } else {
            //其他错误
            _onError(CommonApp.getAppContext().getString(R.string.net_others));
        }
        onComplete();
    }

    @Override
    public void onNext(T t) {
        //可能是缓存,可能能是无缓存的网络数据
        _onNext(t);
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);
}
