package org.dougmitchellcodetest.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by dougmitchell on 11/2/16.
 *
 * This is a collection of static methods that provide RX Observable support for android widget
 * events.
 */
public class Events {

    public static Observable<View> click(View view) {
        final PublishSubject<View> subject = PublishSubject.create();
        view.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                subject.onNext(v);
            }
        });
        return subject;
    }

    public static Observable<String> onChange(EditText editText) {
        final PublishSubject<String> subject = PublishSubject.create();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                subject.onNext(s.toString());
            }
        });

        return subject.asObservable();
    }

    public static Observable<Boolean> blur(View view) {
        final PublishSubject<Boolean> subject = PublishSubject.create();
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override public void onFocusChange(View v, boolean hasFocus) {
                subject.onNext(hasFocus);
            }
        });
        return subject;
    }

    public static Observable<View> touchUp(View view) {
        final PublishSubject<View> subject = PublishSubject.create();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    subject.onNext(v);
                    return true;
                }
                return false;
            }
        });
        return subject;
    }

}
