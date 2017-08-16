package com.editorapendragon.boover;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Josue on 29/01/2017.
 */

public class PickerDialogs extends DialogFragment implements
        DatePickerDialog.OnDateSetListener{

    private TextView datBirthday;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog;
        dialog = new DatePickerDialog(getActivity(),this,year,month,day);
        return dialog;
    }
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
        datBirthday = (TextView) getActivity().findViewById(R.id.datBirthday);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DATE_FIELD);
        datBirthday.setText(dateFormat.format(cal.getTime()));
        Globals.day = dayOfMonth;
        Globals.month = month;
        Globals.year = year;
    }

}


