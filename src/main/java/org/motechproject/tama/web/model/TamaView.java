package org.motechproject.tama.web.model;

import org.joda.time.LocalDate;

import java.util.Date;

public abstract class TamaView {
    protected Date toDate(LocalDate date) {
        if (date == null) return null;
        return date.toDate();
    }
}
