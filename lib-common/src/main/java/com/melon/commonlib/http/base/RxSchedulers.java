package info.emm.commonlib.http.base;


import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import info.emm.commonlib.http.bean.BaseRespose;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Z on 2017/11/21.
 */

public class RxSchedulers<T> {

    private static final String TAG = RxSchedulers.class.getSimpleName();

    public static <T> ObservableTransformer<T, T> io_main() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> handleResult() {


        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {


                return upstream.flatMap(new Function<T, ObservableSource<T>>() {
                    @Override
                    public ObservableSource<T> apply(T t) throws Exception {
                        String result = (String) getValueFromBean(t, "result");
                        if (result != null && result.equals("0")) {
                            return createData(t);
                        } else {
                            String message = (String) getValueFromBean(t, "message");
                            return Observable.error(new ServerException(message==null?result:message));
                        }

                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }


        };
    }


    private static <T> Observable<T> createData(final T t) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter)  {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }

        });
    }


    private static Object getValueFromBean(Object bean, String fildName) {
        return getMethodValue(bean, getBeanMethodNameGet(fildName));
    }

    //获取javaBean的get方法名
    private static String getBeanMethodNameGet(String feildName) {
        feildName = feildName.toUpperCase().charAt(0) + feildName.substring(1);
        return "get" + feildName;
    }

    private static <T> Object getMethodValue(T dataBean, String methodName) {
        Class c = dataBean.getClass();
        Object value = null;
        try {
            Method method = c.getDeclaredMethod(methodName);

            value = method.invoke(dataBean, (Object[]) null);
        } catch (NoSuchMethodException e) {
            // e.printStackTrace();
        } catch (InvocationTargetException e) {
            // e.printStackTrace();
        } catch (IllegalAccessException e) {
            //  e.printStackTrace();
        }
        return value;
    }
}
