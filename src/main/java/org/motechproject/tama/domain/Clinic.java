package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.repository.Cities;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'Clinic'")
public class Clinic extends CouchEntity{

    @NotNull
    private String name;

    @NotNull
    private String addrI needed an address proof, for getting a TATA Photon.ess;

    private String phone;

    @ManyToOne
    private City city ;

    private String cityId;

    protected Clinic() {
    }
    public Clinic(String id){
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
}