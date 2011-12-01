package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.motechproject.tamadomain.domain.SuspendedAdherenceData;

public class SuspendedAdherenceDataPreset {

    public static SuspendedAdherenceData fromYesterdayWithAnyStatus() {
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        DateTime suspendedFromDate = new DateTime();
        suspendedAdherenceData.suspendedFrom(suspendedFromDate);
        suspendedAdherenceData.patientId("patientId");
        suspendedAdherenceData.setAdherenceDataWhenPatientWasSuspended(SuspendedAdherenceData.DosageStatusWhenSuspended.DOSE_TAKEN);
        return suspendedAdherenceData;
    }

    public static SuspendedAdherenceData fromWeekBeforeLastWithAnyStatus(DateTime reActivatedDate) {
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        DateTime suspendedFromDate = reActivatedDate.minusDays(16);
        suspendedAdherenceData.suspendedFrom(suspendedFromDate);
        suspendedAdherenceData.patientId("patientId");
        suspendedAdherenceData.setAdherenceDataWhenPatientWasSuspended(SuspendedAdherenceData.DosageStatusWhenSuspended.DOSE_NOT_TAKEN);
        return suspendedAdherenceData;
    }

    public static SuspendedAdherenceData fromFourteenDaysBackWithAnyStatus(DateTime reActivatedDate) {
        SuspendedAdherenceData suspendedAdherenceData = new SuspendedAdherenceData();
        DateTime suspendedFromDate = reActivatedDate.minusDays(14);
        suspendedAdherenceData.suspendedFrom(suspendedFromDate);
        suspendedAdherenceData.patientId("patientId");
        suspendedAdherenceData.setAdherenceDataWhenPatientWasSuspended(SuspendedAdherenceData.DosageStatusWhenSuspended.DOSE_NOT_TAKEN);
        return suspendedAdherenceData;
    }


}