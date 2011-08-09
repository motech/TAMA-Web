package org.motechproject.tama.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class PillRegimenResponseBuilder {

    private List<DosageResponse> dosages;

    public PillRegimenResponse build() {
        return new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
    }

    public static PillRegimenResponseBuilder startRecording() {
        return new PillRegimenResponseBuilder();
    }

    public PillRegimenResponseBuilder withDefaults(){
        dosages = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> currentDosagesMedicines = new ArrayList<MedicineResponse>();
        ArrayList<MedicineResponse> previousDosagesMedicines = new ArrayList<MedicineResponse>();
        ArrayList<MedicineResponse> nextDosagesMedicines = new ArrayList<MedicineResponse>();
        LocalDate date = DateUtil.today();

        previousDosagesMedicines.add(new MedicineResponse("medicine3", date, date));
        currentDosagesMedicines.add(new MedicineResponse("medicine1", date, date));
        currentDosagesMedicines.add(new MedicineResponse("medicine2", date, date));
        nextDosagesMedicines.add(new MedicineResponse("medicine4", date, date));

        dosages.add(new DosageResponse("previousDosageId", new Time(10, 5), date, date, date, previousDosagesMedicines));
        dosages.add(new DosageResponse("currentDosageId", new Time(16, 5), date, date, date, currentDosagesMedicines));
        dosages.add(new DosageResponse("nextDosageId", new Time(22, 5), date, date, date, nextDosagesMedicines));

        return this;
    }

    public List<DosageResponse> dosages(){
        return new ArrayList<DosageResponse>(dosages);
    }

    public PillRegimenResponseBuilder withDosages(List<DosageResponse> dosages) {
        this.dosages = dosages;
        return this;
    }
}
