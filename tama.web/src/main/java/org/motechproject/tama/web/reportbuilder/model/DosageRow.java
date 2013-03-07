package org.motechproject.tama.web.reportbuilder.model;

import lombok.Data;
import org.motechproject.tama.patient.contract.DrugDosageContract;

import static org.motechproject.tama.common.util.DateFormat.format;

@Data
public class DosageRow {

    private String dosageTypeId;
    private String morningTime;
    private String eveningTime;
    private Integer offsetDays;
    private String startDate;
    private String advice;
    private String mealAdviceId;

    public DosageRow(DrugDosageContract dosageContract) {
        if (null != dosageContract) {
            setAdvice(dosageContract.getAdvice());
            setDosageTypeId(dosageContract.getDosageType());
            setEveningTime(dosageContract.getEveningTime());
            setMealAdviceId(dosageContract.getMealAdvice());
            setMorningTime(dosageContract.getMorningTime());
            setOffsetDays(dosageContract.getOffsetDays());
            setStartDate(format(dosageContract.getStartDate(), "dd/MM/yyyy"));
        }
    }
}
