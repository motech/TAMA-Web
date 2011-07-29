package org.motechproject.tama.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenRequest;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.DrugDosage;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Drugs;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenRequestMapperTest {

    @Mock
    private Drugs drugs;

    @Mock
    private Properties properties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldGetRetryIntervalAndPillWindowFromProperties() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(drugs, properties);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        Drug drug = mock(Drug.class);
        when(drug.fullName(Matchers.<String>any())).thenReturn("");

        treatmentAdvice.setPatientId("123");
        when(properties.getProperty(TAMAConstants.PILL_WINDOW)).thenReturn("10");
        when(properties.getProperty(TAMAConstants.RETRY_INTERVAL)).thenReturn("10");
        when(properties.getProperty(TAMAConstants.REMINDER_LAG)).thenReturn("10");
        when(drugs.get(Matchers.<String>any())).thenReturn(drug);


        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        Date startDateForDrug1 = calendar(2010, 10, 10).getTime();
        Date endDateForDrug1 = calendar(2010, 12, 10).getTime();
        Date startDateForDrug2 = calendar(2011, 02, 10).getTime();
        Date endDateForDrug2 = calendar(2011, 06, 10).getTime();
        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, Arrays.asList("09:00am", "08:30pm")));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug2, endDateForDrug2, Arrays.asList("09:00am", "05:45pm")));
        treatmentAdvice.setDrugDosages(drugDosages);

        pillRegimenRequestMapper.map(treatmentAdvice);

        verify(properties, times(1)).getProperty(TAMAConstants.PILL_WINDOW);
        verify(properties, times(1)).getProperty(TAMAConstants.RETRY_INTERVAL);

    }

    @Test
    public void shouldAddReminderLagToDosageMinutes() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(drugs, properties);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();

        Drug drug = mock(Drug.class);
        String reminderTimeLag = "5";
        when(drug.fullName(Matchers.<String>any())).thenReturn("");
        propertiesExpectations(reminderTimeLag);

        treatmentAdvice.setPatientId("123");
        when(drugs.get(Matchers.<String>any())).thenReturn(drug);


        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        Date startDateForDrug1 = calendar(2010, 10, 10).getTime();
        Date endDateForDrug1 = calendar(2010, 12, 10).getTime();
        Date startDateForDrug2 = calendar(2011, 02, 10).getTime();
        Date endDateForDrug2 = calendar(2011, 06, 10).getTime();
        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, Arrays.asList("09:00am", "08:30pm")));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug2, endDateForDrug2, Arrays.asList("09:00am", "05:45pm")));
        treatmentAdvice.setDrugDosages(drugDosages);

        PillRegimenRequest request = pillRegimenRequestMapper.map(treatmentAdvice);
        DosageRequest dosageRequest = getByStartHour(9, request.getDosageRequests());
        Assert.assertTrue(Integer.valueOf(reminderTimeLag) == dosageRequest.getStartMinute());
    }

    private void propertiesExpectations(String reminderTimeLag) {
        when(properties.getProperty(TAMAConstants.PILL_WINDOW)).thenReturn("10");
        when(properties.getProperty(TAMAConstants.RETRY_INTERVAL)).thenReturn("10");
        when(properties.getProperty(TAMAConstants.REMINDER_LAG)).thenReturn(reminderTimeLag);
    }


    @Test
    public void shouldMapTreatmentAdvicesToPillRegimenRequest() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(drugs, properties);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice();
        treatmentAdvice.setPatientId("123");

        List<DrugDosage> drugDosages = new ArrayList<DrugDosage>();
        Date startDateForDrug1 = calendar(2010, 10, 10).getTime();
        Date endDateForDrug1 = calendar(2010, 12, 10).getTime();
        Date startDateForDrug2 = calendar(2011, 02, 10).getTime();
        Date endDateForDrug2 = calendar(2011, 06, 10).getTime();

        when(drugs.get("Drug1Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug1").build());
        when(drugs.get("Drug2Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug2").build());
        String reminderTimeLag = "5";
        propertiesExpectations(reminderTimeLag);
        drugDosages.add(drugDosage("Drug1Id", startDateForDrug1, endDateForDrug1, Arrays.asList("09:00am", "08:30pm")));
        drugDosages.add(drugDosage("Drug2Id", startDateForDrug2, endDateForDrug2, Arrays.asList("09:00am", "05:45pm")));
        treatmentAdvice.setDrugDosages(drugDosages);

        PillRegimenRequest pillRegimenRequest = pillRegimenRequestMapper.map(treatmentAdvice);

        Assert.assertEquals(treatmentAdvice.getPatientId(), pillRegimenRequest.getExternalId());
        Assert.assertNotNull(pillRegimenRequest.getReminderRepeatIntervalInMinutes());
        Assert.assertNotNull(pillRegimenRequest.getReminderRepeatWindowInHours());

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
        for (Iterator<DosageRequest> it = dosageRequests.iterator(); it.hasNext(); ) {
            DosageRequest dosageRequest = it.next();
            if (dosageRequest.getStartHour() == startHour)
                return dosageRequest;
        }

        Assert.assertFalse(true);
        return null;
    }

    private void assertDosageRequestWithReminderTimeLag(DosageRequest dosageRequest, int startHour, int startMinute) {
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