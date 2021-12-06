package info.emm.commonlib.widget.clip;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

/**
 * Created by Z on 2018/7/9.
 */

public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView mZoomImageView;
    private ClipImageBorderView mClipImageView;
    private int mHorizontalPadding = 20;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mZoomImageView = new ClipZoomImageView(context);
        this.mClipImageView = new ClipImageBorderView(context);
        LayoutParams lp = new LayoutParams(-1, -1);
        this.addView(this.mZoomImageView, lp);
        this.addView(this.mClipImageView, lp);
        this.mHorizontalPadding = (int) TypedValue.applyDimension(1, (float)this.mHorizontalPadding, this.getResources().getDisplayMetrics());
        this.mZoomImageView.setHorizontalPadding(this.mHorizontalPadding);
        this.mClipImageView.setHorizontalPadding(this.mHorizontalPadding);
    }

    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    public Bitmap clip() {
        return this.mZoomImageView.clip();
    }

    public ClipZoomImageView getZoomImageView() {
        return this.mZoomImageView;
    }
}
