package org.motechproject.tama.web;

import org.json.JSONException;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.service.LabResultsService;
import org.motechproject.tama.web.model.CD4Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@RequestMapping("/json/labresults")
@Controller
public class LabResultsApiController {
    private LabResultsService labResultsService;

    @Autowired
    public LabResultsApiController(LabResultsService labResultsService) {
        this.labResultsService = labResultsService;
    }
    @RequestMapping(value = "/listCD4Count", method = RequestMethod.POST)
    @ResponseBody
    String listCD4Count(@RequestParam(value = "patientId") String patientId, @RequestParam("rangeInMonths")int rangeInMonths) throws JSONException {
        return new CD4Json(labResultsService.listCD4Counts(patientId, rangeInMonths)).toString();
    }
}
