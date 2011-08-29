 package org.motechproject.tama.domain;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.motechproject.tama.domain.AilmentDefinition.*;

public enum SystemCategoryDefiniton {

    Allergic("Allergic/Immunologic", new Ailments().setAilments(Arrays.asList(Asthma.getAilment(),
            Psoriasis.getAilment(),
            HayFever.getAilment(),
            Sinusitis.getAilment(),
            Hives.getAilment(),
            AllergicDermatitis.getAilment(),
            AtopicDermatitis.getAilment(),
            InheritedChildHoodEczema.getAilment()
    )).setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Dermatological("Dermatological", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Respiratory("Respiratory", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Cardiovascular("Cardiovascular", new Ailments().setAilments(Arrays.asList(CoronaryDisease.getAilment()))
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Endocrine("Endocrine", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Gastrointestinal("Gastrointestinal", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    GenitoUrinary("Genito-urinary", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Hematological("Hematological", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    MusculoSkeletal("Musculo-skeletal", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Neurological("Neurological", new Ailments().setAilments(Arrays.asList(Dizziness.getAilment(),
            Insomnia.getAilment(),
            ImpairedConcentration.getAilment(),
            Somnolence.getAilment(),
            Nightmares.getAilment()
    )).setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Psychiatric("Psychiatric", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Eyes("Eyes", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    ENT("Ears, Nose, Throat", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Other("Other", new Ailments().setAilments(Arrays.asList(Hypertension.getAilment(),
            Nephrotoxicity.getAilment(),
            Diabetes.getAilment(),
            Tuberculosis.getAilment(),
            Alcoholism.getAilment()))
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment(),
                    OtherAilment.newOtherAilment(),
                    OtherAilment.newOtherAilment())));

    private String categoryName;
    private Ailments ailments;

    SystemCategoryDefiniton(String categoryName, Ailments ailments) {
        this.categoryName = categoryName;
        this.ailments = ailments;
    }

    public Ailments getAilments() {
        return this.ailments;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    private SystemCategory getSystemCategory() {
        return SystemCategory.newSystemCategory(this.getCategoryName(), getAilments());
    }

    public static  List<SystemCategory> all() {
        List<SystemCategory> systemCategories = new LinkedList<SystemCategory>();
        for(SystemCategoryDefiniton definiton : SystemCategoryDefiniton.values()) {
           systemCategories.add(definiton.getSystemCategory());
        }
        return systemCategories;
    }


}
