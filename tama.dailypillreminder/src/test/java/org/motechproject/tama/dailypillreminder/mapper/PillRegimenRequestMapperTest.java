package org.motechproject.tama.dailypillreminder.mapper;

import junit.framework.Assert;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DailyPillRegimenRequest;
import org.motechproject.server.pillreminder.contract.DosageRequest;
import org.motechproject.server.pillreminder.contract.MedicineRequest;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.builder.DrugBuilder;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
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
        Drug drug = mock(Drug.class);
        when(allDrugs.get(Matchers.<String>any())).thenReturn(drug);
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setPatientId("123");
            setDrugDosages(new ArrayList<DrugDosage>() {{
                add(drugDosage("Drug1Id", DateUtil.newDate(2010, 10, 10), DateUtil.newDate(2010, 12, 10), "09:00am", "08:30pm"));
                add(drugDosage("Drug2Id", DateUtil.newDate(2011, 2, 10), DateUtil.newDate(2011, 6, 10), "09:00am", "05:45pm"));
            }});
        }};
        DailyPillRegimenRequest request = pillRegimenRequestMapper.map(treatmentAdvice);
        DosageRequest dosageRequest = getByStartHour(9, request.getDosageRequests());
        Assert.assertEquals(5, dosageRequest.getStartMinute());
    }

    @Test
    public void shouldMapTreatmentAdvicesToPillRegimenRequest() {
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(allDrugs, 10, 10, 5);
        when(allDrugs.get("Drug1Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug1").build());
        when(allDrugs.get("Drug2Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug2").build());
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setPatientId("123");
            setDrugDosages(new ArrayList<DrugDosage>() {{
                add(drugDosage("Drug1Id", DateUtil.today().minusDays(10), DateUtil.today().plusYears(1), "09:00am", "08:30pm"));
                add(drugDosage("Drug2Id", DateUtil.today().plusDays(10), DateUtil.today().plusYears(1), "09:00am", "05:45pm"));
            }});
        }};

        DailyPillRegimenRequest pillRegimenRequest = pillRegimenRequestMapper.map(treatmentAdvice);

        Assert.assertEquals(treatmentAdvice.getPatientId(), pillRegimenRequest.getExternalId());
        Assert.assertNotNull(pillRegimenRequest.getReminderRepeatIntervalInMinutes());
        Assert.assertNotNull(pillRegimenRequest.getPillWindowInHours());

        Assert.assertEquals(3, pillRegimenRequest.getDosageRequests().size());

        DosageRequest dosageRequest1 = getByStartHour(17, pillRegimenRequest.getDosageRequests());
        assertDosageRequestWithReminderTimeLag(dosageRequest1, 17, 50);
        Assert.assertEquals(1, dosageRequest1.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest1.getMedicineRequests().get(0), "Drug2_brandName", DateUtil.today().plusDays(10), DateUtil.today().plusYears(1));

        DosageRequest dosageRequest2 = getByStartHour(20, pillRegimenRequest.getDosageRequests());
        assertDosageRequestWithReminderTimeLag(dosageRequest2, 20, 35);
        Assert.assertEquals(1, dosageRequest2.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest2.getMedicineRequests().get(0), "Drug1_brandName", DateUtil.today(), DateUtil.today().plusYears(1));

        DosageRequest dosageRequest3 = getByStartHour(9, pillRegimenRequest.getDosageRequests());
        assertDosageRequestWithReminderTimeLag(dosageRequest3, 9, 5);
        Assert.assertEquals(2, dosageRequest3.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(0), "Drug1_brandName", DateUtil.today(), DateUtil.today().plusYears(1));
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(1), "Drug2_brandName", DateUtil.today().plusDays(10), DateUtil.today().plusYears(1));
    }

    @Test
    public void shouldConvertDosageRequestToMedicine() {
        final LocalDate startDate = DateUtil.today();
        final int offsetDays = 15;
        final String testBrandName = "testBrand";

        when(allDrugs.get(Matchers.<String>any())).thenReturn(new Drug() {
            @Override
            public String fullName(String brandId) {
                return testBrandName;
            }
        });

        final PillRegimenRequestMapper.DrugDosageMedicineRequestConverter drugDosageMedicineRequestConverter = new PillRegimenRequestMapper(allDrugs, 1, 1, 1).new DrugDosageMedicineRequestConverter(true);
        DrugDosage drugDosage = new DrugDosage() {{
            setMorningTime("10:00am");
            setEveningTime("10:00pm");
            setOffsetDays(offsetDays);
            setStartDate(startDate);
        }};

        final MedicineRequest medicineRequest = drugDosageMedicineRequestConverter.convert(drugDosage);

        assertEquals(startDate.plusDays(offsetDays), medicineRequest.getStartDate());
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

    private DrugDosage drugDosage(final String drugId, final LocalDate startDate, final LocalDate endDate, final String morningTime, final String eveningTime) {
        return new DrugDosage() {{
            setDrugId(drugId);
            setBrandId("brandId");
            setStartDate(startDate);
            setEndDate(endDate);
            setEveningTime(eveningTime);
            setMorningTime(morningTime);
        }};
    }
}