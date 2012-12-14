package org.motechproject.tama.web.model;

import org.junit.Test;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.domain.TypeOfVisit;
import org.motechproject.tama.patient.domain.Patient;

import static org.openqa.selenium.support.testing.Assertions.assertEquals;

public class ClinicVisitUIModelTest {

    @Test
    public void shouldBeTitledWhenBaseline() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTypeOfVisit(TypeOfVisit.Baseline.name());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);

        ClinicVisitUIModel uiModel = new ClinicVisitUIModel(clinicVisit);
        assertEquals("Activated in TAMA", uiModel.getTitle());
    }

    @Test
    public void shouldBeTitledWithWeekNumberWhenScheduled() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTypeOfVisit(TypeOfVisit.Scheduled.name());
        visitResponse.getVisitData().put(ClinicVisit.WEEK_NUMBER, 10);
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);

        ClinicVisitUIModel uiModel = new ClinicVisitUIModel(clinicVisit);
        assertEquals("10 weeks Follow-up visit", uiModel.getTitle());
    }

    @Test
    public void shouldBeTitledWhenUnscheduled() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTypeOfVisit(TypeOfVisit.Unscheduled.name());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);

        ClinicVisitUIModel uiModel = new ClinicVisitUIModel(clinicVisit);
        assertEquals("Ad-hoc Visit", uiModel.getTitle());
    }

    @Test
    public void shouldBeTitledWhenUnscheduledWithAppointment() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTypeOfVisit(TypeOfVisit.UnscheduledWithAppointment.name());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);

        ClinicVisitUIModel uiModel = new ClinicVisitUIModel(clinicVisit);
        assertEquals("Ad-hoc Visit by appointment", uiModel.getTitle());
    }

    @Test
    public void shouldHaveTypeOfVisitAsUnscheduled() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTypeOfVisit(TypeOfVisit.Unscheduled.name());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);

        ClinicVisitUIModel uiModel = new ClinicVisitUIModel(clinicVisit);
        assertEquals("Unscheduled", uiModel.getTypeOfVisit());
    }

    @Test
    public void shouldHaveTypeOfVisitAsUnscheduledWhenScheduledWithAppointment() {
        VisitResponse visitResponse = new VisitResponse();
        visitResponse.setTypeOfVisit(TypeOfVisit.UnscheduledWithAppointment.name());
        ClinicVisit clinicVisit = new ClinicVisit(new Patient(), visitResponse);

        ClinicVisitUIModel uiModel = new ClinicVisitUIModel(clinicVisit);
        assertEquals("Unscheduled", uiModel.getTypeOfVisit());
    }
}
