package info.emm.commonlib.widget;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Z on 2018/9/20.
 */

public class OnDoubleClickListener implements View.OnTouchListener{
    private final String TAG = this.getClass().getSimpleName();
    private int count = 0;
    private long firClick = 0;
    private long secClick = 0;
    /**
     * 两次点击时间间隔，单位毫秒
     */
    private final int interval = 300;
    private DoubleClickCallback mCallback;

    public interface DoubleClickCallback {
        void onDoubleClick(View view);
    }

    public OnDoubleClickListener(DoubleClickCallback callback) {
        super();
        this.mCallback = callback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean isTouch =false;
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (1 == count) {
                firClick = System.currentTimeMillis();
            } else if (2 == count) {
                secClick = System.currentTimeMillis();
                if (secClick - firClick < interval) {
                    if (mCallback != null) {
                        mCallback.onDoubleClick(v);
                        isTouch =true;
                    } else {
                        isTouch =false;
                    }
                    count = 0;
                    firClick = 0;
                } else {
                    firClick = secClick;
                    count = 1;
                    isTouch =false;
                }
                secClick = 0;
            }

        }
        return  isTouch;
    }
}
