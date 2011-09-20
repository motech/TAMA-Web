package org.motechproject.tama.util;

import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.call.PillReminderCall;

public class TamaSessionUtil {

    public static class TamaSessionAttribute {
        public static final String REGIMEN_FOR_PATIENT = "regimen_for_patient";
        public static final String NUMBER_OF_ATTEMPTS = "number_of_retries";
        public static final String SYMPTOMS_REPORTING_PARAM = "symptoms_reporting";
        public static final String PATIENT_DOC_ID = IVRSession.IVRCallAttribute.EXTERNAL_ID;
    }

    public static String getDosageIdFrom(IVRContext ivrContext) {
        return new PillRegimenSnapshot(ivrContext).getCurrentDosage().getDosageId();
    }

    public static String getRegimenIdFrom(IVRContext ivrContext) {
        return getPillRegimen(ivrContext).getPillRegimenId();
    }

    public static PillRegimenResponse getPillRegimen(IVRContext ivrContext) {
        return (PillRegimenResponse) ivrContext.ivrSession().get(TamaSessionUtil.TamaSessionAttribute.REGIMEN_FOR_PATIENT);
    }

    public static int getTimesSent(IVRContext ivrContext) {
        return Integer.parseInt(ivrContext.ivrRequest().getParameter(PillReminderCall.TIMES_SENT).toString());
    }

    public static int getTotalTimesToSend(IVRContext ivrContext) {
        return Integer.parseInt(ivrContext.ivrRequest().getParameter(PillReminderCall.TOTAL_TIMES_TO_SEND).toString());
    }

    public static String getPatientId(IVRContext ivrContext) {
        return getPatientId(ivrContext.ivrSession());
    }

    public static String getPatientId(IVRSession ivrSession) {
        return (String) ivrSession.get(IVRSession.IVRCallAttribute.EXTERNAL_ID);
    }

    public static PillRegimenResponse getPillRegimen(IVRSession ivrSession) {
        return (PillRegimenResponse) ivrSession.get(TamaSessionUtil.TamaSessionAttribute.REGIMEN_FOR_PATIENT);
    }

    public static boolean isSymptomsReportingCall(IVRContext ivrContext) {
        return "true".equals(ivrContext.ivrSession().get(TamaSessionUtil.TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM));
    }

    public static boolean isSymptomsReportingCall(IVRSession ivrSession) {
        return "true".equals(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.SYMPTOMS_REPORTING_PARAM));
    }

}
