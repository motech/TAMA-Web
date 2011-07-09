package org.motechproject.tama.web;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.repository.Cities;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Collection;

@RooWebScaffold(path = "clinics", formBackingObject = Clinic.class)
@RequestMapping("/clinics")
@Controller
public class ClinicController {
    @Autowired
    private Clinics clinics;

    @Autowired
    private Cities cities;
    
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Clinic clinic, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinic", clinic);
            return "clinics/create";
        }
        uiModel.asMap().clear();
        clinics.add(clinic);
        return "redirect:/clinics/" + encodeUrlPathSegment(clinic.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("clinic", Clinic.newClinic());
        return "clinics/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinic", clinics.get(id));
        uiModel.addAttribute("itemId", id);
        return "clinics/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("clinics", clinics.findClinicEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) clinics.getAll().size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("clinics", clinics.getAll());
        }
        return "clinics/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Clinic clinic, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinic", clinic);
            return "clinics/update";
        }
        uiModel.asMap().clear();
        clinic.setRevision(clinics.get(clinic.getId()).getRevision());
        clinics.update(clinic);
        return "redirect:/clinics/" + encodeUrlPathSegment(clinic.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinic", clinics.get(id));
        return "clinics/update";
    }


    @ModelAttribute("citys")
    public Collection<City> populateCitys() {
        return cities.getAllCities();
    }

    @ModelAttribute("clinics")
    public Collection<Clinic> populateClinics() {
        return clinics.getAll();
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }

    
}
