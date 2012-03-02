package org.motechproject.tama.web;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/labresults")
@Controller
public class LabResultsController extends BaseController {

    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";

    private final AllLabResults allLabResults;
    private final AllClinicVisits allClinicVisits;
    private final AllLabTests allLabTests;

    @Autowired
    public LabResultsController(AllLabResults allLabResults, AllLabTests allLabTests, AllClinicVisits allClinicVisits) {
        this.allLabResults = allLabResults;
        this.allLabTests = allLabTests;
        this.allClinicVisits = allClinicVisits;
    }

    public void createForm(String patientId, Model uiModel) {
        uiModel.addAttribute("labResultsUIModel", LabResultsUIModel.newDefault());
        populateUIModel(uiModel, patientId);
    }

    public List<String> create(LabResultsUIModel labResultsUiModel, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        List<String> labResultIds = new ArrayList<String>();
        try {
            if (bindingResult.hasErrors()) {
                populateUIModel(uiModel, labResultsUiModel.getPatientId());
                uiModel.addAttribute("labResultUiModel", labResultsUiModel);
                return labResultIds;
            }
            for (LabResult labResult : labResultsUiModel.getLabResults()) {
                if (labResult.getResult() == null || labResult.getResult().isEmpty()) continue;
                String labResultId = this.allLabResults.upsert(labResult);
                labResultIds.add(labResultId);
            }
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashErrorLabResults", "Error occurred while creating Lab Results: " + e.getMessage());
        }
        return labResultIds;
    }

    public void show(String patientId, String clinicVisitId, List<String> labResultIds, Model uiModel) {
        LabResults labResults = new LabResults();
        for (String labResultId : labResultIds) {
            labResults.add(allLabResults.get(labResultId));
        }
        uiModel.addAttribute("labResultsForPatient", labResults);
        uiModel.addAttribute("patientId", patientId);
        uiModel.addAttribute("clinicVisitId", clinicVisitId);
    }

    @RequestMapping(value = "/update", params = "form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel) {
        LabResultsUIModel labResultsUIModel = LabResultsUIModel.newDefault();
        final ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        final Map<String, LabResult> labResultsMap = emptyLabResultsForAllLabTests(patientDocId);
        for (String labResultId : clinicVisit.getLabResultIds()) {
            final LabResult labResult = allLabResults.get(labResultId);
            labResultsMap.put(labResult.getLabTest_id(), labResult);
        }

        labResultsUIModel.setLabResults(new LabResults(labResultsMap.values()));
        uiModel.addAttribute("labResultsUIModel", labResultsUIModel);
        uiModel.addAttribute("patient", clinicVisit.getPatient());
        uiModel.addAttribute("clinicVisitId", clinicVisitId);
        return "labresults/update";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(LabResultsUIModel labResultsUIModel, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("labResultsUIModel", labResultsUIModel);
            return "labresults/update";
        }
        List<String> allLabResultsIds = new ArrayList<String>();
        for (LabResult labResult : labResultsUIModel.getLabResults()) {
            final String labResultId = allLabResults.upsert(labResult);
            if (labResultId != null) allLabResultsIds.add(labResultId);
        }

        allClinicVisits.updateLabResults(labResultsUIModel.getPatientId(), labResultsUIModel.getClinicVisitId(), allLabResultsIds);
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(labResultsUIModel.getClinicVisitId(), httpServletRequest) + "?patientId=" + labResultsUIModel.getPatientId();
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

    private Map<String, LabResult> emptyLabResultsForAllLabTests(String patientId) {
        final List<LabTest> labTests = allLabTests.getAll();
        Map<String, LabResult> labResults = new HashMap<String, LabResult>();
        for (LabTest labTest : labTests) {
            LabResult labResult = LabResult.newDefault();
            labResult.setPatientId(patientId);
            labResult.setLabTest(labTest);
            labResult.setLabTest_id(labTest.getId());
            labResults.put(labTest.getId(), labResult);
        }
        return labResults;
    }
}
