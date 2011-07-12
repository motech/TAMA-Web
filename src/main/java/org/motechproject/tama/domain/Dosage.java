package org.motechproject.tama.domain;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@RooJavaBean
@RooToString
@RooEntity
public class Dosage extends BaseEntity {

    @NotNull
    private String dosageTypeId;

    private Set<Date> schedules = new HashSet<Date>();
    
    public String getDosageTypeId() {
        return this.dosageTypeId;
    }
    
    public void setDosageTypeId(String dosageTypeId) {
        this.dosageTypeId = dosageTypeId;
    }
    
    public Set<Date> getSchedules() {
        return this.schedules;
    }

    public void setSchedules(Set<Date> schedules) {
        this.schedules = schedules;
    }
}
