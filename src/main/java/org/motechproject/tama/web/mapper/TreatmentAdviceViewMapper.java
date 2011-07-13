package org.motechproject.tama.web.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.Drugs;
import org.motechproject.tama.repository.Patients;
import org.motechproject.tama.repository.Regimens;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import java.util.List;

public class TreatmentAdviceViewMapper {

    private Regimens regimens;
    private TreatmentAdvices treatmentAdvices;
    private Drugs drugs;
    private Patients patients;

    public TreatmentAdviceViewMapper(TreatmentAdvices treatmentAdvices, Patients patients, Regimens regimens, Drugs drugs) {
        this.treatmentAdvices = treatmentAdvices;
        this.patients = patients;
        this.regimens = regimens;
        this.drugs = drugs;
    }

    public TreatmentAdviceView map(String treatmentAdviceId) {
        TreatmentAdvice treatmentAdvice = treatmentAdvices.get(treatmentAdviceId);
        Patient patient = patients.get(treatmentAdvice.getPatientId());
        Regimen regimen = regimens.get(treatmentAdvice.getRegimenId());
        RegimenComposition regimenComposition = regimen.getCompositionsFor(treatmentAdvice.getRegimenCompositionId());
        List<Drug> allDrugs = this.drugs.getDrugs(regimenComposition.getDrugIds());
        String regimenCompositionDisplayName = StringUtils.join(allDrugs.toArray(), " / ");

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setPatientId(patient.getPatientId());
        treatmentAdviceView.setRegimenName(regimen.getRegimenDisplayName());
        treatmentAdviceView.setRegimenCompositionName(regimenCompositionDisplayName);

        return treatmentAdviceView;
    }

}
