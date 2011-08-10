package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'Clinic'")
public class Clinic extends CouchEntity implements Comparable<Clinic> {

    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String phone;

    @ManyToOne
    private City city;

    private String cityId;

    protected Clinic() {
    }

    public Clinic(String id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Clinic newClinic() {
        return new Clinic();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonIgnore
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city.getId();
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public int compareTo(Clinic o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }
}