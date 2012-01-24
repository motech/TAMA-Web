package org.motechproject.tama.web;

import org.motechproject.tama.patient.domain.VitalStatistics;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
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

@RequestMapping("/vital_statistics")
@Controller
public class VitalStatisticsController extends BaseController {

    private static final String UPDATE_FORM = "vital_statistics/update";
    public static final String REDIRECT_AND_SHOW_CLINIC_VISIT = "redirect:/clinicvisits/";

    private final AllVitalStatistics allVitalStatistics;

    @Autowired
    public VitalStatisticsController(AllVitalStatistics allVitalStatistics) {
        this.allVitalStatistics = allVitalStatistics;
    }

    public void createForm(String patientId, Model uiModel) {
        uiModel.addAttribute("vitalStatistics", new VitalStatistics(patientId));
    }

    public void create(VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel) {
        vitalStatistics.setCaptureDate(DateUtil.today());
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("vitalStatistics", vitalStatistics);
            return;
        }
        allVitalStatistics.add(vitalStatistics);
    }

    public void show(String patientId, Model uiModel) {
        VitalStatistics vitalStatisticsForPatient = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        uiModel.addAttribute("vitalStatistics", vitalStatisticsForPatient == null ? null : vitalStatisticsForPatient);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String patientId, Model uiModel) {
        VitalStatistics vitalStatisticsOfPatient = allVitalStatistics.findLatestVitalStatisticByPatientId(patientId);
        uiModel.addAttribute("vitalStatistics", vitalStatisticsOfPatient);
        uiModel.addAttribute("_method", "put");
        return UPDATE_FORM;
    }

    //TODO: The upsert logic can be moved to the repository layer
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        vitalStatistics.setCaptureDate(DateUtil.today());
        uiModel.addAttribute("vitalStatistics", vitalStatistics);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("_method", "put");
            return UPDATE_FORM;
        }
        VitalStatistics dbVitalStatistics = allVitalStatistics.findLatestVitalStatisticByPatientId(vitalStatistics.getPatientId());
        if(dbVitalStatistics.getCaptureDate().equals(vitalStatistics.getCaptureDate())){
            vitalStatistics.setRevision(dbVitalStatistics.getRevision());
            allVitalStatistics.update(vitalStatistics);
        }
        else{
            VitalStatistics newVitalStatistics = new VitalStatistics(vitalStatistics);
            allVitalStatistics.add(newVitalStatistics);
        }
        return REDIRECT_AND_SHOW_CLINIC_VISIT + encodeUrlPathSegment(vitalStatistics.getPatientId(), httpServletRequest);
    }
}
