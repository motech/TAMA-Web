package org.motechproject.tama.domain;

public class ComboBoxView {

    private String id;

    private String displayName;

    public ComboBoxView(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}
