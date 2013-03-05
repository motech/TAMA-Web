package org.motechproject.tama.migration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.common.domain.TimeMeridiem;
import org.motechproject.tama.common.domain.TimeOfDay;
import org.motechproject.tama.migration.repository.PagedPatientsRepository;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RemoveBestCallTimeJobMigrationTest {

    @Mock
    private OutboxService outboxService;
    @Mock
    private PagedPatientsRepository patientRepository;
    private RemoveBestCallTimeJobMigration removeBestCallTimeJobMigration;

    @Before
    public void setup() {
        initMocks(this);
        removeBestCallTimeJobMigration = new RemoveBestCallTimeJobMigration(patientRepository, outboxService);
    }

    @Test
    public void shouldNotDisEnrollFourDayRecallPatient() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        removeBestCallTimeJobMigration.save(patient);
        verify(outboxService, never()).disEnroll(patient);
    }

    @Test
    public void shouldNotDisEnrollPatientsWhoHaveNotOptedToBeCalledOnBestCallTime() {
        Patient patient = PatientBuilder.startRecording()
                .withDefaults()
                .withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(null)
                .build();
        removeBestCallTimeJobMigration.save(patient);
        verify(outboxService, never()).disEnroll(patient);
    }

    @Test
    public void shouldDisEnrollPatientsWithDailyPillReminderBestCallTimeCall() {
        Patient patient = PatientBuilder.startRecording()
                .withDefaults()
                .withCallPreference(CallPreference.DailyPillReminder)
                .withBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM))
                .build();
        removeBestCallTimeJobMigration.save(patient);
        verify(outboxService).disEnroll(patient);
    }
}
