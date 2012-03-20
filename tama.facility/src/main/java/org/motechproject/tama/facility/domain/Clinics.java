package org.motechproject.tama.facility.domain;

import java.util.ArrayList;
import java.util.Collection;

public class Clinics extends ArrayList<Clinic> {
    public Clinics() {
    }

    public Clinics(Collection<? extends Clinic> c) {
        super(c);
    }

    public Clinic getBy(String id) {
        for (Clinic clinic : this) {
            if (clinic.getId().equals(id))
                return clinic;
        }
        return null;
    }
}