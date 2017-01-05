package org.dougmitchellcodetest.services;

import org.dougmitchellcodetest.model.Person;

import java.util.List;

import rx.Observable;

/**
 * Created by dougmitchell on 11/2/16.
 */

public interface PersonRepository {

    List<Person> getAll();
    Person get(long id);
    void save(Person person);
    void remove(Person person);

    Observable updatesObservable();
}
