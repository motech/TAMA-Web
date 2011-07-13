package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.repository.Cities;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Configurable
@TypeDiscriminator("doc.documentType == 'Clinic'")
public class Clinic extends CouchEntity{

    @Autowired
    private Clinics clinics;

    @Autowired
    private Cities cities;

    @NotNull
    private String name;

    @NotNull
    private String address;

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

    public Clinics allClinics() {
        return clinics;
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
        if(this.city != null) return city;
        if(this.cityId != null) return cities.get(cityId);
        return null;
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