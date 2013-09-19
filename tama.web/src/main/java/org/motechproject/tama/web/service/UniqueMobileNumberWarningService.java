package org.motechproject.tama.web.service;


import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.web.PatientController;
import org.motechproject.tama.web.model.UniquePatientMobileNumberWarning;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

public class UniqueMobileNumberWarningService {

    private AllPatients allPatients;

    public UniqueMobileNumberWarningService(AllPatients allPatients) {
        this.allPatients = allPatients;
    }

    public Model checkUniquenessOfPatientMobileNumberAndRenderWarning(Model uiModel, Patient patient) {
        List<Patient> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumber(patient.getMobilePhoneNumber(), patient.getPatientId(), patient.getClinic().getName());
        return updateUIModelWithWarnings(uiModel, patientsWithSameMobileNumber);
    }
    public Model checkUniquenessOfPatientMobileNumberAndRenderWarningClinicId(Model uiModel, Patient patient,String clinicId){
        List<Patient> patientsWithSameMobileNumber = new UniquePatientMobileNumberWarning(allPatients).findAllMobileNumbersWhichMatchTheGivenNumberOnUpdate(patient.getMobilePhoneNumber(), patient.getPatientId(), clinicId);
        return updateUIModelWithWarnings(uiModel, patientsWithSameMobileNumber);
    }

    private Model updateUIModelWithWarnings(Model uiModel, List<Patient> patientsWithSameMobileNumber) {
        List<String> warningMessage = null;
        List<String> adviceMessage = null;
        if (CollectionUtils.isNotEmpty(patientsWithSameMobileNumber)) {
            warningMessage = new ArrayList<>();
            warningMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS);
            adviceMessage = new ArrayList<>();
            adviceMessage.add(PatientController.WARNING_DUPLICATE_PHONE_NUMBERS_SUGGESTION);
        }

        uiModel.addAttribute("patientsWithSameMobileNumber", patientsWithSameMobileNumber);
        uiModel.addAttribute("warningMessage", warningMessage);
        uiModel.addAttribute("adviceMessage", adviceMessage);
        return uiModel;
    }
}
