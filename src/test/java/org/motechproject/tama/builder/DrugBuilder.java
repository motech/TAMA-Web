package org.motechproject.tama.builder;

import org.motechproject.tama.domain.Brand;
import org.motechproject.tama.domain.Drug;

import java.util.HashSet;
import java.util.Set;

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

    private DrugBuilder withBrands(Set<Brand> brands) {
        this.drug.setBrands(brands);
        return this;
    }

    public Drug build() {
        return this.drug;
    }

    public static DrugBuilder startRecording() {
        return new DrugBuilder();
    }

    public DrugBuilder withDefaults(){
        Set<Brand> brands = new HashSet<Brand>();
        Brand brand = new Brand("brandName");
        brand.setCompanyId("brandId");
        brands.add(brand);
        return this.withId("drugId").withName("drugName").withBrands(brands);
    }
}
