package org.motechproject.tama.fourdayrecall.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.testing.utils.BaseUnitTest;

import static junit.framework.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallAdherenceServiceTest extends BaseUnitTest {

    @Mock
    private AdherenceService adherenceService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldDetermineIfTheCurrentAdherenceIsFalling() {
        final int numberOfDaysMissed = 2;
        final String testPatientId = "testPatientId";
        FourDayRecallAdherenceService fourDayRecallService = new FourDayRecallAdherenceService(null, null, null, null, adherenceService) {
            @Override
            public int adherencePercentageFor(int daysMissed) {
                if (numberOfDaysMissed == daysMissed) return 23;
                return 0;
            }

            @Override
            public int getAdherencePercentageForPreviousWeek(String patientId) {
                if (patientId.equals(testPatientId)) return 34;
                return 0;
            }
        };
        assertTrue(fourDayRecallService.isAdherenceFalling(numberOfDaysMissed, testPatientId));
    }

}