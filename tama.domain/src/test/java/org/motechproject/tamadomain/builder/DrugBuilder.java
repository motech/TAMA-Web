package org.motechproject.tamadomain.builder;

import org.motechproject.tamadomain.domain.Brand;
import org.motechproject.tamadomain.domain.Drug;

import java.util.Set;
import java.util.TreeSet;

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
        Set<Brand> brands = new TreeSet<Brand>();
        Brand brand = new Brand("brandName");
        brand.setCompanyId("brandId");
        brands.add(brand);
        return this.withId("drugId").withName("drugName").withBrands(brands);
    }
}
