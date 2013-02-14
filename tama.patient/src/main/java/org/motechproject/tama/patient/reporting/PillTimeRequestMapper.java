package org.motechproject.tama.patient.reporting;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.common.util.TimeUtil;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.reports.contract.PillTimeRequest;

import java.text.NumberFormat;

public class PillTimeRequestMapper {

    private TreatmentAdvice treatmentAdvice;

    public PillTimeRequestMapper(TreatmentAdvice treatmentAdvice) {
        this.treatmentAdvice = treatmentAdvice;
    }

    public PillTimeRequest map() {
        PillTimeRequest request = new PillTimeRequest();
        request.setPatientDocumentId(treatmentAdvice.getPatientId());
        for (DrugDosage dosage : treatmentAdvice.getDrugDosages()) {
            if (StringUtils.isNotBlank(dosage.getMorningTime())) {
                String time = getTwentyFourHourFormat(dosage.getMorningTime());
                request.setMorningPillTime(time);
            }
            if (StringUtils.isNotBlank(dosage.getEveningTime())) {
                String time = getTwentyFourHourFormat(dosage.getEveningTime());
                request.setEveningPillTime(time);
            }
        }
        return request;
    }

    private String getTwentyFourHourFormat(String timeString) {
        TimeUtil util = new TimeUtil(timeString);
        return String.format("%s:%s:00", timeString(util.getHours()), util.getMinutes());
    }

    private String timeString(int timeComponent) {
        NumberFormat twoDigitIntegerFormat = NumberFormat.getInstance();
        twoDigitIntegerFormat.setMinimumIntegerDigits(2);
        return twoDigitIntegerFormat.format(timeComponent);
    }
}
