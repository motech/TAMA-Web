package org.motechproject.tama.web.mapper;

import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.model.TreatmentAdviceView;

public class TreatmentAdviceViewMapper {

    private AllRegimens allRegimens;
    private AllDrugs allDrugs;
    private AllDosageTypes allDosageTypes;
    private AllMealAdviceTypes allMealAdviceTypes;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllPatients allPatients;

    public TreatmentAdviceViewMapper(AllTreatmentAdvices allTreatmentAdvices, AllPatients allPatients, AllRegimens allRegimens, AllDrugs allDrugs, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes) {
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allPatients = allPatients;
        this.allRegimens = allRegimens;
        this.allDrugs = allDrugs;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
    }

    public TreatmentAdviceView map(String treatmentAdviceId) {
        TreatmentAdvice treatmentAdvice = allTreatmentAdvices.get(treatmentAdviceId);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        Regimen regimen = allRegimens.get(treatmentAdvice.getRegimenId());
        DrugCompositionGroup drugCompositionGroup = regimen.getDrugCompositionGroupFor(treatmentAdvice.getDrugCompositionGroupId());

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setTreatmentAdviceId(treatmentAdvice.getId());
        treatmentAdviceView.setPatientIdentifier(treatmentAdvice.getPatientId());
        treatmentAdviceView.setPatientId(patient.getPatientId());
        treatmentAdviceView.setRegimenName(regimen.getDisplayName());
        treatmentAdviceView.setDrugCompositionName(drugCompositionGroup.getName());
        treatmentAdviceView.setPatientStatus(patient.getStatus());

        DrugDosageViewMapper drugDosageViewMapper = new DrugDosageViewMapper(allDrugs, allDosageTypes, allMealAdviceTypes);
        for (DrugDosage drugDosage : treatmentAdvice.getDrugDosages()) {
            treatmentAdviceView.addDrugDosage(drugDosageViewMapper.map(drugDosage));
        }

        return treatmentAdviceView;
    }
}