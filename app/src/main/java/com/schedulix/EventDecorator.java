package com.schedulix;



import android.content.Context;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Nsol on 4/21/2017.
 */

public class EventDecorator implements DayViewDecorator {

    private static final String TAG = EventDecorator.class.getSimpleName();
    private final int color;
    private final HashSet<CalendarDay> dates;
    Context c;
    public EventDecorator(int color, Collection<CalendarDay> dates,Context context) {

        this.color = color;
        this.dates = new HashSet<>(dates);
        this.c = context;
        Log.d(TAG, "EventDecorator: SIze"+this.dates.size());

    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        Log.d(TAG, "shouldDecorate: "+dates.contains(day)+"-"+day.toString());
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setDaysDisabled(false);
        view.setBackgroundDrawable(c.getResources().getDrawable(R.drawable.round_calendar));
    }
}
