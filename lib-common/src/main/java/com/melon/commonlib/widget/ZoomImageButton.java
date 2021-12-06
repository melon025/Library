package info.emm.commonlib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import info.emm.commonlib.R;

public class ZoomImageButton extends AppCompatImageButton {
    private static final float SCALE = 0.95f;
    private float scale;

    public ZoomImageButton(Context context) {
        this(context, null);
        this.scale = getScale(context, null, 0);
    }

    public ZoomImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.scale = getScale(context, attrs, 0);
    }

    public ZoomImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.scale = getScale(context, attrs, defStyleAttr);
    }

    private float getScale(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ZoomButton, defStyleAttr, 0);
        float scale = a.getFloat(R.styleable.ZoomButton_scale, SCALE);
        a.recycle();
        return scale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation animation = new ScaleAnimation(1.0f, this.scale, 1.0f, this.scale,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(100);
                animation.setFillAfter(true);
                setAnimation(animation);
                startAnimation(animation);
                break;
            case MotionEvent.ACTION_UP:
                Animation animation1 = new ScaleAnimation(this.scale, 1.0f, this.scale, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation1.setDuration(100);
                animation1.setFillAfter(true);
                setAnimation(animation1);
                startAnimation(animation1);
                break;
        }
        return super.onTouchEvent(event);
    }
}
