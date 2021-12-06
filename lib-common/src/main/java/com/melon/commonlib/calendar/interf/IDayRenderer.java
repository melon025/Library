package info.emm.commonlib.calendar.interf;

import android.graphics.Canvas;

import info.emm.commonlib.calendar.view.Day;


/**
 * Created by ldf on 17/6/26.
 */

public interface IDayRenderer {

    void refreshContent();

    void drawDay(Canvas canvas, Day day);

    IDayRenderer copy();

}
