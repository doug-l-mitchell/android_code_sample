package org.dougmitchellcodetest;

import org.dougmitchellcodetest.activities.MainActivity;
import org.dougmitchellcodetest.activities.PersonDetailActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by dougmitchell on 11/2/16.
 */

@Singleton
@Component(modules={AppModule.class})
public interface AppComponent {

    void inject(MainActivity act);
    void inject(PersonDetailActivity act);
}
