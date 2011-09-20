package org.motechproject.tama.web.command;

import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdatePreviousPillAdherenceCommand extends UpdateAdherenceCommand {

    @Autowired
    public UpdatePreviousPillAdherenceCommand(AllDosageAdherenceLogs logs) {
        super(logs);
    }

    @Override
    protected String getDosageId(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).getPreviousDosage().getDosageId();
    }
}
