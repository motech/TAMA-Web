package org.motechproject.tama.patient.domain;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public enum SystemCategoryDefinition {

    Allergic("Allergic/Immunologic", new Ailments().setAilments(Arrays.asList(AilmentDefinition.Asthma.getAilment(),
            AilmentDefinition.Psoriasis.getAilment(),
            AilmentDefinition.HayFever.getAilment(),
            AilmentDefinition.Sinusitis.getAilment(),
            AilmentDefinition.Hives.getAilment(),
            AilmentDefinition.AllergicDermatitis.getAilment(),
            AilmentDefinition.AtopicDermatitis.getAilment(),
            AilmentDefinition.InheritedChildHoodEczema.getAilment()
    )).setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Dermatological("Dermatological", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Respiratory("Respiratory", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Cardiovascular("Cardiovascular", new Ailments().setAilments(Arrays.asList(AilmentDefinition.CoronaryDisease.getAilment()))
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

    Neurological("Neurological", new Ailments().setAilments(Arrays.asList(AilmentDefinition.Dizziness.getAilment(),
            AilmentDefinition.Insomnia.getAilment(),
            AilmentDefinition.ImpairedConcentration.getAilment(),
            AilmentDefinition.Somnolence.getAilment(),
            AilmentDefinition.Nightmares.getAilment()
    )).setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Psychiatric("Psychiatric", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Eyes("Eyes", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    ENT("Ears, Nose, Throat", new Ailments().setAilments(Collections.<Ailment>emptyList())
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment()))),

    Other("Other", new Ailments().setAilments(Arrays.asList(AilmentDefinition.Hypertension.getAilment(),
            AilmentDefinition.Nephrotoxicity.getAilment(),
            AilmentDefinition.Diabetes.getAilment(),
            AilmentDefinition.Tuberculosis.getAilment(),
            AilmentDefinition.Alcoholism.getAilment()))
            .setOtherAilments(Arrays.asList(OtherAilment.newOtherAilment(),
                    OtherAilment.newOtherAilment(),
                    OtherAilment.newOtherAilment())));

    private String categoryName;
    private Ailments ailments;

    SystemCategoryDefinition(String categoryName, Ailments ailments) {
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

    public static List<SystemCategory> all() {
        List<SystemCategory> systemCategories = new LinkedList<>();
        for (SystemCategoryDefinition definiton : SystemCategoryDefinition.values()) {
            systemCategories.add(definiton.getSystemCategory());
        }
        return systemCategories;
    }

    public static List<SystemCategory> allExpressRegistration() {
        List<SystemCategory> systemCategories = new LinkedList<>();
        systemCategories.add(Psychiatric.getSystemCategory());
        systemCategories.add(Other.getSystemCategory());
        return systemCategories;
    }
}
