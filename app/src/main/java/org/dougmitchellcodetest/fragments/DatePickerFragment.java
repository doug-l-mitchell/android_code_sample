package org.dougmitchellcodetest.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import org.joda.time.DateTime;

/**
 * Created by dougmitchell on 11/2/16.
 */

public class DatePickerFragment extends DialogFragment {

    int year, month, day = 0;

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    DatePickerDialog.OnDateSetListener listener;

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(year == 0 && month == 0 && day == 0) {
            DateTime dt = new DateTime();
            year = dt.getYear();
            month = dt.getMonthOfYear();
            day = dt.getDayOfMonth();
        }

        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

    public void setDate(DateTime dt) {
        year = dt.getYear();
        month = dt.getMonthOfYear();
        day = dt.getDayOfMonth();
    }
}
