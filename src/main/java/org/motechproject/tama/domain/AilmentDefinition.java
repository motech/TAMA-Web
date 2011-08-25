package org.motechproject.tama.domain;

public enum AilmentDefinition {

    Asthma("Asthma"),

    Psoriasis("Psoriasis"),

    HayFever("Hay Fever"),

    Sinusitis("Sinusitis"),

    Hives("Hives"),

    AllergicDermatitis("Allergic dermatitis / eczema"),

    AtopicDermatitis("Atopic dermatitis / eczema"),

    InheritedChildHoodEczema("Inherited childhood eczema /dermatitis"),

    CoronaryDisease("Coronary disease"),

    Dizziness("Dizziness"),

    Insomnia("Insomnia"),

    ImpairedConcentration("Impaired concentration"),

    Somnolence("Somnolence"),

    Nightmares("Nightmares"),

    Hypertension("Hypertension"),

    Nephrotoxicity("Nephrotoxicity"),

    Diabetes("Diabetes"),

    Tuberculosis("Tuberculosis"),

    Alcoholism("Alcoholism"),

    others("Others");

    private final String value;

    AilmentDefinition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Ailment getAilment() {
        final Ailment ailment = new Ailment();
        ailment.setDefinition(this);
        return ailment;
    }

}
