package org.motechproject.tama.web.command;

import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;

public abstract class BaseTreeCommand implements ITreeCommand{

	    protected String getRegimenIdFrom(IVRContext ivrContext) {
	        return getPillRegimen(ivrContext).getPillRegimenId();
	    }

		private PillRegimenResponse getPillRegimen(IVRContext ivrContext) {
			return (PillRegimenResponse)ivrContext.ivrSession().get(TamaSessionUtil.TamaSessionAttribute.REGIMEN_FOR_PATIENT);
		}

	    protected int getTimesSent(IVRContext ivrContext) {
	        return Integer.parseInt(ivrContext.ivrRequest().getParameter(PillReminderCall.TIMES_SENT).toString());
	    }

	    protected int getTotalTimesToSend(IVRContext ivrContext) {
	        return Integer.parseInt(ivrContext.ivrRequest().getParameter(PillReminderCall.TOTAL_TIMES_TO_SEND).toString());
	    }
}