package org.motechproject.tama.web.service;

import org.apache.commons.collections.CollectionUtils;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.web.model.IncompletePatientDataWarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientDetailsService {

    private AllPatients allPatients;
    private PatientService patientService;

    private AllTreatmentAdvices allTreatmentAdvices;
    private AllVitalStatistics allVitalStatistics;
    private AllClinicVisits allClinicVisits;
    private AllLabResults allLabResults;

    @Autowired
    public PatientDetailsService(AllPatients allPatients, PatientService patientService, AllTreatmentAdvices allTreatmentAdvices, AllVitalStatistics allVitalStatistics, AllClinicVisits allClinicVisits, AllLabResults allLabResults) {
        this.allPatients = allPatients;
        this.patientService = patientService;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allVitalStatistics = allVitalStatistics;
        this.allClinicVisits = allClinicVisits;
        this.allLabResults = allLabResults;
    }

    public void update(String patientId) {
        Patient patient = allPatients.get(patientId);
        List<String> warnings = new IncompletePatientDataWarning(patient, allVitalStatistics, allTreatmentAdvices, allLabResults, allClinicVisits).value();
        patient.setComplete(CollectionUtils.isEmpty(warnings));
        patientService.update(patient, "");
    }
}
