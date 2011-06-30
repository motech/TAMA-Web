package org.motechproject.tama.builder;

import org.motechproject.tama.domain.Company;

public class CompanyBuilder {

    private Company company = new Company();

    public CompanyBuilder withId(String id) {
        company.setId(id);
        return this;
    }

    public CompanyBuilder withName(String name) {
        company.setName(name);
        return this;
    }

    public Company build() {
        return this.company
                ;
    }

    public static CompanyBuilder startRecording() {
        return new CompanyBuilder();
    }

}
