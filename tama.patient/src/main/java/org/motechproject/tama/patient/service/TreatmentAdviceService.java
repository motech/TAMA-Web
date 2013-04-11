package org.motechproject.tama.patient.service;

import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.reporting.PillTimeRequestMapper;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.service.registry.CallPlanRegistry;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.reporting.service.PatientReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TreatmentAdviceService {

    private AllPatients allPatients;
    private AllTreatmentAdvices allTreatmentAdvices;
    private AllRegimens allRegimens;
    private CallTimeSlotService callTimeSlotService;
    private CallPlanRegistry callPlanRegistry;
    private PatientReportingService patientReportingService;
    private AllPatientEventLogs allPatientEventLogs;

    @Autowired
    public TreatmentAdviceService(AllPatients allPatients, AllTreatmentAdvices allTreatmentAdvices, AllRegimens allRegimens, CallTimeSlotService callTimeSlotService, CallPlanRegistry callPlanRegistry, PatientReportingService patientReportingService, AllPatientEventLogs allPatientEventLogs) {
        this.allPatients = allPatients;
        this.allTreatmentAdvices = allTreatmentAdvices;
        this.allRegimens = allRegimens;
        this.callTimeSlotService = callTimeSlotService;
        this.callPlanRegistry = callPlanRegistry;
        this.patientReportingService = patientReportingService;
        this.allPatientEventLogs = allPatientEventLogs;
    }

    public String createRegimen(TreatmentAdvice treatmentAdvice, String userName) {
        allTreatmentAdvices.add(treatmentAdvice, userName);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        if (patient.isOnDailyPillReminder()) {
            callTimeSlotService.allotSlots(patient, treatmentAdvice);
        }
        callPlanRegistry.getCallPlan(patient.callPreference()).enroll(patient, treatmentAdvice);

        patientReportingService.savePillTimes(new PillTimeRequestMapper(treatmentAdvice).map());
        return treatmentAdvice.getId();
    }

    public String changeRegimen(String existingTreatmentAdviceId, String discontinuationReason, TreatmentAdvice treatmentAdvice, String userName) {
        TreatmentAdvice existingTreatmentAdvice = allTreatmentAdvices.get(existingTreatmentAdviceId);
        endCurrentRegimen(discontinuationReason, existingTreatmentAdvice, userName);
        allTreatmentAdvices.add(treatmentAdvice, userName);
        Patient patient = allPatients.get(treatmentAdvice.getPatientId());
        callTimeSlotService.freeSlots(patient, existingTreatmentAdvice);
        if (patient.isOnDailyPillReminder()) {
            callTimeSlotService.allotSlots(patient, treatmentAdvice);
        }
        callPlanRegistry.getCallPlan(patient.callPreference()).reEnroll(patient, treatmentAdvice);

        patientReportingService.savePillTimes(new PillTimeRequestMapper(treatmentAdvice).map());

        Regimen existingRegimen = allRegimens.get(existingTreatmentAdvice.getRegimenId());
        Regimen newRegimen = allRegimens.get(treatmentAdvice.getRegimenId());

        allPatientEventLogs.add(new PatientEventLog(patient.getId(),
                PatientEvent.Regimen_Changed,
                newRegimen.getDisplayName(),
                existingRegimen.getDisplayName()),
                userName);
        return treatmentAdvice.getId();
    }

    private void endCurrentRegimen(String discontinuationReason, TreatmentAdvice existingTreatmentAdvice, String userName) {
        existingTreatmentAdvice.endTheRegimen(discontinuationReason);
        allTreatmentAdvices.update(existingTreatmentAdvice, userName);
    }
}