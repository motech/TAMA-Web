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
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.refdata.builder.DrugBuilder;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.objectcache.AllDrugsCache;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PillRegimenRequestMapperTest {

    @Mock
    private AllDrugsCache allDrugs;

    @Mock
    private Properties properties;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldMapTreatmentAdvicesToPillRegimenRequest() {
        final int reminderLag = 5;
        PillRegimenRequestMapper pillRegimenRequestMapper = new PillRegimenRequestMapper(allDrugs, 10, 10, reminderLag);
        when(allDrugs.getBy("Drug1Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug1").build());
        when(allDrugs.getBy("Drug2Id")).thenReturn(DrugBuilder.startRecording().withDefaults().withName("Drug2").build());
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        TreatmentAdvice treatmentAdvice = new TreatmentAdvice() {{
            setPatientId("123");
            setDrugDosages(new ArrayList<DrugDosage>() {{
                add(drugDosage("Drug1Id", DateUtil.newDate(2010, 10, 10), DateUtil.newDate(2010, 12, 10), "09:00am", "08:30pm", 0));
                add(drugDosage("Drug2Id", DateUtil.newDate(2011, 2, 10), DateUtil.newDate(2011, 6, 10), "09:00am", "05:45pm", 15));
            }});
        }};

        DailyPillRegimenRequest pillRegimenRequest = pillRegimenRequestMapper.map(patient, treatmentAdvice);

        assertEquals(treatmentAdvice.getPatientId(), pillRegimenRequest.getExternalId());
        assertNotNull(pillRegimenRequest.getReminderRepeatIntervalInMinutes());
        assertNotNull(pillRegimenRequest.getPillWindowInHours());
        assertEquals(reminderLag, pillRegimenRequest.getBufferOverDosageTimeInMinutes());

        assertEquals(3, pillRegimenRequest.getDosageRequests().size());

        DosageRequest dosageRequest1 = getByStartHour(17, pillRegimenRequest.getDosageRequests());
        assertEquals(1, dosageRequest1.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest1.getMedicineRequests().get(0), "Drug2_brandName", DateUtil.newDate(2011, 2, 25), DateUtil.newDate(2011, 6, 10));

        DosageRequest dosageRequest2 = getByStartHour(20, pillRegimenRequest.getDosageRequests());
        assertEquals(1, dosageRequest2.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest2.getMedicineRequests().get(0), "Drug1_brandName", DateUtil.newDate(2010, 10, 10), DateUtil.newDate(2010, 12, 10));

        DosageRequest dosageRequest3 = getByStartHour(9, pillRegimenRequest.getDosageRequests());
        assertEquals(2, dosageRequest3.getMedicineRequests().size());
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(0), "Drug1_brandName", DateUtil.newDate(2010, 10, 10), DateUtil.newDate(2010, 12, 10));
        assertMedicineRequest(dosageRequest3.getMedicineRequests().get(1), "Drug2_brandName", DateUtil.newDate(2011, 2, 10), DateUtil.newDate(2011, 6, 10));
    }

    @Test
    public void shouldConvertEveningDoseToMedicineRequest() {
        final LocalDate startDate = DateUtil.newDate(2011, 12, 12);
        final int offsetDays = 15;
        final String testBrandName = "testBrand";

        when(allDrugs.getBy(Matchers.<String>any())).thenReturn(new Drug() {
            @Override
            public String fullName(String brandId) {
                return testBrandName;
            }
        });

        final MedicineRequest medicineRequest = mapDrugDosageToMedicineRequest(false, startDate, offsetDays);

        assertEquals(startDate.plusDays(offsetDays), medicineRequest.getStartDate());
    }

    @Test
    public void shouldConvertMorningDoseToMedicineRequest_ForVariableDosage() {
        boolean morningDose = true;
        LocalDate startDate = DateUtil.newDate(2011, 12, 12);
        int offsetDays = 15;
        final String testBrandName = "testBrand";

        when(allDrugs.getBy(Matchers.<String>any())).thenReturn(new Drug() {
            @Override
            public String fullName(String brandId) {
                return testBrandName;
            }
        });

        MedicineRequest medicineRequest = mapDrugDosageToMedicineRequest(morningDose, startDate, offsetDays);

        assertEquals(startDate, medicineRequest.getStartDate());
    }

    private MedicineRequest mapDrugDosageToMedicineRequest(boolean morningDose, final LocalDate startDate, final int offsetDays) {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        final PillRegimenRequestMapper.DrugDosageMedicineRequestConverter drugDosageMedicineRequestConverter = new PillRegimenRequestMapper(allDrugs, 1, 1, 1).new DrugDosageMedicineRequestConverter(morningDose, patient);
        DrugDosage drugDosage = new DrugDosage() {{
            setMorningTime("10:00am");
            setEveningTime("10:00pm");
            setOffsetDays(offsetDays);
            setStartDate(startDate);
        }};

        return drugDosageMedicineRequestConverter.convert(drugDosage);
    }

    private DosageRequest getByStartHour(int startHour, List<DosageRequest> dosageRequests) {
        for (DosageRequest dosageRequest : dosageRequests) {
            if (dosageRequest.getStartHour() == startHour)
                return dosageRequest;
        }
        return null;
    }

    private void assertMedicineRequest(MedicineRequest medicineRequest, String name, LocalDate startDate, LocalDate endDate) {
        Assert.assertEquals(name, medicineRequest.getName());
        Assert.assertEquals(startDate, medicineRequest.getStartDate());
        Assert.assertEquals(endDate, medicineRequest.getEndDate());
    }

    private DrugDosage drugDosage(final String drugId, final LocalDate startDate, final LocalDate endDate, final String morningTime, final String eveningTime, final Integer offsetDays) {
        return new DrugDosage() {{
            setDrugId(drugId);
            setBrandId("brandId");
            setStartDate(startDate);
            setEndDate(endDate);
            setEveningTime(eveningTime);
            setMorningTime(morningTime);
            setOffsetDays(offsetDays);
        }};
    }
}