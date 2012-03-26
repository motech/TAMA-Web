package org.motechproject.tama.web;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.ReportedOpportunisticInfections;
import org.motechproject.tama.patient.repository.AllReportedOpportunisticInfections;
import org.motechproject.tama.refdata.domain.OpportunisticInfection;
import org.motechproject.tama.refdata.repository.AllOpportunisticInfections;
import org.motechproject.tama.web.model.OIStatus;
import org.motechproject.tama.web.model.OpportunisticInfectionsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RequestMapping("/opportunisticInfections")
@Controller
public class OpportunisticInfectionsController extends BaseController {

    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";
    private static final String UPDATE_FORM = "opportunisticInfections/update";
    public static final String OPPORTUNISTIC_INFECTIONS_UIMODEL = "opportunisticInfectionsUIModel";
    public static final String OTHER = "Other";

    private AllClinicVisits allClinicVisits;
    private AllReportedOpportunisticInfections allReportedOpportunisticInfections;
    private AllOpportunisticInfections allOpportunisticInfections;

    @Autowired
    public OpportunisticInfectionsController(AllClinicVisits allClinicVisits, AllReportedOpportunisticInfections allReportedOpportunisticInfections,
                                             AllOpportunisticInfections allOpportunisticInfections) {
        this.allClinicVisits = allClinicVisits;
        this.allReportedOpportunisticInfections = allReportedOpportunisticInfections;
        this.allOpportunisticInfections = allOpportunisticInfections;
    }

    public void createForm(ClinicVisit clinicVisit, Model uiModel) {
        uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, OpportunisticInfectionsUIModel.newDefault(clinicVisit, allOpportunisticInfections.getAll()));
    }

    public String create(@Valid OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        try {
            if (bindingResult.hasErrors()) {
                uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, opportunisticInfectionsUIModel);
                return null;
            }

            ReportedOpportunisticInfections reportedOpportunisticInfections = buildReportedOpportunisticInfections(opportunisticInfectionsUIModel);
            if (reportedOpportunisticInfections.getOpportunisticInfectionIds().isEmpty()) {
                return null;
            }

            allReportedOpportunisticInfections.add(reportedOpportunisticInfections, loggedInUserId(httpServletRequest));
            return reportedOpportunisticInfections.getId();
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashErrorOpportunisticInfections", "Error occurred while creating Opportunistic Infections: " + e.getMessage());
        }
        return null;
    }

    private ReportedOpportunisticInfections buildReportedOpportunisticInfections(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel) {
        ReportedOpportunisticInfections reportedOpportunisticInfections = new ReportedOpportunisticInfections();
        reportedOpportunisticInfections.setPatientId(opportunisticInfectionsUIModel.getPatientId());
        reportedOpportunisticInfections.setCaptureDate(DateUtil.today());

        boolean otherInfectionReported = false;

        for (OIStatus oiStatus : opportunisticInfectionsUIModel.getInfections()) {
            if (oiStatus.getReported()) {
                String nameOfInfection = oiStatus.getOpportunisticInfection();
                if(OTHER.equals(nameOfInfection)) otherInfectionReported = true;
                List<OpportunisticInfection> oiList = (List<OpportunisticInfection>) CollectionUtils.select(allOpportunisticInfections.getAll(), withName(nameOfInfection));
                reportedOpportunisticInfections.addOpportunisticInfection(oiList.get(0));
            }
        }
        if (otherInfectionReported && opportunisticInfectionsUIModel.getOtherDetails() != null && !opportunisticInfectionsUIModel.getOtherDetails().isEmpty()) {
            reportedOpportunisticInfections.setOtherOpportunisticInfectionDetails(opportunisticInfectionsUIModel.getOtherDetails());
        }
        return reportedOpportunisticInfections;
    }

    private Predicate withName(final String nameOfInfection) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                OpportunisticInfection opportunisticInfection = (OpportunisticInfection) o;
                return opportunisticInfection.getName().equals(nameOfInfection);
            }
        };
    }

    public void show(ClinicVisit clinicVisit, Model uiModel) {
        populateUIModel(clinicVisit, uiModel);
    }

    private void populateUIModel(ClinicVisit clinicVisit, Model uiModel) {
        if (clinicVisit.getReportedOpportunisticInfectionsId() == null) {
            uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, OpportunisticInfectionsUIModel.newDefault(clinicVisit, allOpportunisticInfections.getAll()));
        } else {
            uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, OpportunisticInfectionsUIModel.create(clinicVisit,
                    allReportedOpportunisticInfections.get(clinicVisit.getReportedOpportunisticInfectionsId()), allOpportunisticInfections.getAll()));
        }
    }

    @RequestMapping(value = "/update", params = "form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel) {
        final ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        populateUIModel(clinicVisit, uiModel);
        uiModel.addAttribute("patient", clinicVisit.getPatient());
        uiModel.addAttribute("_method", "put");
        return UPDATE_FORM;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, HttpServletRequest httpServletRequest) {
        ReportedOpportunisticInfections storedOpportunisticInfections = getStoredOpportunisticInfections(opportunisticInfectionsUIModel);
        if (storedOpportunisticInfections != null) {
            allReportedOpportunisticInfections.remove(storedOpportunisticInfections, loggedInUserId(httpServletRequest));
        }
        ReportedOpportunisticInfections reportedOpportunisticInfections = buildReportedOpportunisticInfections(opportunisticInfectionsUIModel);
        if (reportedOpportunisticInfections.getOpportunisticInfectionIds().isEmpty()) {
            allClinicVisits.updateOpportunisticInfections(opportunisticInfectionsUIModel.getPatientId(),
                    opportunisticInfectionsUIModel.getClinicVisitId(), null);
        } else {
            allReportedOpportunisticInfections.add(reportedOpportunisticInfections, loggedInUserId(httpServletRequest));
            allClinicVisits.updateOpportunisticInfections(opportunisticInfectionsUIModel.getPatientId(),
                    opportunisticInfectionsUIModel.getClinicVisitId(), reportedOpportunisticInfections.getId());
        }

        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(opportunisticInfectionsUIModel.getClinicVisitId(), httpServletRequest) + "?patientId=" + opportunisticInfectionsUIModel.getPatientId();
    }

    private ReportedOpportunisticInfections getStoredOpportunisticInfections(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel) {
        ClinicVisit clinicVisit = allClinicVisits.get(opportunisticInfectionsUIModel.getPatientId(), opportunisticInfectionsUIModel.getClinicVisitId());
        return clinicVisit.getReportedOpportunisticInfectionsId() == null ? null : allReportedOpportunisticInfections.get(clinicVisit.getReportedOpportunisticInfectionsId());
    }
}
