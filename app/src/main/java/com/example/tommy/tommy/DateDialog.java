package com.example.tommy.tommy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import static android.R.attr.format;

/**
 * Created by tomho on 07/08/2017.
 */

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private EditText txtDate;
    private static final DateFormat sqlFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat appFormat = new SimpleDateFormat("dd/MM/yyyy");

    public DateDialog(View view) {
        txtDate = (EditText) view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        String date = day + "/" + (month + 1) + "/" + year;
        txtDate.setText(date);
        txtDate.setError(null);
    }

    public static String convertDateToSqlFormat(String str) {
        return convertDate(str, appFormat, sqlFormat);
    }

    public static String convertDateFromSqlFormat(String str) {
        return convertDate(str, sqlFormat, appFormat);
    }

    private static String convertDate(String str, DateFormat inFormat, DateFormat outFormat) {
        Date date;
        try {
            date = inFormat.parse(str);
        } catch (ParseException e) {
            date = Calendar.getInstance().getTime();
        }

        return outFormat.format(date);
    }
}