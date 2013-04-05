package org.motechproject.tama.common.domain;

public enum TAMAMessageType {

    PUSHED_MESSAGE("Pushed Message"),
    ALL_MESSAGES("All Messages"),
    FAMILY_AND_CHILDREN("Family and children"),
    NUTRITION_AND_LIFESTYLE("Nutrition and lifestyle"),
    SYMPTOMS("Symptoms"),
    ADHERENCE("Adherence"),
    ART_AND_CD4("ART and CD4"),
    LIVING_WITH_HIV("Living with HIV");


    private String displayName;

    private TAMAMessageType(String displayName) {
        this.displayName = displayName;
    }

    public static TAMAMessageType lookup(String messagesCategory) {
        try {
            return TAMAMessageType.valueOf(messagesCategory);
        } catch (Exception e) {
        }
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }
}
