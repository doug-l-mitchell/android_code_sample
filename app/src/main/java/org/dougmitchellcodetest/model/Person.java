package org.dougmitchellcodetest.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by dougmitchell on 11/2/16.
 */

@Entity
public class Person {
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String phone;
    private String dateOfBirth;
    ;
    private String zip;

    @Generated(hash = 2086436236)
    public Person(Long id, String firstName, String lastName, String phone,
            String dateOfBirth, String zip) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.zip = zip;
    }

    @Generated(hash = 1024547259)
    public Person() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    @Keep
    public boolean canBeSaved() {
        // We should not save an empty entity.
        // Editing a record by removing all of its data is not the same as deleting it.
        // A new record should have at least first or last name

        return (firstName != null && !firstName.isEmpty()) || (lastName != null && !lastName.isEmpty());
    }
}
