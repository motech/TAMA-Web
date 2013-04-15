package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.domain.CouchEntity;

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
        if (name.toLowerCase().equals("others") || name.toLowerCase().equals("other")) {
            return 1;
        } else if (o.name.toLowerCase().equals("others") || o.name.toLowerCase().equals("other")) {
            return -1;
        } else {
            return name.toLowerCase().compareTo(o.name.toLowerCase());
        }
    }
}
