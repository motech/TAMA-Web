package org.motechproject.tama.web;

import org.motechproject.tama.domain.VitalStatistics;
import org.motechproject.tama.repository.AllVitalStatistics;
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

@RooWebScaffold(path = "vital_statistics", formBackingObject = VitalStatistics.class)
@RequestMapping("/vital_statistics")
@Controller
public class VitalStatisticsController extends BaseController {

    private static final String FORM = "vital_statistics/form";
    public static final String REDIRECT_AND_SHOW_VITAL_STATISTICS = "redirect:/vital_statistics/";
    public static final String SHOW_VIEW = "vital_statistics/show";

    private final AllVitalStatistics allVitalStatistics;

    @Autowired
    public VitalStatisticsController(AllVitalStatistics allVitalStatistics) {
        this.allVitalStatistics = allVitalStatistics;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "patientId", required = true) String patientId, Model uiModel, HttpServletRequest httpServletRequest) {
        VitalStatistics vitalStatisticsOfPatient = allVitalStatistics.findByPatientId(patientId);
        uiModel.addAttribute("vitalStatistics", vitalStatisticsOfPatient == null ? new VitalStatistics(patientId) : vitalStatisticsOfPatient);
        return vitalStatisticsOfPatient == null ? FORM : REDIRECT_AND_SHOW_VITAL_STATISTICS + encodeUrlPathSegment(patientId, httpServletRequest);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("vitalStatistics", vitalStatistics);
            return FORM;
        }
        allVitalStatistics.add(vitalStatistics);
        return REDIRECT_AND_SHOW_VITAL_STATISTICS + encodeUrlPathSegment(vitalStatistics.getPatientId(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String patientId, Model uiModel) {
        VitalStatistics vitalStatisticsForPatient = allVitalStatistics.findByPatientId(patientId);
        uiModel.addAttribute("vitalStatistics", vitalStatisticsForPatient == null ? null : vitalStatisticsForPatient);
        return SHOW_VIEW;
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String patientId, Model uiModel) {
        VitalStatistics vitalStatisticsOfPatient = allVitalStatistics.findByPatientId(patientId);
        uiModel.addAttribute("vitalStatistics", vitalStatisticsOfPatient);
        uiModel.addAttribute("_method", "put");
        return FORM;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid VitalStatistics vitalStatistics, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        uiModel.addAttribute("vitalStatistics", vitalStatistics);
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("_method", "put");
            return FORM;
        }
        allVitalStatistics.update(vitalStatistics);
        return REDIRECT_AND_SHOW_VITAL_STATISTICS + encodeUrlPathSegment(vitalStatistics.getPatientId(), httpServletRequest);
    }
}
