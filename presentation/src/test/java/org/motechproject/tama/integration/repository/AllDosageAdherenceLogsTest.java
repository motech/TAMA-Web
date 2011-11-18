package org.motechproject.tama.integration.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllDosageAdherenceLogsTest extends SpringIntegrationTest {
    
    @Autowired
    private AllDosageAdherenceLogs allDosageAdherenceLogs;

    @Before
    public void before() {
        super.before();
        initMocks(this);
    }

    @After
    public void after() {
        super.after();
    }

    @Test
    public void shouldLoadPatientByRegimenIdAndDosageStatus() {
        DosageAdherenceLog dosageAdherenceLog1 = adherenceLog("1", DosageStatus.NOT_TAKEN);
        DosageAdherenceLog dosageAdherenceLog2 = adherenceLog("1", DosageStatus.TAKEN);
        DosageAdherenceLog dosageAdherenceLog3 = adherenceLog("1", DosageStatus.WILL_TAKE_LATER);
        DosageAdherenceLog dosageAdherenceLog4 = adherenceLog("2", DosageStatus.TAKEN);
        allDosageAdherenceLogs.add(dosageAdherenceLog1);
        allDosageAdherenceLogs.add(dosageAdherenceLog2);
        allDosageAdherenceLogs.add(dosageAdherenceLog3);
        allDosageAdherenceLogs.add(dosageAdherenceLog4);

        markForDeletion(dosageAdherenceLog1, dosageAdherenceLog2, dosageAdherenceLog3, dosageAdherenceLog4);
        assertEquals(1, allDosageAdherenceLogs.findScheduledDosagesSuccessCount("1"));
    }

    private DosageAdherenceLog adherenceLog(String regimenId, DosageStatus dosageStatus) {
        DosageAdherenceLog adherenceLog = new DosageAdherenceLog();
        adherenceLog.setRegimenId(regimenId);
        adherenceLog.setDosageStatus(dosageStatus);
        return adherenceLog;
    }
}
