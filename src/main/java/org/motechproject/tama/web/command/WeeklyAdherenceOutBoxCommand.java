package org.motechproject.tama.web.command;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.motechproject.decisiontree.model.ITreeCommand;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.tama.ivr.PillRegimenSnapshot;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public class WeeklyAdherenceOutBoxCommand implements ITreeCommand {

	@Autowired
	AllDosageAdherenceLogs allDosageAdherenceLogs;

	@Override
	public String[] execute(Object oContext) {
	ArrayList<String> result =  new ArrayList<String>();
		if (oContext instanceof IVRContext) {
			IVRContext ivrContext = (IVRContext) oContext;
			String patientId = ivrContext.ivrSession().getExternalId();
			DateTime now = DateUtil.now();
			PillRegimenSnapshot pillRegimenSnapshot = new PillRegimenSnapshot(ivrContext, now);
			String regimenId = TamaSessionUtil.getRegimenIdFrom(ivrContext);

			double adherencePercentageAsOfNow = getAdherencePercentage(regimenId, now, pillRegimenSnapshot);
			double adherencePercentageAsOfLastWeek = getAdherencePercentage(regimenId, now.minusWeeks(1), pillRegimenSnapshot);
			boolean falling = adherencePercentageAsOfNow < adherencePercentageAsOfLastWeek;

			if (adherencePercentageAsOfNow > 0.9) {
				// A message saying indicating that I’ve done well and should
				// try not to miss a single dose is played to me
				result.add(TamaIVRMessage.M02_04_ADHERENCE_COMMENT_GT95_FALLING);
			} else if (adherencePercentageAsOfNow > 0.7) {
				if (falling) {
					// A message saying my adherence can improve and I need to
					// take my doses more regularly should be played to me
					result.add(TamaIVRMessage.M02_05_ADHERENCE_COMMENT_70TO90_FALLING);
				} else {
					// A message indicating my adherence is improving but it can
					// improve further is played
					result.add(TamaIVRMessage.M02_06_ADHERENCE_COMMENT_70TO90_RISING);
				}
			} else {
				if (falling) {
					// A message indicating my adherence needs to improve
					// substantially is played
					result.add(TamaIVRMessage.M02_07_ADHERENCE_COMMENT_LT70_FALLING);
				} else {
					// A message indicating my adherence is improving but it
					// needs to improve further is played
					result.add(TamaIVRMessage.M02_08_ADHERENCE_COMMENT_LT70_RISING);
				}
			}
		}
		return result.toArray(new String[0]);
	}

	private double getAdherencePercentage(String regimenId, DateTime asOfDate, PillRegimenSnapshot pillRegimenSnapshot) {
		int scheduledDosagesTotalCountForLastFourWeeksAsOfNow = pillRegimenSnapshot.getScheduledDosagesTotalCount(asOfDate);
		int dosagesTakenForLastFourWeeksAsOfNow = allDosageAdherenceLogs.findScheduledDosagesSuccessCount(regimenId, asOfDate.toLocalDate(), asOfDate
				.minusWeeks(4).toLocalDate());
		return dosagesTakenForLastFourWeeksAsOfNow / scheduledDosagesTotalCountForLastFourWeeksAsOfNow;
	}
}