package org.motechproject.tama.tools.controller;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderAdherenceService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping("/tama-tools/backfill")
@Controller
public class BackFillAdherenceController {

    DailyPillReminderAdherenceService dailyPillReminderAdherenceService;

    @Autowired
    public BackFillAdherenceController(DailyPillReminderAdherenceService dailyPillReminderAdherenceService) {
        this.dailyPillReminderAdherenceService = dailyPillReminderAdherenceService;
    }

    @RequestMapping(value = "daily", method = RequestMethod.POST)
    @ResponseBody
    public String daily(@RequestParam String fromDateString, @RequestParam String toDateString, @RequestParam DosageStatus dosageStatus, @RequestParam String patientDocId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            DateTime fromDate = DateUtil.newDateTime(LocalDate.parse(fromDateString));
            DateTime toDate = DateUtil.newDateTime(LocalDate.parse(toDateString));

            dailyPillReminderAdherenceService.backFillAdherence(patientDocId, fromDate, toDate, dosageStatus);
            return "Done";
        } catch (Exception e) {
            return "Error:" + e.getMessage();
        }
    }
}
