package org.motechproject.tama.patient.reporting;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.tama.patient.domain.NonHIVMedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;

import java.io.IOException;

public class MedicalHistoryRequestMapper {

    public MedicalHistoryRequest map(Patient patient) {
        MedicalHistoryRequest request = new MedicalHistoryRequest();
        try {
            request.setPatientId(patient.getPatientId());
            request.setNonHivMedicalHistory(toJson(patient.getMedicalHistory().getNonHivMedicalHistory()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return request;
    }

    private JsonNode toJson(NonHIVMedicalHistory medicalHistory) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(mapper.writeValueAsString(medicalHistory));
    }
}
