package org.motechproject.tama.preset;

import org.joda.time.DateTime;
import org.motechproject.tama.web.view.SuspendedAdherenceData;

public class SuspendedAdherenceDataPreset {

    public static SuspendedAdherenceData fromYesterdayWithAnyStatus(){
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        DateTime suspendedFromDate = new DateTime();
        suspendedAdherenceData.suspendedFrom(suspendedFromDate);
        suspendedAdherenceData.patientId("patientId");
        suspendedAdherenceData.setAdherenceDataWhenPatientWasSuspended(SuspendedAdherenceData.DosageStatusWhenSuspended.DOSE_TAKEN);
        return suspendedAdherenceData;
    }

    public static SuspendedAdherenceData fromWeekBeforeLastWithAnyStatus(DateTime reActivatedDate){
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        DateTime suspendedFromDate = reActivatedDate.minusDays(16);
        suspendedAdherenceData.suspendedFrom(suspendedFromDate);
        suspendedAdherenceData.patientId("patientId");
        suspendedAdherenceData.setAdherenceDataWhenPatientWasSuspended(SuspendedAdherenceData.DosageStatusWhenSuspended.DOSE_NOT_TAKEN);
        return suspendedAdherenceData;
    }


}