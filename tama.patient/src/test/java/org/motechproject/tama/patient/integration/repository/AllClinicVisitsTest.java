package org.motechproject.tama.patient.integration.repository;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.domain.ClinicVisit;
import org.motechproject.tama.patient.repository.AllClinicVisits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllClinicVisitsTest extends SpringIntegrationTest {

    @Autowired
    private AllClinicVisits allClinicVisits;

    @Test
    public void shouldSaveClinicVisit() {
        ClinicVisit clinicVisit = new ClinicVisit() {{
            setTreatmentAdviceId("treatmentAdviceId");
            setLabResultIds(new ArrayList<String>() {{ add("labResultId"); }});
            setVitalStatisticsId("vitalStatisticsId");
        }};

        allClinicVisits.add(clinicVisit);

        assertNotNull(clinicVisit.getId());
        markForDeletion(clinicVisit);

        ClinicVisit savedClinicVisit = allClinicVisits.get(clinicVisit.getId());
        assertEquals("treatmentAdviceId", savedClinicVisit.getTreatmentAdviceId());
        assertEquals(new ArrayList<String>() {{ add("labResultId"); }}, savedClinicVisit.getLabResultIds());
        assertEquals("vitalStatisticsId" , savedClinicVisit.getVitalStatisticsId());
    }
}
