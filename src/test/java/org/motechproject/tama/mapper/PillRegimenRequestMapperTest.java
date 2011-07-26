package org.motechproject.tama.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Drugs;

import java.util.*;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenRequestMapperTest {

    @Mock
    private Drugs drugs;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldMapTreatmentAdvicesToPillRegimenRequest() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(drugs);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        Date startDateForDrug1 = calendar(2010, 10, 10).getTime();
        Date endDateForDrug1 = calendar(2010, 12, 10).getTime();
        Date startDateForDrug2 = calendar(2011, 02, 10).getTime();
        Date endDateForDrug2 = calendar(2011, 06, 10).getTime();

        when(drugs.get("Drug1Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug1").build());
        when(drugs.get("Drug2Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug2").build());

        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, Arrays.asList("09:00am", "08:30pm")));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug2, endDateForDrug2, Arrays.asList("09:00am", "05:45pm")));
        treatmentAdvice.setDrugDosages(drugDosages);

        PillRegimenRequest pillRegimenRequest = pillRegimenRequestMapper.map(treatmentAdvice);

        Assert.assertEquals(treatmentAdvice.getPatientId(), pillRegimenRequest.getExternalId());
        Assert.assertNotNull(pillRegimenRequest.getReminderRepeatIntervalInMinutes());
        Assert.assertNotNull(pillRegimenRequest.getReminderRepeatWindowInHours());

        Assert.assertEquals(3, pillRegimenRequest.getDosageRequests().size());

        DosageRequest dosageRequest1 = getByStartHour(17, pillRegimenRequest.getDosageRequests());
        assertDosageRequest(dosageRequest1, 17, 45);
        Assert.assertEquals(1, dosageRequest1.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest1.getMedicineRequests().get(0), "brandName_Drug2", startDateForDrug2, endDateForDrug2);

        DosageRequest dosageRequest2 = getByStartHour(20, pillRegimenRequest.getDosageRequests());
        assertDosageRequest(dosageRequest2, 20, 30);
        Assert.assertEquals(1, dosageRequest2.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest2.getMedicineRequests().get(0), "brandName_Drug1", startDateForDrug1, endDateForDrug1);

        DosageRequest dosageRequest3 = getByStartHour(9, pillRegimenRequest.getDosageRequests());
        assertDosageRequest(dosageRequest3, 9, 0);
        Assert.assertEquals(2, dosageRequest3.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(0), "brandName_Drug1", startDateForDrug1, endDateForDrug1);
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(1), "brandName_Drug2", startDateForDrug2, endDateForDrug2);
    }

    private DosageRequest getByStartHour(int startHour, List<DosageRequest> dosageRequests) {
        for (Iterator<DosageRequest> it = dosageRequests.iterator(); it.hasNext(); ) {
            DosageRequest dosageRequest = it.next();
            if (dosageRequest.getStartHour() == startHour)
                return dosageRequest;
        }

        Assert.assertFalse(true);
        return null;
    }

    private void assertDosageRequest(DosageRequest dosageRequest, int startHour, int startMinute) {
        Assert.assertEquals(startHour, dosageRequest.getStartHour());
        Assert.assertEquals(startMinute, dosageRequest.getStartMinute());
    }

    private void assertMedicineRequest(MedicineRequest medicineRequest, String name, Date startDate, Date endDate) {
        Assert.assertEquals(name, medicineRequest.getName());
        Assert.assertEquals(startDate, medicineRequest.getStartDate());
        Assert.assertEquals(endDate, medicineRequest.getEndDate());
    }

    private Calendar calendar(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        return date;
    }

    private DrugDosage drugDosage(String drugId, Date startDate, Date endDate, List<String> dosageSchedules) {
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setDrugId(drugId);
        drugDosage.setBrandId("brandId");
        drugDosage.setStartDate(startDate);
        drugDosage.setEndDate(endDate);
        drugDosage.setDosageSchedules(dosageSchedules);
        return drugDosage;
    }
}