package org.motechproject.tama.domain;

import javax.validation.constraints.NotNull;

public class Gender extends CouchEntity {

    @NotNull
    private String type;

    public Gender() {
    }

    public Gender(String id) {
        this.setId(id);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static Gender newGender(String type) {
        Gender gender = new Gender();
        gender.setType(type);
        return gender;
    }
}
