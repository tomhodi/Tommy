package com.example.tommy.tommy;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A date picker dialog for the date_of_birth field of the registration page.
 * Provides useful methods for converting date's strings from SQL format ("yyyy-M-d") to
 * more convenient one ("d/M/yyyy) and backwards.
 */

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final DateFormat sqlFormat = new SimpleDateFormat("yyyy-M-d");
    private static final DateFormat appFormat = new SimpleDateFormat("d/M/yyyy");

    private EditText txtDate;

    public DateDialog(View view) {
        txtDate = (EditText) view;
    }

    /**
     * Extract the savedInstanceState date parameters and return a new DatePickerDialog.
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    /**
     * Sets the date to the txtDate EditText.
     */
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //show to the selected date in the text box
        String date = day + "/" + (month + 1) + "/" + year;
        txtDate.setText(date);
        txtDate.setError(null);
    }

    /**
     * Converts date to 'yyyy-M-d' format.
     */
    public static String convertDateToSqlFormat(String str) {
        return convertDate(str, appFormat, sqlFormat);
    }

    /**
     * Converts date from 'd/M/yyyy' format.
     */
    public static String convertDateFromSqlFormat(String str) {
        return convertDate(str, sqlFormat, appFormat);
    }

    /**
     * Parses the date string and returns true iff the date is in a valid format.
     */
    public static boolean validateDate(String str) {
        try {
            Date date = appFormat.parse(str);
            return appFormat.format(date).equals(str);
        } catch (ParseException e) {
            return false;
        }
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