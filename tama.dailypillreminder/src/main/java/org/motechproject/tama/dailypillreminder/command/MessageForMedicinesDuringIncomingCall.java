package org.motechproject.tama.dailypillreminder.command;

import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MessageForMedicinesDuringIncomingCall extends DailyPillReminderTreeCommand {
    private AllPatients allPatients;
    private AllClinics allClinics;
    private ClinicNameMessageBuilder clinicNameMessageBuilder;

    @Autowired
    public MessageForMedicinesDuringIncomingCall(AllPatients allPatients, AllClinics allClinics, PillReminderService pillReminderService, ClinicNameMessageBuilder clinicNameMessageBuilder) {
        super(pillReminderService);
        this.allPatients = allPatients;
        this.allClinics = allClinics;
        this.clinicNameMessageBuilder = clinicNameMessageBuilder;
    }

    @Override
    public String[] executeCommand(DailyPillReminderContext context) {
        ArrayList<String> messages = new ArrayList<String>();
        Patient patient = allPatients.get(context.patientId());
        Clinic clinic = allClinics.get(patient.getClinic_id());

        if (!context.hasTraversedTree(TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM)) {
            messages.add(clinicNameMessageBuilder.getInboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage()));
        }
        PillRegimen pillRegimen = pillRegimen(context);
        if (pillRegimen.isNowWithinCurrentDosePillWindow(context.callStartTime())) {
            messages.add(TamaIVRMessage.ITS_TIME_FOR_THE_PILL);
            addMedicines(messages, pillRegimen, context);
            messages.add(TamaIVRMessage.PILL_FROM_THE_BOTTLE);
        } else {
            messages.add(TamaIVRMessage.NOT_REPORTED_IF_TAKEN);
            addMedicines(messages, pillRegimen, context);
            messages.add(TamaIVRMessage.PILL_FROM_THE_BOTTLE_AFTER_PILL_WINDOW);
        }

        return messages.toArray(new String[messages.size()]);
    }

    private void addMedicines(ArrayList<String> messages, PillRegimen pillRegimen, DailyPillReminderContext context) {
        for (String medicine : pillRegimen.medicinesForCurrentDose(context.callStartTime())) {
            messages.add(medicine);
        }
    }
}
