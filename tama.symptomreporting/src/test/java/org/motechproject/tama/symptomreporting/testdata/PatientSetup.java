package org.motechproject.tama.symptomreporting.testdata;

import org.mockito.Matchers;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.objectcache.AllRegimensCache;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PatientSetup {

    private String mobileNumber;
    private String patientDocumentId;
    private String regimenId;

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimensCache allRegimens;
    private String patientId;
    private Clinic clinic;

    private Patient patient;
    private TreatmentAdvice treatmentAdvice;
    private Regimen regimen;

    public PatientSetup(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllRegimensCache allRegimens) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;
    }

    public PatientSetup withPatientDocumentId(String patientDocumentId) {
        this.patientDocumentId = patientDocumentId;
        return this;
    }

    public PatientSetup withPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public PatientSetup withMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public PatientSetup withRegimenId(String regimenId) {
        this.regimenId = regimenId;
        return this;
    }

    public PatientSetup withClinic(Clinic clinic) {
        this.clinic = clinic;
        return this;
    }

    public Patient getPatient(){
        return patient;
    }

    public TreatmentAdvice getTreatmentAdvice() {
        return treatmentAdvice;
    }

    public Regimen getRegimen() {
        return regimen;
    }

    public void run() {
        patient = PatientBuilder.startRecording().withMobileNumber(mobileNumber).withId(patientDocumentId).withPatientId(patientId).withClinic(clinic).build();
        when(allPatients.get(patientDocumentId)).thenReturn(patient);

        treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withRegimenId(regimenId).build();
        when(allTreatmentAdvices.currentTreatmentAdvice(patientDocumentId)).thenReturn(treatmentAdvice);

        regimen = mock(Regimen.class);
        when(allRegimens.getBy(Matchers.<String>any())).thenReturn(regimen);
    }
}
