package org.motechproject.tama.dailypillreminder.integration.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.common.NoAdherenceRecordedException;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@PrepareForTest(DateUtil.class)
@ContextConfiguration(locations = "classpath*:applicationDailyPillReminderContext.xml", inheritLocations = false)
public class DailyReminderAdherenceServiceIT extends SpringIntegrationTest {

    @Mock
    private DailyPillReminderService dailyPillReminderService;

    @Mock
    private PatientAlertService patientAlertService;

    @Autowired
    private AllPatients allPatients;

    @Autowired
    private AllTreatmentAdvices allTreatmentAdvices;

    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Autowired
    private AllUniquePatientFields allUniquePatientFields;

    private DailyPillReminderAdherenceService dailyReminderAdherenceService;

    @Qualifier("ivrProperties")
    private Properties properties;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private Patient patient;

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyPillReminderAdherenceService(allDosageAdherenceLogs, dailyPillReminderService, properties, new AdherenceService(), allPatients, allTreatmentAdvices);
        setUpDate();
        patient = PatientBuilder.startRecording().withCallPreference(CallPreference.DailyPillReminder).build();
    }

    public void setUpDate() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
    }

    @Test
    public void adherenceWhenLessThan4WeeksIntoRegimen() throws NoAdherenceRecordedException {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", patient.getId(), 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(1), null, null, null));
        }});
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        addAdherenceLog("dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusDays(2));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusDays(3));
        addAdherenceLog("dosage1Id", DosageStatus.NOT_RECORDED, DateUtil.today().minusDays(4));
        addAdherenceLog("dosage1Id", DosageStatus.NOT_RECORDED, DateUtil.today().minusDays(5));
        addAdherenceLog("dosage1Id", DosageStatus.NOT_RECORDED, DateUtil.today().minusDays(6));
        addAdherenceLog("dosage1Id", DosageStatus.NOT_RECORDED, DateUtil.today().minusDays(7));
        patient.setPatientId("patientId1");
        patient.getPatientPreferences().setPasscode("1234");

        allPatients.add(patient);
        markForDeletion(allPatients.getAll().toArray());
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());
        markForDeletion(allUniquePatientFields.getAll().toArray());

        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(new PillRegimen(regimenStartingToday));

        final DateTime afterDoseTime = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(25.00, dailyReminderAdherenceService.getAdherencePercentage(patient.getId(), afterDoseTime));
    }

    @Test
    public void adherenceWhenLessThan4WeeksIntoRegimen_WhenPatientTransitionedFromDailyPillReminderToWeeklyPillReminder_AndBackToDailyPillReminder() throws NoAdherenceRecordedException {
        PillRegimenResponse regimenStartingToday = new PillRegimenResponse("pillRegimenId", patient.getId(), 2, 5, new ArrayList<DosageResponse>() {{
            add(new DosageResponse("dosage1Id", new Time(10, 30), DateUtil.today().minusWeeks(2), null, null, null));
        }});
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today());
        addAdherenceLog("dosage1Id", DosageStatus.NOT_TAKEN, DateUtil.today().minusDays(1));
        addAdherenceLog("dosage1Id", DosageStatus.WILL_TAKE_LATER, DateUtil.today().minusDays(2));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusDays(3));
        addAdherenceLog("dosage1Id", DosageStatus.TAKEN, DateUtil.today().minusDays(4));
        patient.setPatientId("patientId9");
        patient.getPatientPreferences().setCallPreferenceTransitionDate(DateUtil.now().minusDays(5));
        patient.getPatientPreferences().setPasscode("4444");

        allPatients.add(patient);
        markForDeletion(allPatients.getAll().toArray());
        markForDeletion(allDosageAdherenceLogs.getAll().toArray());
        markForDeletion(allUniquePatientFields.getAll().toArray());

        when(dailyPillReminderService.getPillRegimen(patient.getId())).thenReturn(new PillRegimen(regimenStartingToday));

        final DateTime afterDoseTime = DateUtil.now().withHourOfDay(11).withMinuteOfHour(30);
        assertEquals(60.00, dailyReminderAdherenceService.getAdherencePercentage(patient.getId(), afterDoseTime));
    }

    private void addAdherenceLog(String dosageId, DosageStatus dosageStatus, LocalDate dosageDate) {
        allDosageAdherenceLogs.add(new DosageAdherenceLog(patient.getId(), "pillRegimenId", dosageId, "treatmentAdviceId", dosageStatus, dosageDate, new Time(10, 5), DateUtil.newDateTime(dosageDate, 0, 0, 0)));
    }
}
