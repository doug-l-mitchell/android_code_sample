package org.dougmitchellcodetest.services.impl;


import org.dougmitchellcodetest.model.DaoSession;
import org.dougmitchellcodetest.model.Person;
import org.dougmitchellcodetest.model.PersonDao;
import org.dougmitchellcodetest.services.PersonRepository;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by dougmitchell on 11/2/16.
 *
 * A standard database respository implementation
 */

public class PersonRepositoryImpl implements PersonRepository {

    private PersonDao personDao;
    private PublishSubject updatesSubject;

    @Inject
    public PersonRepositoryImpl(DaoSession dbSession) {
        personDao = dbSession.getPersonDao();
        updatesSubject = PublishSubject.create();
    }

    @Override public List<Person> getAll() {
        return personDao.loadAll();
    }

    @Override public Person get(long id) {
        return personDao.load(id);
    }

    @Override public void save(Person person) {
        if (person.getId() == null || person.getId() < 1)
            personDao.insert(person);

        personDao.save(person);
        updatesSubject.onNext(null);
    }

    @Override public void remove(Person person) {
        personDao.delete(person);
    }

    @Override public Observable updatesObservable() {
        return updatesSubject.asObservable();
    }
}
