package info.emm.commonlib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout.LayoutParams;

/**
 * <Pre>
 * TODO
 * </Pre>
 *
 * @author cxy
 * @version 1.0
 */
public class CopyDialogAnimation extends Dialog {


    private Window window = null;

    private String widthType ;
    public CopyDialogAnimation(Context context)

    {
        super(context);
    }

    public CopyDialogAnimation(Context context,int theme)
    {
        super(context,theme);
    }
    public CopyDialogAnimation(Context context,int theme,String widthType)

    {
        super(context,theme);
        this.widthType = widthType ;
    }

    public void showDialog(View view, int x, int y, String title) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        this.setTitle(title);
        if("1".equals(widthType))
            getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);// 需要添加的语句
        else
            getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);// 需要添加的语句
        windowDeploy(x, y);

        // 设置触摸对话框意外的地方取消对话框

        setCanceledOnTouchOutside(true);

        show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0); //显示软键盘
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //显示软键

    }
    public void showDialogUrl(View view, int x, int y, String title) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(view);
        this.setTitle(title);
        if("1".equals(widthType))
            getWindow().setLayout(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);// 需要添加的语句
        else
            getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);// 需要添加的语句

        window = getWindow(); // 得到对话框

        WindowManager.LayoutParams wl = window.getAttributes();


        wl.gravity = Gravity.CENTER_VERTICAL; // 设置重力

        window.setAttributes(wl);

        // 设置触摸对话框意外的地方取消对话框

        setCanceledOnTouchOutside(true);

        show();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0); //显示软键盘
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //显示软键

    }

    // 设置窗口显示

    public void windowDeploy(int x, int y) {

        window = getWindow(); // 得到对话框

        //window.setWindowAnimations(R.style.toast_anim); // 设置窗口弹出动画

        // window.setBackgroundDrawableResource(R.color.vifrification);
        // //设置对话框背景为透明

        WindowManager.LayoutParams wl = window.getAttributes();

        // 根据x，y坐标设置窗口需要显示的位置
/*
		wl.x = x; // x小于0左移，大于0右移

		wl.y = y; // y小于0上移，大于0下移
*/
        // wl.alpha = 0.6f; //设置透明度

        wl.gravity = Gravity.BOTTOM; // 设置重力

        window.setAttributes(wl);

    }


}
