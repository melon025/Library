package info.emm.commonlib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import info.emm.commonlib.utils.ToastUitl;

public class QuickIndexBar extends View {
    private Paint mPaint;
    private int mCellWidth;
    private float mCellHeight;
    private Rect mRect;
    private String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","#"};

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        //创建画笔  取消锯齿
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置颜色和字体粗细
        mPaint.setColor(Color.GRAY);
//        mPaint.setTypeface(Typeface.DEFAULT_BOLD);//粗体
        mPaint.setTextSize(sp2px(context, 14));
        mRect = new Rect();
    }
    private  int sp2px(Context context,float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public interface OnLetterChangedListener {
        public void onLetterChanged(String letter);
    }

    private OnLetterChangedListener onLetterChangedListener;

    public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
        this.onLetterChangedListener = onLetterChangedListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < letters.length; i++) {
            //要绘制的文本
            String letter = letters[i];
            //计算文本的宽高
            mPaint.getTextBounds(letter, 0, 1, mRect);
            //获取文本的宽高
            int textWidth = mRect.width();
            int textHeight = mRect.height();
            //修改当前选中字体的颜色
            mPaint.setColor(currentIndex == i ? Color.BLUE : Color.GRAY);
            //计算坐标
            float x = mCellWidth * 0.5f - textWidth * 0.5f;
            float y = mCellHeight * 0.5f + textHeight * 0.5f + mCellHeight * i;
            canvas.drawText(letter, x, y, mPaint);
        }
    }

    //在测量完成之后执行
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //获取当前控件的宽高
        mCellWidth = getMeasuredWidth();
        int height = getMeasuredHeight();
        mCellHeight = height * 1.0f / letters.length;
    }

    private int currentIndex = -1;
    private int pretIndex = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
               float y = event.getY();
                //计算按下的索引
                int index = (int) (y / mCellHeight);
                if (index >=0 && index<=letters.length-1){
                    ToastUitl.showShort(letters[index]);
                }
            case MotionEvent.ACTION_MOVE:
                float y1 = event.getY();
                //保存上一次索引
                pretIndex = currentIndex;
                //计算按下的索引
                currentIndex = (int) (y1 / mCellHeight);
                if (currentIndex >= 0 && currentIndex <= letters.length - 1) {
                    ToastUitl.showShort(letters[currentIndex]);
                    if (onLetterChangedListener != null && pretIndex != currentIndex) {
                        onLetterChangedListener.onLetterChanged(letters[currentIndex]);
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //重置
                currentIndex = -1;
                invalidate();
                break;
        }
        return true;
    }
}
