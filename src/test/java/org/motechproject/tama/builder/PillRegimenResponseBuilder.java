package org.motechproject.tama.builder;

import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;

import java.util.ArrayList;
import java.util.Date;

public class PillRegimenResponseBuilder {

    private PillRegimenResponse pillRegimen;

    public PillRegimenResponse build() {
        return this.pillRegimen;
    }

    public static PillRegimenResponseBuilder startRecording() {
        return new PillRegimenResponseBuilder();
    }

    public PillRegimenResponseBuilder withDefaults(){
        ArrayList<DosageResponse> dosages = new ArrayList<DosageResponse>();
        ArrayList<MedicineResponse> currentDosagesMedicines = new ArrayList<MedicineResponse>();
        ArrayList<MedicineResponse> previousDosagesMedicines = new ArrayList<MedicineResponse>();
        Date date = new Date();

        currentDosagesMedicines.add(new MedicineResponse("medicine1", date, date));
        currentDosagesMedicines.add(new MedicineResponse("medicine2", date, date));
        previousDosagesMedicines.add(new MedicineResponse("medicine3", date, date));

        dosages.add(new DosageResponse("currentDosageId", new Time(22, 5), date, date, date, currentDosagesMedicines));
        dosages.add(new DosageResponse("previousDosageId", new Time(10, 5), date, date, date, previousDosagesMedicines));

        pillRegimen = new PillRegimenResponse("regimenId", "patientId", 2, 5, dosages);
        return this;
    }
}
