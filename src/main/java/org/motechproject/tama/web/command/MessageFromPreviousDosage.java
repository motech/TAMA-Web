package org.motechproject.tama.web.command;

import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.builder.IVRDayMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageFromPreviousDosage extends BaseTreeCommand {
    private PillReminderService service;

    @Autowired
    public MessageFromPreviousDosage(PillReminderService service) {
        this.service = service;
    }

    @Override
    public String[] execute(Object o) {
        IVRContext ivrContext = (IVRContext) o;
        DosageResponse previousDosage = service.getPreviousDosage(getRegimenIdFrom(ivrContext), getDosageIdFrom(ivrContext));
        String previousDosageId = previousDosage.getDosageId();

        List<String> messages = new ArrayList<String>();
        if ("previousDosageId".equals(previousDosageId)) {
            putPreviousDosageId(ivrContext, previousDosageId);
            IVRDayMessageBuilder ivrDayMessageBuilder = new IVRDayMessageBuilder(getDosageIdFrom(ivrContext), previousDosageId, previousDosage.getDosageHour());
            messages.add(IVRMessage.YOUR);
            messages.addAll(ivrDayMessageBuilder.getMessages(IVRMessage.YESTERDAYS, IVRMessage.MORNING, IVRMessage.EVENING));
            messages.add(IVRMessage.DOSE_NOT_RECORDED);
            messages.addAll(ivrDayMessageBuilder.getMessages(IVRMessage.YESTERDAY, IVRMessage.IN_THE_MORNING, IVRMessage.IN_THE_EVENING));
            messages.add(IVRMessage.YOU_WERE_SUPPOSED_TO_TAKE);
            messages.addAll(getMedicineNames(previousDosage.getMedicines()));
            messages.add(IVRMessage.FROM_THE_BOTTLE);
            messages.add(IVRMessage.PREVIOUS_DOSE_MENU);
        }
        return messages.toArray(new String[]{});
    }

    private List<String> getMedicineNames(List<MedicineResponse> medicines) {
        List<String> medicineNames = new ArrayList<String>();
        for (MedicineResponse medicineResponse : medicines) {
            medicineNames.add(medicineResponse.getName());
        }
        return medicineNames;
    }

}
