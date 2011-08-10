package org.motechproject.tama.domain;

import org.apache.commons.lang.StringUtils;
import org.ektorp.support.TypeDiscriminator;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.TreeSet;

@TypeDiscriminator("doc.documentType == 'Drug'")
public class Drug extends CouchEntity {

    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Brand> brands = new TreeSet<Brand>();

    public Drug() {
    }

    public Drug(String name) {
        this.name = name;
    }

    public Brand getBrand(String brandId) {
        for (Brand brand : brands) {
            if (brand.getCompanyId().equals(brandId))
                return brand;
        }
        return null;
    }

    public void addBrand(Brand brand) {
        brands.add(brand);
    }

    public void removeBrand(Brand brand) {
        brands.remove(brand);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Brand> getBrands() {
        return new TreeSet<Brand>(this.brands);
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String fullName(String brandId) {
        Brand brand = this.getBrand(brandId);
        return StringUtils.remove(this.name, "+") + "_" + brand.getName();
    }
}
