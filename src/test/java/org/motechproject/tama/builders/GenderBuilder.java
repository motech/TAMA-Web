package org.motechproject.tama.builders;

import org.motechproject.tama.domain.Gender;

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
