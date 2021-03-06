package org.motechproject.tama.refdata.builder;

import org.motechproject.tama.refdata.domain.Gender;

public class GenderBuilder {

    private Gender gender = new Gender();

    public GenderBuilder withId(String id) {
        gender.setId(id);
        return this;
    }

    public GenderBuilder withType(String type) {
        gender.setType(type);
        return this;
    }

    public Gender build() {
        return this.gender;
    }

    public static GenderBuilder startRecording() {
        return new GenderBuilder();
    }
}
