package org.motechproject.tama.web.mapper;

import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Patients;
import org.motechproject.tama.repository.Regimens;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;

public class TreatmentAdviceViewMapper {

    private Regimens regimens;
    private TreatmentAdvices treatmentAdvices;
    private Patients patients;

    public TreatmentAdviceViewMapper(TreatmentAdvices treatmentAdvices, Patients patients, Regimens regimens) {
        this.treatmentAdvices = treatmentAdvices;
        this.patients = patients;
        this.regimens = regimens;
    }

    public TreatmentAdviceView map(String treatmentAdviceId) {
        TreatmentAdvice treatmentAdvice = treatmentAdvices.get(treatmentAdviceId);
        Patient patient = patients.get(treatmentAdvice.getPatientId());
        Regimen regimen = regimens.get(treatmentAdvice.getRegimenId());
        RegimenComposition regimenComposition = regimen.getCompositionsFor(treatmentAdvice.getRegimenCompositionId());

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setPatientId(patient.getPatientId());
        treatmentAdviceView.setRegimenName(regimen.getRegimenDisplayName());
        treatmentAdviceView.setRegimenCompositionName(regimenComposition.getDisplayName());

        return treatmentAdviceView;
    }

}
