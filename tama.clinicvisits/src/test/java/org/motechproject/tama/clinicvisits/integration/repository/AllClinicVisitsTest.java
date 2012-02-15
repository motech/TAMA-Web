package org.motechproject.tama.clinicvisits.integration.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.builder.ClinicVisitBuilder;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@ContextConfiguration(locations = "classpath*:applicationClinicVisitsContext.xml", inheritLocations = false)
public class AllClinicVisitsTest extends SpringIntegrationTest {

    @Autowired
    private AllClinicVisits allClinicVisits;

    @Before
    public void setUp() {
        markForDeletion(allClinicVisits.getAll().toArray());
    }

    @Test
    public void shouldSaveClinicVisit() {
        final String patientId = "patientId";
        final String vitalStatisticsId = "vitalStatisticsId";
        final String treatmentAdviceId = "treatmentAdviceId";
        final ArrayList<String> labResultIds = new ArrayList<String>() {{
            add("labResultId");
        }};

        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().build();
        allClinicVisits.add(clinicVisit);

        assertNotNull(clinicVisit.getId());

        ClinicVisit savedClinicVisit = allClinicVisits.get(clinicVisit.getId());
        assertEquals(patientId, savedClinicVisit.getPatientId());
        assertEquals(DateUtil.today(), savedClinicVisit.getVisitDate());
        assertEquals(treatmentAdviceId, savedClinicVisit.getTreatmentAdviceId());
        assertEquals(labResultIds, savedClinicVisit.getLabResultIds());
        assertEquals(vitalStatisticsId, savedClinicVisit.getVitalStatisticsId());
        markForDeletion(savedClinicVisit);
    }

    @Test
    public void shouldFindByPatientId() {
        final ClinicVisit clinicVisit1 = ClinicVisitBuilder.startRecording().withPatientId("pid1").withVisitDate(DateUtil.today()).build();
        final ClinicVisit clinicVisit2 = ClinicVisitBuilder.startRecording().withPatientId("pid2").withVisitDate(DateUtil.today()).build();
        final ClinicVisit clinicVisit3 = ClinicVisitBuilder.startRecording().withPatientId("pid1").withVisitDate(DateUtil.today().minusDays(1)).build();

        allClinicVisits.add(clinicVisit1);
        allClinicVisits.add(clinicVisit2);
        allClinicVisits.add(clinicVisit3);

        final List<ClinicVisit> clinicVisits = allClinicVisits.find_by_patient_id("pid1");
        for (ClinicVisit clinicVisit : clinicVisits) {
            assertEquals("pid1", clinicVisit.getPatientId());
        }
    }

    @Test
    public void shouldUpdateTreatmentAdviceId() {
        final ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withPatientId("pid1").withVisitDate(DateUtil.today()).build();
        allClinicVisits.add(clinicVisit);

        clinicVisit.setTreatmentAdviceId("newNew");
        allClinicVisits.update(clinicVisit);

        final ClinicVisit updatedClinicVisit = allClinicVisits.get(clinicVisit.getId());
        assertEquals("newNew", updatedClinicVisit.getTreatmentAdviceId());
    }

    @Test
    public void shouldNotSetVisitDateIfItIsNotInTheDocument() {
        ClinicVisit clinicVisit = ClinicVisitBuilder.startRecording().withDefaults().withVisitDate(null).build();
        allClinicVisits.add(clinicVisit);
        assertNull(allClinicVisits.get(clinicVisit.getId()).getVisitDate());
    }
}
