package org.motechproject.tama.dailypillreminder.mapper;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.tama.common.util.TimeUtil;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static ch.lambdaj.Lambda.convert;

@Component
public class PillRegimenRequestMapper {

    @Autowired
    private AllDrugs allDrugs;

    @Value("#{dailyPillReminderProperties['pill.window.hrs']}")
    private Integer pillWindow;

    @Value("#{ivrProperties['retry.interval.mins']}")
    private Integer retryInterval;

    @Value("#{dailyPillReminderProperties['reminder.lag.mins']}")
    private Integer reminderLag;

    public PillRegimenRequestMapper(AllDrugs allDrugs, Integer pillWindow, Integer retryInterval, Integer reminderLag) {
        this.allDrugs = allDrugs;
        this.pillWindow = pillWindow;
        this.retryInterval = retryInterval;
        this.reminderLag = reminderLag;
    }

    public PillRegimenRequestMapper() {
        super();
    }

    public DailyPillRegimenRequest map(Patient patient, TreatmentAdvice treatmentAdvice) {
        final Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest = new DrugDosageMedicineRequestConverter(patient);
        final Converter<String, DosageRequest> dosageRequestFromSchedule = new DosageTimeDosageRequestConverter(treatmentAdvice, drugDosageToMedicineRequest);
        final Set<String> dosageSchedule = treatmentAdvice.groupDosagesByTime().keySet();
        return new DailyPillRegimenRequest(treatmentAdvice.getPatientId(), pillWindow, retryInterval, convert(dosageSchedule, dosageRequestFromSchedule));
    }

    protected class DrugDosageMedicineRequestConverter implements Converter<DrugDosage, MedicineRequest> {
        private Patient patient;

        public DrugDosageMedicineRequestConverter(Patient patient) {
            this.patient = patient;
        }

        @Override
        public MedicineRequest convert(DrugDosage drugDosage) {
            LocalDate dosageStartDate = patient.getDosageStartDate(drugDosage);
            return new MedicineRequest(allDrugs.get(drugDosage.getDrugId()).fullName(drugDosage.getBrandId()), dosageStartDate, drugDosage.getEndDate());
        }
    }

    private class DosageTimeDosageRequestConverter implements Converter<String, DosageRequest> {
        private final TreatmentAdvice treatmentAdvice;
        private final Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest;

        public DosageTimeDosageRequestConverter(TreatmentAdvice treatmentAdvice, Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest) {
            this.treatmentAdvice = treatmentAdvice;
            this.drugDosageToMedicineRequest = drugDosageToMedicineRequest;
        }

        @Override
        public DosageRequest convert(String dosageTime) {
            TimeUtil timeUtil = new TimeUtil(dosageTime).withReminderLagTime(reminderLag);
            List<DrugDosage> dosages = treatmentAdvice.groupDosagesByTime().get(dosageTime);
            return new DosageRequest(timeUtil.getHours(), timeUtil.getMinutes(), Lambda.convert(dosages, drugDosageToMedicineRequest));
        }
    }
}