package org.motechproject.tama.mapper;

import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Drugs;
import org.motechproject.tama.util.TimeUtil;

import java.util.*;

public class PillRegimenRequestMapper {

    private Drugs drugs;

    public PillRegimenRequestMapper(Drugs drugs) {
        this.drugs = drugs;
    }

    public PillRegimenRequest map(TreatmentAdvice treatmentAdvice) {
        return new PillRegimenRequest(treatmentAdvice.getPatientId(), 2, 15, mapDosageRequests(treatmentAdvice));
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
            for (String dosageSchedule : drug.getDosageSchedules()) {
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
        TimeUtil timeUtil = new TimeUtil(schedule);
        List<MedicineRequest> medicineRequests = createMedicineRequests(drugDosages);
        return new DosageRequest(timeUtil.getHours(), timeUtil.getMinutes(), medicineRequests);
    }

    private List<MedicineRequest> createMedicineRequests(List<DrugDosage> drugDosages) {
        List<MedicineRequest> medicineRequests = new ArrayList<MedicineRequest>();
        for (DrugDosage drugDosage : drugDosages) {
                MedicineRequest medicineRequest = new MedicineRequest(drugs.get(drugDosage.getDrugId()).fullName(drugDosage.getBrandId()), drugDosage.getStartDate(), drugDosage.getEndDate());
            medicineRequests.add(medicineRequest);
        }
        return medicineRequests;
    }
}