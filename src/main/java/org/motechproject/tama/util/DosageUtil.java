package org.motechproject.tama.util;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.util.DateUtil;

public class DosageUtil {
	public static int getScheduledDosagesTotalCount(DateTime startDate, DateTime endDate, PillRegimenResponse pillRegimen) {
		int totalCount = 0;
        for (DosageResponse dosageResponse : pillRegimen.getDosages()) {
            DateTime dosageStartDate = DateUtil.newDateTime(dosageResponse.getStartDate(), dosageResponse.getDosageHour(), dosageResponse.getDosageMinute(), 0);

            DateTime fromDate = dosageStartDate.minusHours(pillRegimen.getReminderRepeatWindowInHours());
            if (endDate.isBefore(fromDate)) continue;
            
            Days days = Days.daysBetween(startDate.isBefore(fromDate)?fromDate:startDate, endDate);
            int dayCount = days.getDays() + 1;
            totalCount += Math.min(dayCount, TAMAConstants.DAYS_IN_FOUR_WEEKS);
        }
        return totalCount;
	}
}
