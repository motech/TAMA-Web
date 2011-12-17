package org.motechproject.tamadatasetup.domain;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class FourDayRecallSetupConfiguration extends DataSetupConfiguration {
    @Autowired
    public FourDayRecallSetupConfiguration(@Qualifier("fourDayRecallDataSetup") Properties properties) {
        super(properties);
        validate();
    }

    private void validate() {
        patientDocId();
        pinNumber();
        adherenceResponse();
        startFromWeeksAfterTreatmentAdvice();
        bestCallTime();
        treatmentAdviceGivenDate();
    }

    public String patientDocId() {
        return stringValue("patientDocId");
    }

    public String pinNumber() {
        return stringValue("pinNumber");
    }

    public String adherenceResponse() {
        return stringValue("adherenceResponse");
    }

    public int startFromWeeksAfterTreatmentAdvice() {
        return intValue("startFromWeeksAfterTreatmentAdvice", 0);
    }

    public Time bestCallTime() {
        return timeValue("bestCallTime");
    }

    public LocalDate treatmentAdviceGivenDate() {
        return dateValue("treatmentAdviceGivenDate");
    }
}
