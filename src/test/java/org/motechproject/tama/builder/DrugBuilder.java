package org.motechproject.tama.builder;

import org.motechproject.tama.domain.Drug;

public class DrugBuilder {

    private Drug drug = new Drug();

    public DrugBuilder withId(String id){
        this.drug.setId(id);
        return this;
    }

    public DrugBuilder withName(String name){
        this.drug.setName(name);
        return this;
    }

    public Drug build() {
        return this.drug;
    }

    public static DrugBuilder startRecording() {
        return new DrugBuilder();
    }

    public DrugBuilder withDefaults(){
        return this.withId("9999999").withName("drugName");
    }
}
