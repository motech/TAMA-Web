package org.motechproject.tamadomain.domain;


import org.joda.time.DateTime;

public class SuspendedAdherenceData {
    public static enum DosageStatusWhenSuspended {
        DOSE_TAKEN("Dose Taken", DosageStatus.TAKEN, 0), DOSE_NOT_TAKEN("Dose Not Taken", DosageStatus.NOT_TAKEN, 4);

        private String value;
        private DosageStatus dosageStatus;
        private int equivalentNumberOfDaysMissed;

        DosageStatusWhenSuspended(String value, DosageStatus dosageStatus, int equivalentNumberOfDaysMissed) {
            this.value = value;
            this.dosageStatus = dosageStatus;
            this.equivalentNumberOfDaysMissed = equivalentNumberOfDaysMissed;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public DosageStatus getStatus() {
            return dosageStatus;
        }

        public int numberOfDaysMissed(){
            return this.equivalentNumberOfDaysMissed;
        }
    }

    private DosageStatusWhenSuspended adherenceDataWhenPatientWasSuspended;

    private DateTime suspendedFrom;

    private String patientId;

    public DosageStatusWhenSuspended getAdherenceDataWhenPatientWasSuspended() {
        return adherenceDataWhenPatientWasSuspended;
    }

    public void setAdherenceDataWhenPatientWasSuspended(DosageStatusWhenSuspended adherenceDataWhenPatientWasSuspended) {
        this.adherenceDataWhenPatientWasSuspended = adherenceDataWhenPatientWasSuspended;
    }

    public void suspendedFrom(DateTime suspendedFromDate) {
        this.suspendedFrom = suspendedFromDate;
    }

    public DateTime suspendedFrom() {
        return suspendedFrom;
    }

    public String patientId() {
        return patientId;
    }

    public void patientId(String patientId) {
        this.patientId = patientId;
    }
}
