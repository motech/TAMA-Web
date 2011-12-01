package org.motechproject.tama.web;

import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.tamadomain.domain.LabResult;
import org.motechproject.tamadomain.domain.LabResults;
import org.motechproject.tamadomain.domain.LabTest;
import org.motechproject.tamadomain.repository.AllLabResults;
import org.motechproject.tamadomain.repository.AllLabTests;
import org.motechproject.util.DateUtil;
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
import javax.validation.Valid;
import java.util.List;

@RooWebScaffold(path = "labresults", formBackingObject = LabResultsUIModel.class)
@RequestMapping("/labresults")
@Controller
public class LabResultsController extends BaseController {

    private static final String CREATE_VIEW = "labresults/create";
    public static final String REDIRECT_AND_SHOW_LAB_RESULTS = "redirect:/labresults/";
    public static final String SHOW_VIEW = "labresults/show";

    private final AllLabResults allLabResults;
    private final AllLabTests allLabTests;

    @Autowired
    public LabResultsController(AllLabResults allLabResults, AllLabTests allLabTests) {
        this.allLabResults = allLabResults;
        this.allLabTests = allLabTests;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        if (allLabResults.findByPatientId(patientId).isEmpty()) {
            uiModel.addAttribute("labResultsUIModel", LabResultsUIModel.newDefault());
            populateUIModel(uiModel, patientId);
            return CREATE_VIEW;
        } else {
            return REDIRECT_AND_SHOW_LAB_RESULTS + encodeUrlPathSegment(patientId, httpServletRequest);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid LabResultsUIModel labResultsUiModel, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateUIModel(uiModel, labResultsUiModel.getPatientId());
            uiModel.addAttribute("labResultUiModel", labResultsUiModel);
            return CREATE_VIEW;
        }
        for (LabResult labResult : labResultsUiModel.getLabResults()) {
            this.allLabResults.add(labResult);
        }
        return REDIRECT_AND_SHOW_LAB_RESULTS + encodeUrlPathSegment(labResultsUiModel.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String patientId, Model uiModel) {
        uiModel.addAttribute("labResultsForPatient", allLabResults.findByPatientId(patientId));
        uiModel.addAttribute("patientId", patientId);
        return SHOW_VIEW;
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String patientId, Model uiModel) {
        LabResultsUIModel labResultsUIModel = LabResultsUIModel.newDefault();
        labResultsUIModel.setLabResults(allLabResults.findByPatientId(patientId));
        uiModel.addAttribute("labResultsUIModel", labResultsUIModel);
        uiModel.addAttribute("patientId", patientId);
        return "labresults/update";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid LabResultsUIModel labResultsUIModel, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("labResultsUIModel", labResultsUIModel);
            uiModel.addAttribute("patientId", labResultsUIModel.getPatientId());
            return "labresults/update";
        }
        allLabResults.merge(labResultsUIModel.getLabResults());
        return REDIRECT_AND_SHOW_LAB_RESULTS + encodeUrlPathSegment(labResultsUIModel.getPatientId(), httpServletRequest);
    }

    private void populateUIModel(Model uiModel, String patientId) {
        List<LabTest> labTestsAvailable = allLabTests.getAll();
        uiModel.addAttribute("labTests", labTestsAvailable);

        uiModel.addAttribute("patientId", patientId);
        uiModel.addAttribute("today", DateUtil.today());

        LabResults labResults = createLabResultForEachLabTest(patientId, labTestsAvailable);
        uiModel.addAttribute("labResults", labResults);
    }

    private LabResults createLabResultForEachLabTest(String patientId, List<LabTest> labTestsAvailable) {
        LabResults labResults = new LabResults();
        for (LabTest labTest : labTestsAvailable) {
            LabResult labResult = LabResult.newDefault();
            labResult.setPatientId(patientId);
            labResult.setLabTest(labTest);
            labResult.setLabTest_id(labTest.getId());
            labResults.add(labResult);
        }
        return labResults;
    }
}
