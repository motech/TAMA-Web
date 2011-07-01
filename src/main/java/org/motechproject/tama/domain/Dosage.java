package org.motechproject.tama.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class Dosage {

    @NotNull
    private String dosageTypeId;

    private Set<Date> schedules = new HashSet<Date>();
}
