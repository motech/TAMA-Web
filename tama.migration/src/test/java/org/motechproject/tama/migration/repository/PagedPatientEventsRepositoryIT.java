package org.motechproject.tama.migration.repository;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationMigrationContext.xml")
public class PagedPatientEventsRepositoryIT extends SpringIntegrationTest {

    @Autowired
    @Qualifier("tamaDbConnector")
    CouchDbConnector tamaCouchDbConnector;

    @Autowired
    PagedPatientEventsRepository pagedPatientsEventsRepository;

    @Override
    public CouchDbConnector getDBConnector() {
        return tamaCouchDbConnector;
    }

    @Test
    public void shouldLoadPatientEventLogs() {
        List<PatientEventLog> patientEvents = asList(
                new PatientEventLog(),
                new PatientEventLog()
        );

        patientEvents.get(0).setPatientId("patientId1");
        patientEvents.get(1).setPatientId("patientId2");

        pagedPatientsEventsRepository.add(patientEvents.get(0), "userName", false);
        pagedPatientsEventsRepository.add(patientEvents.get(1), "userName", false);

        markForDeletion(patientEvents);

        assertEquals("patientId1", pagedPatientsEventsRepository.get(0, 1).get(0).getPatientId());
        assertEquals("patientId2", pagedPatientsEventsRepository.get(1, 1).get(0).getPatientId());
    }
}
