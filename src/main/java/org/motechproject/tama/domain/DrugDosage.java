package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RooJavaBean
@RooEntity
public class DrugDosage {

    @NotNull
    private String drugId;

    @JsonIgnore
    private String drugName;

    @NotNull
    private String brandId;

    @ManyToOne
    private Dosage dosage;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-")
    private Date startDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-")
    private Date endDate;

    @NotNull
    private String advice;

    @NotNull
    private String mealAdviceId;
}
