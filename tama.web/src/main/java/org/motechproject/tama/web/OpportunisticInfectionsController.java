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
    public static final String OPPORTUNISTIC_INFECTIONS_UIMODEL = "opportunisticInfectionsUIModel";
    private static final String UPDATE_FORM = "opportunisticInfections/update";

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

    public String create(@Valid OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, BindingResult bindingResult, Model uiModel) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, opportunisticInfectionsUIModel);
            return null;
        }
        if(opportunisticInfectionsUIModel.getHasInfectionsReported()) {
            ReportedOpportunisticInfections reportedOpportunisticInfections = buildReportedOpportunisticInfections(opportunisticInfectionsUIModel);
            reportedOpportunisticInfections.setPatientId(opportunisticInfectionsUIModel.getPatientId());
            reportedOpportunisticInfections.setCaptureDate(DateUtil.today());
            allReportedOpportunisticInfections.add(reportedOpportunisticInfections);
            return reportedOpportunisticInfections.getId();
        }
        return null;

    }

    public ReportedOpportunisticInfections buildReportedOpportunisticInfections(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel) {
        ReportedOpportunisticInfections reportedOpportunisticInfections = new ReportedOpportunisticInfections();
        for (OIStatus oiStatus : opportunisticInfectionsUIModel.getInfections()) {
            if (oiStatus.getReported()) {
                String nameOfInfection = oiStatus.getOpportunisticInfection();
                List<OpportunisticInfection> oiList = (List<OpportunisticInfection>) CollectionUtils.select(allOpportunisticInfections.getAll(), withName(nameOfInfection));
                reportedOpportunisticInfections.addOpportunisticInfection(oiList.get(0));
            }
        }
        if(opportunisticInfectionsUIModel.getOtherDetails() != null && !opportunisticInfectionsUIModel.getOtherDetails().isEmpty())
            reportedOpportunisticInfections.setOtherOpportunisticInfectionDetails(opportunisticInfectionsUIModel.getOtherDetails());
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
        ReportedOpportunisticInfections reportedOpportunisticInfections = new ReportedOpportunisticInfections();
        String reportedOpportunisticInfectionsId = clinicVisit.getReportedOpportunisticInfectionsId();
        if (reportedOpportunisticInfectionsId != null) {
            reportedOpportunisticInfections = allReportedOpportunisticInfections.get(reportedOpportunisticInfectionsId);
        }

        OpportunisticInfectionsUIModel opportunisticInfectionsUIModel = OpportunisticInfectionsUIModel.create(clinicVisit, reportedOpportunisticInfections, allOpportunisticInfections.getAll());
        uiModel.addAttribute(OPPORTUNISTIC_INFECTIONS_UIMODEL, opportunisticInfectionsUIModel);
    }

    @RequestMapping(value = "/update", params = "form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel) {
       /* final ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        if (clinicVisit.getOpportunisticInfectionsId() == null) {
            uiModel.addAttribute("OpportunisticInfectionsUIModel", OpportunisticInfectionsUIModel.newDefault(clinicVisit));
        } else {
            uiModel.addAttribute("OpportunisticInfectionsUIModel", OpportunisticInfectionsUIModel.get(clinicVisit, allReportedOpportunisticInfections.get(clinicVisit.getOpportunisticInfectionsId())));
        }
        uiModel.addAttribute("patient", clinicVisit.getPatient());
        uiModel.addAttribute("_method", "put");*/
        return UPDATE_FORM;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(OpportunisticInfectionsUIModel opportunisticInfectionsUIModel, HttpServletRequest httpServletRequest) {
       /* ReportedOpportunisticInfections opportunisticInfections = opportunisticInfectionsUIModel.getOpportunisticInfections();
        opportunisticInfections.setCaptureDate(DateUtil.today());
        if (opportunisticInfections.getId() == null || opportunisticInfections.getId().isEmpty()) {
            allReportedOpportunisticInfections.add(opportunisticInfections);
            allClinicVisits.updateOpportunisticInfections(opportunisticInfectionsUIModel.getPatientId(), opportunisticInfectionsUIModel.getClinicVisitId(), opportunisticInfections.getId());
        } else {
            ReportedOpportunisticInfections savedOpportunisticInfections = allReportedOpportunisticInfections.get(opportunisticInfections.getId());
            opportunisticInfections.setRevision(savedOpportunisticInfections.getRevision());
            allReportedOpportunisticInfections.update(opportunisticInfections);
            allClinicVisits.updateOpportunisticInfections(opportunisticInfectionsUIModel.getPatientId(), opportunisticInfectionsUIModel.getClinicVisitId(), opportunisticInfections.getId());
        }
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(opportunisticInfectionsUIModel.getClinicVisitId(), httpServletRequest) + "?patientId=" + opportunisticInfectionsUIModel.getPatientId();*/
        return null;
    }
}
