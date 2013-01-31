package org.motechproject.tama.web;

import org.motechproject.tama.reporting.properties.ReportingProperties;
import org.motechproject.tama.web.model.DateFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("analysisData")
public class AnalysisDataController extends BaseController {

    private CallSummaryController callSummaryController;
    private ReportingProperties reportingProperties;

    @Autowired
    public AnalysisDataController(CallSummaryController callSummaryController, ReportingProperties reportingProperties) {
        this.callSummaryController = callSummaryController;
        this.reportingProperties = reportingProperties;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String show(Model uiModel) {
        uiModel.addAttribute("patientDateFilter", new DateFilter());
        uiModel.addAttribute("patientEventDateFilter", new DateFilter());
        uiModel.addAttribute("reports_url", reportingProperties.reportingURL());
        callSummaryController.filterLogs(uiModel);
        return "analysisData/show";
    }
}
