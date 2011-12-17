package org.motechproject.tama.common.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.joda.time.LocalDate;

import java.util.Date;

public abstract class BaseEntity {
    protected Date toDate(LocalDate date) {
        if (date == null) return null;
        return date.toDate();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
