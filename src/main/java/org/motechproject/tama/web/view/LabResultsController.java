package org.motechproject.tama.web.view;

import org.motechproject.tama.domain.LabResult;
import org.motechproject.tama.repository.LabResults;
import org.motechproject.tama.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RooWebScaffold(path = "labresults", formBackingObject = LabResult.class)
@RequestMapping("/labresults")
@Controller
public class LabResultsController extends BaseController {

    private static final String CREATE_VIEW = "labresults/create";
    public static final String REDIRECT_AND_SHOW_LAB_RESULTS = "redirect:/labresults/";

    private final LabResults labResults;

    @Autowired
    public LabResultsController(LabResults labResults) {
        this.labResults = labResults;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        LabResult labResult = LabResult.newDefault();
        labResult.setPatientId(patientId);
        uiModel.addAttribute("labResult", labResult);
        return CREATE_VIEW;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(LabResult labResult, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        labResults.add(labResult);
        return REDIRECT_AND_SHOW_LAB_RESULTS + encodeUrlPathSegment(labResult.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("labresults",labResults.findByPatientId(id));
        return "labresults/show";
    }
}
