package org.motechproject.tamacallflow.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;
import java.util.List;

public class PillRegimenResponseBuilder {

    private List<DosageResponse> dosages;
    private String id;

    public PillRegimenResponse build() {
        return new PillRegimenResponse(id, "patientId", 2, 5, dosages);
    }

    public static PillRegimenResponseBuilder startRecording() {
        return new PillRegimenResponseBuilder();
    }

    public PillRegimenResponseBuilder withDefaults() {
        id = "regimenId";
        dosages = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> currentDosagesMedicines = new ArrayList<MedicineResponse>();
        ArrayList<MedicineResponse> previousDosagesMedicines = new ArrayList<MedicineResponse>();
        ArrayList<MedicineResponse> nextDosagesMedicines = new ArrayList<MedicineResponse>();
        LocalDate date = new LocalDate(2010, 10, 10);

        previousDosagesMedicines.add(new MedicineResponse("medicine3", date, null));
        currentDosagesMedicines.add(new MedicineResponse("medicine1", date, null));
        currentDosagesMedicines.add(new MedicineResponse("medicine2", date, null));
        nextDosagesMedicines.add(new MedicineResponse("medicine4", date, null));

        dosages.add(new DosageResponse("previousDosageId", new Time(10, 5), date, null, date, previousDosagesMedicines));
        dosages.add(new DosageResponse("currentDosageId", new Time(16, 5), date, null, date, currentDosagesMedicines));
        dosages.add(new DosageResponse("nextDosageId", new Time(22, 5), date, null, date, nextDosagesMedicines));

        return this;
    }

    public List<DosageResponse> dosages() {
        return new ArrayList<DosageResponse>(dosages);
    }

    public PillRegimenResponseBuilder withDosages(List<DosageResponse> dosages) {
        this.dosages = dosages;
        return this;
    }

    public PillRegimenResponseBuilder withRegimenId(String id) {
        this.id = id;
        return this;
    }

}
