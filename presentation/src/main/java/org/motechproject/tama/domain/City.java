package org.motechproject.tama.domain;

import org.ektorp.support.TypeDiscriminator;

import javax.validation.constraints.NotNull;

@TypeDiscriminator("doc.documentType == 'City'")
public class City extends CouchEntity implements Comparable<City> {

    @NotNull
    private String name;

    protected City() {
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

    public static City newCity(String name) {
        City city = new City();
        city.setName(name);
        return city;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(City o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }
}
