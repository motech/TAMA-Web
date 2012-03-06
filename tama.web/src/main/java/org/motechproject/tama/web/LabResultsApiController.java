package org.motechproject.tama.web;

import org.json.JSONException;
import org.motechproject.tama.patient.service.LabResultsService;
import org.motechproject.tama.web.model.LabResultsJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/labresults")
@Controller
public class LabResultsApiController {
    private LabResultsService labResultsService;

    @Autowired
    public LabResultsApiController(LabResultsService labResultsService) {
        this.labResultsService = labResultsService;
    }

    @RequestMapping(value = "/listCD4Count.json", method = RequestMethod.GET)
    @ResponseBody
    String listCD4Count(@RequestParam(value = "patientId") String patientId, @RequestParam("rangeInMonths")int rangeInMonths) throws JSONException {
        return new LabResultsJson(labResultsService.listCD4Counts(patientId, rangeInMonths)).toString();
    }

    @RequestMapping(value = "/listPVLCount.json", method = RequestMethod.GET)
    @ResponseBody
    public String listPVLLabResults(@RequestParam(value = "patientId") String patientId, @RequestParam("rangeInMonths")int rangeInMonths) throws JSONException {
        return new LabResultsJson(labResultsService.listPVLLabResults(patientId, rangeInMonths)).toString();
    }
}
