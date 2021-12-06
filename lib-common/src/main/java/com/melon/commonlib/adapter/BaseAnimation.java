package info.emm.commonlib.adapter;

import android.animation.Animator;
import android.view.View;

/**
 * Created by Z on 2017/12/4.
 */

public interface BaseAnimation {
    Animator[] getAnimators(View view);
}
