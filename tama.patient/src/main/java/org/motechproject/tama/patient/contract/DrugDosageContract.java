package org.motechproject.tama.patient.contract;

import lombok.Data;
import org.motechproject.tama.common.TAMAConstants;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
public class DrugDosageContract {

    private String drugId;
    private String drugName;
    private String brandId;
    private String dosageType;
    private Integer offsetDays = 0;
    private String morningTime;
    private String eveningTime;
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-", pattern = TAMAConstants.DATE_FORMAT)
    private Date endDate;

    private String advice;
    private String mealAdvice;
}
