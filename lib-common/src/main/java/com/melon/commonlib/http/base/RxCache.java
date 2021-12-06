package info.emm.commonlib.http.base;

import android.content.Context;

import org.reactivestreams.Subscriber;

import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Z on 2017/3/13.
 * 缓存
 */

public class RxCache {
    private static String TAG = "RxCache";
    public static int CACHE_STALE_SEC = 60 * 60 * 24 * 31;//缓存时间

    /**
     * 不强制刷新,
     *
     * @param context
     * @param cacheKey
     * @param fromNetwork
     * @param <T>
     * @return
     */
    public static <T> Observable<T> load(final Context context,
                                         final String cacheKey,
                                         Observable<T> fromNetwork) {
        Observable<T> fromCache = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                T cache = (T) ACache.get(context).getAsObject(cacheKey);
                if (cache != null) {
                    emitter.onNext(cache);
                } else {
                    emitter.onComplete();
                }
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        fromNetwork = fromNetwork.map(new Function<T, T>() {
            @Override
            public T apply(T result) throws Exception {
                ACache.get(context).put(cacheKey, (Serializable) result, CACHE_STALE_SEC);
                return result;
            }

        });

        return Observable.concat(fromCache, fromNetwork);
    }

    public static <T> Observable<T> load(final Context context,
                                         final String cacheKey,
                                         final int expireTime,
                                         boolean forceRefresh,
                                         Observable<T> fromNetwork
    ) {
        Observable<T> fromCache = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                T cache = (T) ACache.get(context).getAsObject(cacheKey);
                if (cache != null) {
                    emitter.onNext(cache);
                } else {
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        fromNetwork = fromNetwork.map(new Function<T, T>() {
            @Override
            public T apply(T result) throws Exception {
                ACache.get(context).put(cacheKey, (Serializable) result, expireTime);
                return result;
            }

        });
        //强制刷新则返回接口数据
        if (forceRefresh) {
            return fromNetwork;
        } else {
            //优先返回缓存
            return Observable.concat(fromCache, fromNetwork).firstElement().toObservable().publish();
        }
    }
}
