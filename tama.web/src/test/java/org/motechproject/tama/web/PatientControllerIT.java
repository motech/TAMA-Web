package org.motechproject.tama.web;

import org.junit.Test;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ExtendedModelMap;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(locations = "classpath:META-INF/spring/application*Context.xml", inheritLocations = false)
public class PatientControllerIT extends SpringIntegrationTest{

    @Autowired PatientController patientController;
    @Autowired
    AllPatients allPatients;

    @Autowired(required = false)
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();
    
    @Test
    public void shouldActivate() throws Exception {
        final Patient patient = PatientBuilder.startRecording().withDefaults().
                withActivationDate(null).
                build();
        allPatients.add(patient);
        markForDeletion(patient);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCharacterEncoding()).thenReturn(null);

        patientController.activate(patient.getId(), new ExtendedModelMap(), request);

        Patient patientFromDB = allPatients.get(patient.getId());
        assertEquals(Status.Active.toString(), patientFromDB.getStatus().toString());
        assertEquals(DateUtil.today(), patientFromDB.getActivationDate().toLocalDate());
    }
}
