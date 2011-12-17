package org.motechproject.tamadatasetup.domain;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class PillReminderDataSetupConfiguration extends DataSetupConfiguration {
    @Autowired
    public PillReminderDataSetupConfiguration(@Qualifier("pillReminderDataSetup") Properties properties) {
        super(properties);
        validate();
    }

    private void validate() {
        percentageOfPillTaken();
        numberOfDaysToRunFor();
        morningDoseTime();
        eveningDoseTime();
        pinNumber();
        dosageId();
        patientDocId();
        signedUpForOutboxCall();
        bestCallTime();
        treatmentAdviceGivenDate();
        startFromDaysAfterTreatmentAdvice();
    }

    public int percentageOfPillTaken() {
        return intValue("percentageOfPillTaken");
    }

    public int numberOfDaysToRunFor() {
        return intValue("numberOfDaysToRunFor");
    }

    public Time morningDoseTime() {
        return timeValue("morningDoseTime", null);
    }

    public Time eveningDoseTime() {
        return timeValue("eveningDoseTime", null);
    }

    public String pinNumber() {
        return stringValue("pinNumber");
    }

    public String dosageId() {
        return stringValue("dosageId");
    }

    public String patientDocId() {
        return stringValue("patientDocId");
    }

    public boolean signedUpForOutboxCall() {
        return booleanValue("signedUpForOutboxCall");
    }

    public Time bestCallTime() {
        return timeValue("bestCallTime", null);
    }

    public LocalDate treatmentAdviceGivenDate() {
        return dateValue("treatmentAdviceGivenDate");
    }

    public int startFromDaysAfterTreatmentAdvice() {
        return intValue("startFromDaysAfterTreatmentAdvice");
    }
}
