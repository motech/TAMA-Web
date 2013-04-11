package org.motechproject.tama.web;

import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.refdata.domain.City;
import org.motechproject.tama.refdata.objectcache.AllCitiesCache;
import org.motechproject.tama.refdata.repository.AllCities;
import org.motechproject.tama.web.view.ClinicsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequestMapping("/clinics")
@Controller
public class ClinicController extends BaseController {
    private AllClinics allClinics;
    private AllCitiesCache allCities;

    @Autowired
    public ClinicController(AllClinics allClinics, AllCitiesCache allCities) {
        this.allClinics = allClinics;
        this.allCities = allCities;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Clinic clinic, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinic", clinic);
            return "clinics/clinicForm";
        }
        uiModel.asMap().clear();
        allClinics.add(clinic, loggedInUserId(httpServletRequest));
        return "redirect:/clinics/" + encodeUrlPathSegment(clinic.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("clinic", Clinic.newClinic());
        return "clinics/clinicForm";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinic", allClinics.get(id));
        uiModel.addAttribute("itemId", id);
        return "clinics/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("clinics", new ClinicsView(allClinics).getAll());
        return "clinics/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Clinic clinic, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinic", clinic);
            return "clinics/clinicForm";
        }
        uiModel.asMap().clear();
        clinic.setRevision(allClinics.get(clinic.getId()).getRevision());
        allClinics.update(clinic, loggedInUserId(httpServletRequest));
        return "redirect:/clinics/" + encodeUrlPathSegment(clinic.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinic", allClinics.get(id));
        uiModel.addAttribute("mode", "update");
        return "clinics/clinicForm";
    }

    @ModelAttribute("cities")
    public Collection<City> populateCities() {
        List<City> allCities = this.allCities.getAll();
        arrangeCities(allCities);
        return allCities;
    }

    private void arrangeCities(List<City> allCities) {
        Collections.sort(allCities);
        Collections.swap(allCities,allCities.size()-1,allCities.size()-2);
    }

    @ModelAttribute("clinics")
    public Collection<Clinic> populateClinics() {
        return allClinics.getAll();
    }
}
