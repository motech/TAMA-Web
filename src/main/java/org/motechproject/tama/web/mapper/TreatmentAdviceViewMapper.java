package org.motechproject.tama.web.mapper;

import org.apache.commons.lang.StringUtils;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Drugs;
import org.motechproject.tama.repository.Regimens;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class TreatmentAdviceViewMapper {

    @Autowired
    private Regimens regimens;
    @Autowired
    private TreatmentAdvices treatmentAdvices;
    @Autowired
    private Drugs drugs;

    public TreatmentAdviceViewMapper() {
    }

    public TreatmentAdviceViewMapper(Regimens regimens, TreatmentAdvices treatmentAdvices, Drugs drugs) {
        this.regimens = regimens;
        this.treatmentAdvices = treatmentAdvices;
        this.drugs = drugs;
    }

    public TreatmentAdviceView map(String treatmentAdviceId) {
        TreatmentAdvice treatmentAdvice = treatmentAdvices.get(treatmentAdviceId);
        Regimen regimen = regimens.get(treatmentAdvice.getRegimenId());
        RegimenComposition regimenComposition = regimen.getCompositionsFor(treatmentAdvice.getRegimenCompositionId());
        List<Drug> allDrugs = this.drugs.getDrugs(regimenComposition.getDrugIds());
        String regimenCompositionDisplayName = StringUtils.join(allDrugs.toArray(), " / ");

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setPatientId(treatmentAdvice.getPatientId());
        treatmentAdviceView.setRegimenName(regimen.getRegimenDisplayName());
        treatmentAdviceView.setRegimenCompositionName(regimenCompositionDisplayName);

        return treatmentAdviceView;
    }

}
