package com.smove.carbooking.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kunal.Mahajan on 8/4/2018.
 */

public class DateTimePicker {

    public static final long DEFAULT_VALUE_NOT_SELECTED = -1;
    private final DatePickListener datePickListener;
    private Context context;

    private int y = -1, m = -1, d = -1;
    private boolean isDateSelected;
    private boolean isTimeSelected;

    public DateTimePicker(Context context, DatePickListener listener) {
        this.context = context;
        this.datePickListener = listener;
    }

    public void selectDateTime() {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(context, (view, year1, month1, day1) -> {
            y = year1;
            m = month1;
            d = day1;
            isDateSelected = true;
            showTimePicker();
        }, year, month, day);

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis());
        datePicker.show();

        datePicker.setOnDismissListener(dialog -> {
            if (!isDateSelected)
                datePickListener.dateSelected(DEFAULT_VALUE_NOT_SELECTED);
        });
    }

    private void showTimePicker() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(context, (timePicker, h, m) -> {
            isTimeSelected = true;
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, y);
            cal.set(Calendar.MONTH, DateTimePicker.this.m);
            cal.set(Calendar.DAY_OF_MONTH, d);
            cal.set(Calendar.HOUR_OF_DAY, h);
            cal.set(Calendar.MINUTE, m);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Date date = cal.getTime();
            datePickListener.dateSelected(date.getTime());
        }, hour, min, false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!isTimeSelected)
                    datePickListener.dateSelected(DEFAULT_VALUE_NOT_SELECTED);
            }
        });
        dialog.show();
    }

    public interface DatePickListener {
        public void dateSelected(long d);
    }
}
