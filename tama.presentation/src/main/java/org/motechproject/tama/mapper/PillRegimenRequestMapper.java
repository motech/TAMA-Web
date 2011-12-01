package org.motechproject.tama.mapper;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

import org.joda.time.LocalDate;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.tamacommon.util.TimeUtil;
import org.motechproject.tamadomain.domain.DrugDosage;
import org.motechproject.tamadomain.domain.TreatmentAdvice;
import org.motechproject.tamadomain.repository.AllDrugs;
import org.motechproject.tamacallflow.util.DosageUtil;
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

    @Value("#{ivrProperties['pill.window.hrs']}")
    private Integer pillWindow;

    @Value("#{ivrProperties['retry.interval.mins']}")
    private Integer retryInterval;

    @Value("#{ivrProperties['reminder.lag.mins']}")
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

    public DailyPillRegimenRequest map(final TreatmentAdvice treatmentAdvice) {
        final Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest = new DrugDosageMedicineRequestConverter(false);
        final Converter<DrugDosage, MedicineRequest> drugEveningDosageToMedicineRequest = new DrugDosageMedicineRequestConverter(true);
        final Converter<String, DosageRequest> dosageRequestFromSchedule = new DosageTimeDosageRequestConverter(treatmentAdvice, drugDosageToMedicineRequest, drugEveningDosageToMedicineRequest);
        final Set<String> dosageSchedule = treatmentAdvice.groupDosagesByTime().keySet();
        return new DailyPillRegimenRequest(treatmentAdvice.getPatientId(),
                pillWindow,
                retryInterval,
                convert(dosageSchedule, dosageRequestFromSchedule));
    }

    protected class DrugDosageMedicineRequestConverter implements Converter<DrugDosage, MedicineRequest> {
    	
    	boolean addOffsetDays;
    	
    	public DrugDosageMedicineRequestConverter(boolean addOffsetDays) {
    		this.addOffsetDays = addOffsetDays;
		}
        @Override
        public MedicineRequest convert(DrugDosage drugDosage) {
            LocalDate startDate = addOffsetDays?drugDosage.getStartDate().plusDays(drugDosage.getOffsetDays()):drugDosage.getStartDate();
			return new MedicineRequest(allDrugs.get(drugDosage.getDrugId()).fullName(drugDosage.getBrandId()),
                    startDate,
                    drugDosage.getEndDate());
        }
    }

    private class DosageTimeDosageRequestConverter implements Converter<String, DosageRequest> {
        private final TreatmentAdvice treatmentAdvice;
        private final Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest;
        private final Converter<DrugDosage, MedicineRequest> drugEveningDosageToMedicineRequest;

        public DosageTimeDosageRequestConverter(TreatmentAdvice treatmentAdvice,
				Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest,
				Converter<DrugDosage, MedicineRequest> drugEveningDosageToMedicineRequest) {
            this.treatmentAdvice = treatmentAdvice;
            this.drugDosageToMedicineRequest = drugDosageToMedicineRequest;
            this.drugEveningDosageToMedicineRequest = drugEveningDosageToMedicineRequest;
        }

		@Override
        public DosageRequest convert(String dosageTime) {
            TimeUtil timeUtil = new TimeUtil(dosageTime).withReminderLagTime(reminderLag);
            List<DrugDosage> dosages = treatmentAdvice.groupDosagesByTime().get(dosageTime);
			Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequestConverter =
					DosageUtil.isEveningDosage(dosageTime)?drugEveningDosageToMedicineRequest: drugDosageToMedicineRequest;
			return new DosageRequest(timeUtil.getHours(),
                    timeUtil.getMinutes(),
                    Lambda.convert(dosages,
                            drugDosageToMedicineRequestConverter));
        }
    }
}