package org.motechproject.tama.web;

import org.motechproject.tama.domain.City;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
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

@RooWebScaffold(path = "clinicians", formBackingObject = Clinician.class)
@RequestMapping("/clinicians")
@Controller
public class ClinicianController {

    @Autowired
    private Clinicians clinicians;

    @Autowired
    private Clinics clinics;

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Clinician clinician, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinician", clinician);
            return "clinicians/create";
        }
        uiModel.asMap().clear();
        clinicians.add(clinician);
        return "redirect:/clinicians/" + encodeUrlPathSegment(clinician.getId(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("clinician", new Clinician());
        return "clinicians/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinician", clinicians.get(id));
        uiModel.addAttribute("itemId", id);
        return "clinicians/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("clinicians", clinicians.getAll());
            float nrOfPages = (float) clinicians.getAll().size() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("clinicians", clinicians.getAll());
        }
        return "clinicians/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Clinician clinician, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinician", clinician);
            return "clinicians/update";
        }
        uiModel.asMap().clear();
        clinician.setRevision(clinicians.get(clinician.getId()).getRevision());
        clinicians.update(clinician);
        return "redirect:/clinicians/" + encodeUrlPathSegment(clinician.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinician", clinicians.get(id));
        return "clinicians/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        clinicians.remove(clinicians.get(id));
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/clinicians";
    }

    @ModelAttribute("clinicians")
    public Collection<Clinician> populateClinicians() {
        return clinicians.getAll();
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
