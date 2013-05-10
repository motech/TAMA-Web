package org.motechproject.tama.web;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.web.model.VitalStatisticsUIModel;
import org.motechproject.tama.web.service.PatientDetailsService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/vitalstatistics")
@Controller
public class VitalStatisticsController extends BaseController {

    private static final String UPDATE_FORM = "vitalstatistics/update";
    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";

    private final AllVitalStatistics allVitalStatistics;
    private AllClinicVisits allClinicVisits;
    private PatientDetailsService patientDetailsService;

    @Autowired
    public VitalStatisticsController(AllVitalStatistics allVitalStatistics, AllClinicVisits allClinicVisits, PatientDetailsService patientDetailsService) {
        this.allVitalStatistics = allVitalStatistics;
        this.allClinicVisits = allClinicVisits;
        this.patientDetailsService = patientDetailsService;
    }

    public void createForm(String patientId, Model uiModel) {
        uiModel.addAttribute("vitalStatistics", new VitalStatistics(patientId));
    }

    public String create(VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        try {
            vitalStatistics.setCaptureDate(DateUtil.today());
            if (bindingResult.hasErrors()) {
                uiModel.addAttribute("vitalStatistics", vitalStatistics);
                return null;
            }
            if (isNotEmpty(vitalStatistics)) {
                allVitalStatistics.add(vitalStatistics, loggedInUserId(httpServletRequest));
            }
        } catch (RuntimeException e) {
            httpServletRequest.setAttribute("flash.flashErrorVitalStatistics", "Error occurred while creating Vital Statistics: " + e.getMessage());
        }
        return vitalStatistics.getId();
    }

    private boolean isNotEmpty(VitalStatistics vitalStatistics) {
        return vitalStatistics.getSystolicBp() != null || vitalStatistics.getDiastolicBp() != null
                || vitalStatistics.getHeightInCm() != null || vitalStatistics.getWeightInKg() != null
                || vitalStatistics.getPulse() != null || vitalStatistics.getTemperatureInFahrenheit() != null;
    }

    public void show(String vitalStatisticsId, Model uiModel) {
        VitalStatistics vitalStatisticsForPatient = null;
        if (vitalStatisticsId != null) {
            vitalStatisticsForPatient = allVitalStatistics.get(vitalStatisticsId);
        }
        uiModel.addAttribute("vitalStatistics", vitalStatisticsForPatient == null ? new VitalStatistics() : vitalStatisticsForPatient);
    }

    @RequestMapping(value = "/update", params = "form", method = RequestMethod.GET)
    public String updateForm(@RequestParam(value = "patientId", required = true) String patientDocId, @RequestParam(value = "clinicVisitId", required = true) String clinicVisitId, Model uiModel) {
        final ClinicVisit clinicVisit = allClinicVisits.get(patientDocId, clinicVisitId);
        if (clinicVisit.getVitalStatisticsId() == null) {
            uiModel.addAttribute("vitalStatisticsUIModel", VitalStatisticsUIModel.newDefault(clinicVisit));
        } else {
            uiModel.addAttribute("vitalStatisticsUIModel", VitalStatisticsUIModel.get(clinicVisit, allVitalStatistics.get(clinicVisit.getVitalStatisticsId())));
        }
        uiModel.addAttribute("patient", clinicVisit.getPatient());
        uiModel.addAttribute("_method", "put");
        return UPDATE_FORM;
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public String update(VitalStatisticsUIModel vitalStatisticsUIModel, HttpServletRequest httpServletRequest) {
        final VitalStatistics vitalStatistics = vitalStatisticsUIModel.getVitalStatistics();
        vitalStatistics.setCaptureDate(DateUtil.today());
        if (vitalStatistics.getId() == null || vitalStatistics.getId().isEmpty()) {
            if (isNotEmpty(vitalStatistics)) {
                allVitalStatistics.add(vitalStatistics, loggedInUserId(httpServletRequest));
                allClinicVisits.updateVitalStatistics(vitalStatisticsUIModel.getPatientId(), vitalStatisticsUIModel.getClinicVisitId(), vitalStatistics.getId());
                patientDetailsService.update(vitalStatisticsUIModel.getPatientId());
            }
        } else {
            final VitalStatistics savedVitalStatistics = allVitalStatistics.get(vitalStatistics.getId());
            vitalStatistics.setRevision(savedVitalStatistics.getRevision());
            if (isNotEmpty(vitalStatistics)) {
                allVitalStatistics.update(vitalStatistics, loggedInUserId(httpServletRequest));
                allClinicVisits.updateVitalStatistics(vitalStatisticsUIModel.getPatientId(), vitalStatisticsUIModel.getClinicVisitId(), vitalStatistics.getId());
                patientDetailsService.update(vitalStatisticsUIModel.getPatientId());
            } else {
                allVitalStatistics.remove(savedVitalStatistics, loggedInUserId(httpServletRequest));
                allClinicVisits.updateVitalStatistics(vitalStatisticsUIModel.getPatientId(), vitalStatisticsUIModel.getClinicVisitId(), null);
                patientDetailsService.update(vitalStatisticsUIModel.getPatientId());
            }
        }
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(vitalStatisticsUIModel.getClinicVisitId(), httpServletRequest) + "?patientId=" + vitalStatisticsUIModel.getPatientId();
    }
}
