package org.motechproject.tama.mapper;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllDrugs;
import org.motechproject.tama.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
        final Converter<DrugDosage, MedicineRequest> drugDosageToMedicineRequest = new Converter<DrugDosage, MedicineRequest>() {
            @Override
            public MedicineRequest convert(DrugDosage drugDosage) {
                return new MedicineRequest(allDrugs.get(drugDosage.getDrugId()).fullName(drugDosage.getBrandId()),
                        drugDosage.getStartDate(),
                        drugDosage.getEndDate());
            }
        };
        final Converter<String, DosageRequest> dosageRequestFromSchedule = new Converter<String, DosageRequest>() {
            @Override
            public DosageRequest convert(String dosageTime) {
                TimeUtil timeUtil = new TimeUtil(dosageTime).withReminderLagTime(reminderLag);
                return new DosageRequest(timeUtil.getHours(),
                        timeUtil.getMinutes(),
                        Lambda.convert(treatmentAdvice.groupDosagesByTime().get(dosageTime),
                                drugDosageToMedicineRequest));
            }
        };
        final Set<String> dosageSchedule = treatmentAdvice.groupDosagesByTime().keySet();
        return new DailyPillRegimenRequest(treatmentAdvice.getPatientId(),
                pillWindow,
                retryInterval,
                convert(dosageSchedule, dosageRequestFromSchedule));
    }
}