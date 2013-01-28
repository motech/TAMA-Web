package org.motechproject.tama.patient.reporting;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.tama.patient.domain.NonHIVMedicalHistory;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.motechproject.tama.reports.contract.MedicalHistoryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MedicalHistoryRequestMapper {

    private AllHIVTestReasonsCache testReasonsCache;
    private AllModesOfTransmissionCache modesOfTransmissionCache;

    @Autowired
    public MedicalHistoryRequestMapper(AllModesOfTransmissionCache modesOfTransmissionCache, AllHIVTestReasonsCache testReasonsCache) {
        this.modesOfTransmissionCache = modesOfTransmissionCache;
        this.testReasonsCache = testReasonsCache;
    }

    public MedicalHistoryRequest map(Patient patient) {
        MedicalHistoryRequest request = new MedicalHistoryRequest();
        try {
            request.setPatientId(patient.getPatientId());
            request.setHivTestReason(testReasonsCache.getBy(patient.getMedicalHistory().getHivMedicalHistory().getTestReasonId()).getName());
            request.setModesOfTransmission(modesOfTransmissionCache.getBy(patient.getMedicalHistory().getHivMedicalHistory().getModeOfTransmissionId()).getType());
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
