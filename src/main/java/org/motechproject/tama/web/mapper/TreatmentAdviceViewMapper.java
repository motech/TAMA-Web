package org.motechproject.tama.web.mapper;

import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.*;
import org.motechproject.tama.web.model.TreatmentAdviceView;

public class TreatmentAdviceViewMapper {

    private Regimens regimens;
    private Drugs drugs;
    private DosageTypes dosageTypes;
    private MealAdviceTypes mealAdviceTypes;
    private TreatmentAdvices treatmentAdvices;
    private Patients patients;

    public TreatmentAdviceViewMapper(TreatmentAdvices treatmentAdvices, Patients patients, Regimens regimens, Drugs drugs, DosageTypes dosageTypes, MealAdviceTypes mealAdviceTypes) {
        this.treatmentAdvices = treatmentAdvices;
        this.patients = patients;
        this.regimens = regimens;
        this.drugs = drugs;
        this.dosageTypes = dosageTypes;
        this.mealAdviceTypes = mealAdviceTypes;
    }

    public TreatmentAdviceView map(String treatmentAdviceId) {
        TreatmentAdvice treatmentAdvice = treatmentAdvices.get(treatmentAdviceId);
        Patient patient = patients.get(treatmentAdvice.getPatientId());
        Regimen regimen = regimens.get(treatmentAdvice.getRegimenId());
        DrugCompositionGroup drugCompositionGroup = regimen.getDrugCompositionGroupFor(treatmentAdvice.getDrugCompositionGroupId());

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setTreatmentAdviceId(treatmentAdvice.getId());
        treatmentAdviceView.setPatientIdentifier(treatmentAdvice.getPatientId());
        treatmentAdviceView.setPatientId(patient.getPatientId());
        treatmentAdviceView.setRegimenName(regimen.getDisplayName());
        treatmentAdviceView.setDrugCompositionName(drugCompositionGroup.getName());

        DrugDosageViewMapper drugDosageViewMapper = new DrugDosageViewMapper(drugs, dosageTypes, mealAdviceTypes);
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            treatmentAdviceView.addDrugDosage(drugDosageViewMapper.map(drugDosage));
        }

        return treatmentAdviceView;
    }

}
