package org.motechproject.tama.web.command;

import static org.junit.Assert.fail;
import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.DosageUtil;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DosageUtil.class)
public class WeeklyAdherenceOutBoxCommandTest {
	
	WeeklyAdherenceOutBoxCommand weeklyAdherenceOutBoxCommand;
	
	@Mock
	IVRSession ivrSession;
	@Mock
	PillReminderService pillReminderService;
	@Mock
	PillRegimenResponse pillRegimenResponse;
	@Mock
	AllDosageAdherenceLogs allDosageAdherenceLogs;
	
	@Before
	public void setUp() throws NoSuchFieldException{
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(DosageUtil.class);
		weeklyAdherenceOutBoxCommand = new WeeklyAdherenceOutBoxCommand();
		PrivateAccessor.setField(weeklyAdherenceOutBoxCommand, "pillReminderService", pillReminderService);
		PrivateAccessor.setField(weeklyAdherenceOutBoxCommand, "allDosageAdherenceLogs", allDosageAdherenceLogs);
	}

	@Test
	public void testExecute() {
		String externalId = "someExternalId";
		String pillRegimenId = "pillRegimenId";
		int successCountThisWeek = 23;
		int successCountLastWeek = 23;
		int scheduledDosageCount = 28;
		String[] result; 
		
		Mockito.when(ivrSession.getExternalId()).thenReturn(externalId);
		Mockito.when(pillReminderService.getPillRegimen(Mockito.anyString())).thenReturn(pillRegimenResponse);
		Mockito.when(pillRegimenResponse.getPillRegimenId()).thenReturn(pillRegimenId);
		DateTime now = DateUtil.now();
		Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(pillRegimenId, now.minusWeeks(4).toLocalDate(), now.toLocalDate())).thenReturn(successCountThisWeek);
		Mockito.when(allDosageAdherenceLogs.findScheduledDosagesSuccessCount(pillRegimenId, now.minusWeeks(5).toLocalDate(), now.minusWeeks(1).toLocalDate())).thenReturn(successCountLastWeek);
		PowerMockito.when(DosageUtil.getScheduledDosagesTotalCount(Mockito.any(DateTime.class), Mockito.any(DateTime.class), Mockito.any(PillRegimenResponse.class))).thenReturn(scheduledDosageCount);
		
		
		result = weeklyAdherenceOutBoxCommand.execute(ivrSession);
		
		Assert.assertEquals(result[0], TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);
				
	}
}
