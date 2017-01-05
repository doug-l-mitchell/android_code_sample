package org.dougmitchellcodetest;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.dougmitchellcodetest.model.DaoMaster;
import org.dougmitchellcodetest.model.DaoSession;
import org.dougmitchellcodetest.services.PersonRepository;
import org.dougmitchellcodetest.services.impl.PersonRepositoryImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by dougmitchell on 11/2/16.
 */
@Module
public class AppModule {

    private Application app;
    private DaoSession dbSession;

    public AppModule(Application app) {
        this.app = app;

        // establish the database here so that it can be injected into the repository
        SQLiteDatabase db = new  DaoMaster.DevOpenHelper(app, "people_db", null)
                .getWritableDatabase();
        DaoMaster master = new DaoMaster(db);
        dbSession = master.newSession();
    }

    @Provides
    @Singleton
    Application providesApplication() { return app; }

    @Provides
    @Singleton
    PersonRepository providesPersonRepository(PersonRepositoryImpl impl) { return impl; }

    @Provides
    Context providesAppContext() { return app.getApplicationContext(); }

    @Provides
    DaoSession providesDbSession() { return dbSession; }
}
