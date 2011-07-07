package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@TypeDiscriminator("doc.documentType == 'City'")
public class City extends CouchEntity{

    @NotNull
    private String name;

    protected City(){
    }

    public City(String id) {
         super();
        this.setId(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static City newCity(String name){
        City city = new City();
        city.setName(name);
        return city;
    }
}
