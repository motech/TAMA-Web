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
import java.util.Set;

@RooJavaBean
@RooEntity
public class DrugDosage {

    @NotNull
    private String drugId;

    @JsonIgnore
    private String drugName;

    @JsonIgnore
    private Set<Brand> brands;

    @NotNull
    private String brandId;

    @NotNull
    private String dosageTypeId;

    @ManyToOne
    private Dosage dosage;

    @NotNull
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = "dd/MM/yyyy")
    private Date endDate;

    private String advice;

    @NotNull
    private String mealAdviceId;
}
