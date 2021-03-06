package org.motechproject.tama.dailypillreminder.builder;

import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.api.contract.DosageResponse;
import org.motechproject.server.pillreminder.api.contract.MedicineResponse;
import org.motechproject.server.pillreminder.api.contract.PillRegimenResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PillRegimenResponseBuilder {

    private List<DosageResponse> dosages;
    private String id;

    public PillRegimenResponse build() {
        return new PillRegimenResponse(id, "patientId", 2, 5, 5, dosages);
    }

    public static PillRegimenResponseBuilder startRecording() {
        return new PillRegimenResponseBuilder();
    }

    public PillRegimenResponseBuilder withDefaults() {
        id = "regimenId";
        dosages = new ArrayList<>();
        ArrayList<MedicineResponse> currentDosagesMedicines = new ArrayList<>();
        ArrayList<MedicineResponse> previousDosagesMedicines = new ArrayList<>();
        ArrayList<MedicineResponse> nextDosagesMedicines = new ArrayList<>();
        LocalDate date = new LocalDate(2010, 10, 10);

        previousDosagesMedicines.add(new MedicineResponse("", date, null));
        currentDosagesMedicines.add(new MedicineResponse("", date, null));
        currentDosagesMedicines.add(new MedicineResponse("", date, null));
        nextDosagesMedicines.add(new MedicineResponse("", date, null));

        dosages.add(new DosageResponse("previousDosageId", new Time(10, 5), date, null, date, previousDosagesMedicines));
        dosages.add(new DosageResponse("currentDosageId", new Time(16, 5), date, null, date, currentDosagesMedicines));
        dosages.add(new DosageResponse("nextDosageId", new Time(22, 5), date, null, date, nextDosagesMedicines));

        return this;
    }

    public List<DosageResponse> dosages() {
        return new ArrayList<>(dosages);
    }

    public PillRegimenResponseBuilder withDosages(List<DosageResponse> dosages) {
        this.dosages = dosages;
        return this;
    }

    public PillRegimenResponseBuilder withRegimenId(String id) {
        this.id = id;
        return this;
    }

    public PillRegimenResponseBuilder withTwoDosages(Time dosage1Time, LocalDate dosage1StartDate, String dosage1Id, Time dosage2Time, LocalDate dosage2StartDate, String dosage2Id) {
        dosages = new ArrayList<>();
        dosages.add(new DosageResponse(dosage1Id, dosage1Time, dosage1StartDate, null, null, Collections.<MedicineResponse>emptyList()));
        dosages.add(new DosageResponse(dosage2Id, dosage2Time, dosage2StartDate, null, null, Collections.<MedicineResponse>emptyList()));
        return this;
    }
}
