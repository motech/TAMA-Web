package org.motechproject.tama.mapper;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllDrugs;
import org.motechproject.tama.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.apache.commons.collections.CollectionUtils.*;

@Component
public class PillRegimenRequestMapper {

    @Autowired
    private AllDrugs allDrugs;

    @Autowired
    @Qualifier("ivrProperties")
    private Properties properties;

    public PillRegimenRequestMapper(AllDrugs allDrugs) {
        this.allDrugs = allDrugs;
    }

    public PillRegimenRequestMapper(AllDrugs allDrugs, Properties properties) {
        this.allDrugs = allDrugs;
        this.properties = properties;
    }

    public PillRegimenRequestMapper() {
        super();
    }

    public DailyPillRegimenRequest map(TreatmentAdvice treatmentAdvice) {
        return new DailyPillRegimenRequest(treatmentAdvice.getPatientId(),
                Integer.valueOf(properties.getProperty(TAMAConstants.PILL_WINDOW)),
                Integer.valueOf(properties.getProperty(TAMAConstants.RETRY_INTERVAL)),
                mapDosageRequests(treatmentAdvice));
    }

    private List<DosageRequest> mapDosageRequests(TreatmentAdvice treatmentAdvice) {
        Map<String, List<DrugDosage>> drugDosagesMap = createDosageMap(treatmentAdvice);
        return createDosageRequests(drugDosagesMap);
    }

    private List<DosageRequest> createDosageRequests(Map<String, List<DrugDosage>> drugDosagesMap) {
        List<DosageRequest> dosageRequests = new ArrayList<DosageRequest>();
        for (Iterator<String> it = drugDosagesMap.keySet().iterator(); it.hasNext(); ) {
            String schedule = it.next();
            List<DrugDosage> drugDosages = drugDosagesMap.get(schedule);
            DosageRequest dosageRequest = createDosageRequest(schedule, drugDosages);
            dosageRequests.add(dosageRequest);
        }
        return dosageRequests;
    }

    private Map<String, List<DrugDosage>> createDosageMap(TreatmentAdvice treatmentAdvice) {
        Map<String, List<DrugDosage>> drugDosagesMap = new HashMap<String, List<DrugDosage>>();
        for (DrugDosage drug : treatmentAdvice.getDrugDosages()) {
            List<String> dosageSchedules = drug.getDosageSchedules();
            filter(dosageSchedules, new Predicate() {
                @Override
                public boolean evaluate(Object o) {
                    String dosageSchedule = (String) o;
                    return StringUtils.isNotBlank(dosageSchedule);
                }
            });
            for (String dosageSchedule : dosageSchedules) {
                List<DrugDosage> drugList = getExistingDrugsForDosageSchedule(drugDosagesMap, dosageSchedule);
                drugList.add(drug);
                drugDosagesMap.put(dosageSchedule, drugList);
            }
        }
        return drugDosagesMap;
    }

    private List<DrugDosage> getExistingDrugsForDosageSchedule(Map<String, List<DrugDosage>> drugDosagesMap, String dosageSchedule) {
        List<DrugDosage> drugList = drugDosagesMap.get(dosageSchedule);
        return drugList == null ? new ArrayList<DrugDosage>() : drugList;
    }

    private DosageRequest createDosageRequest(String schedule, List<DrugDosage> drugDosages) {
        int reminderLagTime = Integer.valueOf(properties.getProperty(TAMAConstants.REMINDER_LAG));
        TimeUtil timeUtil = new TimeUtil(schedule).withReminderLagTime(reminderLagTime);
        List<MedicineRequest> medicineRequests = createMedicineRequests(drugDosages);
        return new DosageRequest(timeUtil.getHours(), timeUtil.getMinutes(), medicineRequests);
    }

    private List<MedicineRequest> createMedicineRequests(List<DrugDosage> drugDosages) {
        List<MedicineRequest> medicineRequests = new ArrayList<MedicineRequest>();
        for (DrugDosage drugDosage : drugDosages) {
            MedicineRequest medicineRequest = new MedicineRequest(allDrugs.get(drugDosage.getDrugId()).fullName(drugDosage.getBrandId()),
                    drugDosage.getStartDate(),
                    drugDosage.getEndDate());
            medicineRequests.add(medicineRequest);
        }
        return medicineRequests;
    }
}