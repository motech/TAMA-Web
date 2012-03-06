package org.motechproject.tama.web;

import org.json.JSONException;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.service.VitalStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/vitalstatistics")
@Controller
public class VitalStatisticsApiController {

    private VitalStatisticsService vitalStatisticsService;

    @Autowired
    public VitalStatisticsApiController(VitalStatisticsService vitalStatisticsService) {
        this.vitalStatisticsService = vitalStatisticsService;
    }

    @RequestMapping(value = "/listWeightOverTime.json", method = RequestMethod.GET)
    @ResponseBody
    public String listWeightOverTime(@RequestParam(value = "patientId") String patientId, @RequestParam("rangeInMonths") int rangeInMonths) throws JSONException {
        List<VitalStatistics> vitalStatisticsList = vitalStatisticsService.getAllFor(patientId, rangeInMonths);
        return new VitalStatisticsJson(vitalStatisticsList).weightList().toString();
    }


    @RequestMapping(value = "/listBPOverTime.json", method = RequestMethod.GET)
    @ResponseBody
    public String listBPOverTime(@RequestParam(value = "patientId") String patientId, @RequestParam("rangeInMonths") int rangeInMonths) throws JSONException {
        List<VitalStatistics> vitalStatisticsList = vitalStatisticsService.getAllFor(patientId, rangeInMonths);
        return new VitalStatisticsJson(vitalStatisticsList).bpList().toString();
    }
}
