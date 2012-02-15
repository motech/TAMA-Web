package org.motechproject.tama.web;

import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.clinicvisits.service.ClinicVisitService;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.web.model.VitalStatisticsUIModel;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/vital_statistics")
@Controller
public class VitalStatisticsController extends BaseController {

    private static final String UPDATE_FORM = "vital_statistics/update";
    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";

    private final AllVitalStatistics allVitalStatistics;
    private AllClinicVisits allClinicVisits;
    private ClinicVisitService clinicVisitService;

    @Autowired
    public VitalStatisticsController(AllVitalStatistics allVitalStatistics, AllClinicVisits allClinicVisits, ClinicVisitService clinicVisitService) {
        this.allVitalStatistics = allVitalStatistics;
        this.allClinicVisits = allClinicVisits;
        this.clinicVisitService = clinicVisitService;
    }

    public void createForm(String patientId, Model uiModel) {
        uiModel.addAttribute("vitalStatistics", new VitalStatistics(patientId));
    }

    public String create(VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel) {
        vitalStatistics.setCaptureDate(DateUtil.today());
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("vitalStatistics", vitalStatistics);
            return null;
        }
        if (isNotEmpty(vitalStatistics)) {
            allVitalStatistics.add(vitalStatistics);
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
    public String updateForm(String clinicVisitId, Model uiModel) {
        final ClinicVisit clinicVisit = allClinicVisits.get(clinicVisitId);
        if (clinicVisit.getVitalStatisticsId() == null) {
            uiModel.addAttribute("vitalStatisticsUIModel", VitalStatisticsUIModel.newDefault(clinicVisit));
        } else {
            uiModel.addAttribute("vitalStatisticsUIModel", VitalStatisticsUIModel.get(clinicVisit, allVitalStatistics.get(clinicVisit.getVitalStatisticsId())));
        }
        uiModel.addAttribute("_method", "put");
        return UPDATE_FORM;
    }

    //TODO: The upsert logic can be moved to the repository layer
    @RequestMapping(method = RequestMethod.PUT)
    public String update(VitalStatisticsUIModel vitalStatisticsUIModel, HttpServletRequest httpServletRequest) {
        final VitalStatistics vitalStatistics = vitalStatisticsUIModel.getVitalStatistics();
        vitalStatistics.setCaptureDate(DateUtil.today());
        if (vitalStatistics.getId() == null || vitalStatistics.getId().isEmpty()) {
            if (isNotEmpty(vitalStatistics)) {
                allVitalStatistics.add(vitalStatistics);
                clinicVisitService.updateVitalStatistics(vitalStatisticsUIModel.getClinicVisitId(), vitalStatistics.getId());
            }
        } else {
            final VitalStatistics savedVitalStatistics = allVitalStatistics.get(vitalStatistics.getId());
            vitalStatistics.setRevision(savedVitalStatistics.getRevision());
            if (isNotEmpty(vitalStatistics)) {
                allVitalStatistics.update(vitalStatistics);
                clinicVisitService.updateVitalStatistics(vitalStatisticsUIModel.getClinicVisitId(), vitalStatistics.getId());
            } else {
                allVitalStatistics.remove(savedVitalStatistics);
                clinicVisitService.updateVitalStatistics(vitalStatisticsUIModel.getClinicVisitId(), null);
            }
        }
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(vitalStatisticsUIModel.getClinicVisitId(), httpServletRequest);
    }
}
