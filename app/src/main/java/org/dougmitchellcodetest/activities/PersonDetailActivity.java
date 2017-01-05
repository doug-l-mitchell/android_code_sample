package org.dougmitchellcodetest.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.dougmitchellcodetest.R;
import org.dougmitchellcodetest.ThisApp;
import org.dougmitchellcodetest.common.Events;
import org.dougmitchellcodetest.fragments.DatePickerFragment;
import org.dougmitchellcodetest.model.Person;
import org.dougmitchellcodetest.services.PersonRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by dougmitchell on 11/2/16.
 */

public class PersonDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.first_name) EditText firstName;
    @BindView(R.id.last_name) EditText lastName;
    @BindView(R.id.phone) EditText phone;
    @BindView(R.id.date_of_birth) EditText dateOfBirth;
    @BindView(R.id.zip_code) EditText zip;
    @BindView(R.id.done) Button doneButton;
    @BindView(R.id.save) Button saveButton;
    @BindView(R.id.layout_container) View layoutContainer;

    @Inject PersonRepository repository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormat.mediumDate();
    private Person person;

    private InputMethodManager inputMethodManager;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ThisApp) getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.activity_person_detail);
        ButterKnife.bind(this);

        Events.click(doneButton)
                .subscribe(new Action1<View>() {
                    @Override public void call(View view) {
                        finish();
                    }
                });

        Events.click(saveButton)
                .subscribe(new Action1<View>() {
                    @Override public void call(View view) {
                    saveButton.setEnabled(false);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    if (!person.canBeSaved()) {
                        Snackbar snackbar = Snackbar.make(layoutContainer, R.string.cannot_save_person, Snackbar.LENGTH_LONG);
                        snackbar.getView().setBackgroundColor(ContextCompat.getColor(PersonDetailActivity.this, R.color.colorAccent));
                        snackbar.show();
                    } else {
                        repository.save(person);
                    }
                    }
                });

        // this view is used to add, view or edit a Person object. A valid Person object will have
        // an id. If not, we are adding a new Person
        long contactId = getIntent().getLongExtra("id", 0);
        if (contactId > 0) {
            person = repository.get(contactId);
        } else {
            person = new Person();
        }

        bindPersonData();

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // the DatePicker dialog is used for input for date of birth instead
        // of the keyboard
        Events.touchUp(dateOfBirth)
                .subscribe(new Action1<View>() {
                    @Override public void call(View view) {
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        DatePickerFragment frag = new DatePickerFragment();
                        if (person.getDateOfBirth() != null && !person.getDateOfBirth().isEmpty())
                            frag.setDate(dateTimeFormatter.parseDateTime(person.getDateOfBirth()));
                        frag.setListener(PersonDetailActivity.this);
                        frag.show(getSupportFragmentManager(), "DatePicker");
                    }
                });

        // auto format the user input in the phone number field
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // watch for changes to know if we should update the database
        Observable.merge(
                Events.onChange(firstName).map(new Func1<String, Boolean>() {
                    @Override public Boolean call(String text) {
                        boolean changed = !text.equals(person.getFirstName());
                        if (changed)
                            person.setFirstName(firstName.getText().toString());
                        return changed;
                    }
                }),
                Events.onChange(lastName).map(new Func1<String, Boolean>() {
                    @Override public Boolean call(String text) {
                        boolean changed = !text.equals(person.getLastName());
                        person.setLastName(lastName.getText().toString());
                        return changed;
                    }
                }),
                Events.onChange(phone).map(new Func1<String, Boolean>() {
                    @Override public Boolean call(String text) {
                        boolean changed = !text.equals(person.getPhone());
                        if (changed)
                            person.setPhone(phone.getText().toString());
                        return changed;
                    }
                }),
                Events.onChange(dateOfBirth).map(new Func1<String, Boolean>() {
                    @Override public Boolean call(String text) {
                        boolean changed = !text.equals(person.getDateOfBirth());
                        if (changed)
                            person.setDateOfBirth(dateOfBirth.getText().toString());
                        return changed;
                    }
                }),
                Events.onChange(zip).map(new Func1<String, Boolean>() {
                    @Override public Boolean call(String text) {
                        boolean changed = !text.equals(person.getZip());
                        if (changed)
                            person.setZip(zip.getText().toString());
                        return changed;
                    }
                })
        ).filter(new Func1<Boolean, Boolean>() {
            @Override public Boolean call(Boolean somethingHasChanged) {
                return somethingHasChanged;
            }
        })
                .throttleLast(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override public void call(Boolean aBoolean) {
                        saveButton.setEnabled(true);
                    }
                });
    }

    private void bindPersonData() {
        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
        phone.setText(person.getPhone());
        if (person.getDateOfBirth() != null && !person.getDateOfBirth().isEmpty()) {
            DateTime dt = dateTimeFormatter.parseDateTime(person.getDateOfBirth());
            dateOfBirth.setText(dateTimeFormatter.print(dt));
        }
        zip.setText(person.getZip());
        saveButton.setEnabled(false);
    }

    @Override public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // deal with a bit of weirdness with the DateTimePicker on API 23
        if(month == 0) month = 1;
        if(dayOfMonth == 0) dayOfMonth = 1;
        DateTime dt = new DateTime(year, month, dayOfMonth, 0, 0);
        dateOfBirth.setText(dateTimeFormatter.print(dt));
    }
}
