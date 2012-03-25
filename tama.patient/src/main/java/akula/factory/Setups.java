package akula.factory;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;

import static org.motechproject.util.DateUtil.newDate;

@Factory(TreatmentAdvice.class)
class TreatmentAdviceFactory {
}

@Factory(DrugDosage.class)
class DrugDosageFactory {

    public LocalDate startDate() {
        return newDate(2012, 2, 10);
    }

    public LocalDate endDate() {
        return newDate(2012, 2, 20);
    }

    public Integer offsetDays() {
        return 0;
    }
}

@Factory(value = DrugDosage.class, name = "MorningDose")
class MorningDose extends DrugDosageFactory {

    public String morningTime() {
        return "10:00am";
    }

    public String eveningTime() {
        return "";
    }
}

@Factory(value = DrugDosage.class, name = "EveningDose")
class EveningDose extends DrugDosageFactory {

    public String morningTime() {
        return "";
    }

    public String eveningTime() {
        return "08:00pm";
    }
}

@Factory(value = DrugDosage.class, name = "TwiceDailyDose")
class TwiceDailyDose extends DrugDosageFactory {

    public String morningTime() {
        return "10:00am";
    }

    public String eveningTime() {
        return "08:00pm";
    }
}

@Factory(Patient.class)
@Persistent(databaseName = "tama-web")
class PatientFactory {
}