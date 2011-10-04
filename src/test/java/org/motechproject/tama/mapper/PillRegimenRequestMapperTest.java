package org.motechproject.tama.mapper;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.AllDrugs;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenRequestMapperTest {

    @Mock
    private AllDrugs allDrugs;

    @Mock
    private Properties properties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldAddReminderLagToDosageMinutes() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(allDrugs, 10, 10, 5);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        Drug drug = mock(Drug.class);
        when(drug.fullName(Matchers.<String>any())).thenReturn("");

        treatmentAdvice.setPatientId("123");
        when(allDrugs.get(Matchers.<String>any())).thenReturn(drug);


        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        LocalDate startDateForDrug1 = DateUtil.newDate(2010, 10, 10);
        LocalDate endDateForDrug1 = DateUtil.newDate(2010, 12, 10);
        LocalDate startDateForDrug2 = DateUtil.newDate(2011, 2, 10);
        LocalDate endDateForDrug2 = DateUtil.newDate(2011, 6, 10);
        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, Arrays.asList("09:00am", "08:30pm")));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug2, endDateForDrug2, Arrays.asList("09:00am", "05:45pm")));
        treatmentAdvice.setDrugDosages(drugDosages);

        DailyPillRegimenRequest request = pillRegimenRequestMapper.map(treatmentAdvice);
        DosageRequest dosageRequest = getByStartHour(9, request.getDosageRequests());
        Assert.assertEquals(5, dosageRequest.getStartMinute());
    }


    @Test
    public void shouldMapTreatmentAdvicesToPillRegimenRequest() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(allDrugs, 10, 10, 5);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        LocalDate startDateForDrug1 = DateUtil.newDate(2010, 10, 10);
        LocalDate endDateForDrug1 = DateUtil.newDate(2010, 12, 10);
        LocalDate startDateForDrug2 = DateUtil.newDate(2011, 2, 10);
        LocalDate endDateForDrug2 = DateUtil.newDate(2011, 6, 10);

        when(allDrugs.get("Drug1Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug1").build());
        when(allDrugs.get("Drug2Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug2").build());
        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, Arrays.asList("09:00am", "08:30pm")));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug2, endDateForDrug2, Arrays.asList("09:00am", "05:45pm")));
        treatmentAdvice.setDrugDosages(drugDosages);

        DailyPillRegimenRequest pillRegimenRequest = pillRegimenRequestMapper.map(treatmentAdvice);

        Assert.assertEquals(treatmentAdvice.getPatientId(), pillRegimenRequest.getExternalId());
        Assert.assertNotNull(pillRegimenRequest.getReminderRepeatIntervalInMinutes());
        Assert.assertNotNull(pillRegimenRequest.getPillWindowInHours());

        Assert.assertEquals(3, pillRegimenRequest.getDosageRequests().size());

        DosageRequest dosageRequest1 = getByStartHour(17, pillRegimenRequest.getDosageRequests());
        assertDosageRequestWithReminderTimeLag(dosageRequest1, 17, 50);
        Assert.assertEquals(1, dosageRequest1.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest1.getMedicineRequests().get(0), "Drug2_brandName", startDateForDrug2, endDateForDrug2);

        DosageRequest dosageRequest2 = getByStartHour(20, pillRegimenRequest.getDosageRequests());
        assertDosageRequestWithReminderTimeLag(dosageRequest2, 20, 35);
        Assert.assertEquals(1, dosageRequest2.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest2.getMedicineRequests().get(0), "Drug1_brandName", startDateForDrug1, endDateForDrug1);

        DosageRequest dosageRequest3 = getByStartHour(9, pillRegimenRequest.getDosageRequests());
        assertDosageRequestWithReminderTimeLag(dosageRequest3, 9, 5);
        Assert.assertEquals(2, dosageRequest3.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(0), "Drug1_brandName", startDateForDrug1, endDateForDrug1);
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(1), "Drug2_brandName", startDateForDrug2, endDateForDrug2);
    }

    private DosageRequest getByStartHour(int startHour, List<DosageRequest> dosageRequests) {
        for (DosageRequest dosageRequest : dosageRequests) {
            if (dosageRequest.getStartHour() == startHour)
                return dosageRequest;
        }
        return null;
    }

    private void assertDosageRequestWithReminderTimeLag(DosageRequest dosageRequest, int startHour, int startMinute) {
        Assert.assertEquals(startHour, dosageRequest.getStartHour());
        Assert.assertEquals(startMinute, dosageRequest.getStartMinute());
    }

    private void assertMedicineRequest(MedicineRequest medicineRequest, String name, LocalDate startDate, LocalDate endDate) {
        Assert.assertEquals(name, medicineRequest.getName());
        Assert.assertEquals(startDate, medicineRequest.getStartDate());
        Assert.assertEquals(endDate, medicineRequest.getEndDate());
    }

    private DrugDosage drugDosage(String drugId, LocalDate startDate, LocalDate endDate, List<String> dosageSchedules) {
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setDrugId(drugId);
        drugDosage.setBrandId("brandId");
        drugDosage.setStartDate(startDate);
        drugDosage.setEndDate(endDate);
        drugDosage.setDosageSchedules(dosageSchedules);
        return drugDosage;
    }
}