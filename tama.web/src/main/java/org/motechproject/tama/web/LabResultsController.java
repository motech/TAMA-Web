package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.motechproject.tama.web.model.LabResultsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/labresults")
@Controller
public class LabResultsController extends BaseController {

    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";

    private final AllLabResults allLabResults;
    private final AllLabTests allLabTests;

    @Autowired
    public LabResultsController(AllLabResults allLabResults, AllLabTests allLabTests) {
        this.allLabResults = allLabResults;
        this.allLabTests = allLabTests;
    }

    public void createForm(String patientId, Model uiModel) {
        uiModel.addAttribute("labResultsUIModel", LabResultsUIModel.newDefault());
        populateUIModel(uiModel, patientId);
    }

    public void create(LabResultsUIModel labResultsUiModel, BindingResult bindingResult, Model uiModel) {
        if (bindingResult.hasErrors()) {
            populateUIModel(uiModel, labResultsUiModel.getPatientId());
            uiModel.addAttribute("labResultUiModel", labResultsUiModel);
            return;
        }
        for (LabResult labResult : labResultsUiModel.getLabResults()) {
            this.allLabResults.upsert(labResult);
        }
    }

    public void show(String patientId, Model uiModel) {
        uiModel.addAttribute("labResultsForPatient", allLabResults.findLatestLabResultsByPatientId(patientId));
        uiModel.addAttribute("patientId", patientId);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String patientId, Model uiModel) {
        LabResultsUIModel labResultsUIModel = LabResultsUIModel.newDefault();
        labResultsUIModel.setLabResults(allLabResults.findLatestLabResultsByPatientId(patientId));
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
        for (LabResult labResult : labResultsUIModel.getLabResults()) {
            allLabResults.upsert(labResult);
        }
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(labResultsUIModel.getPatientId(), httpServletRequest);
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
