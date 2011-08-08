package org.motechproject.tama.web;

import org.ektorp.UpdateConflictException;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Clinician;
import org.motechproject.tama.repository.Clinicians;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.web.view.CliniciansView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RooWebScaffold(path = "clinicians", formBackingObject = Clinician.class)
@RequestMapping("/clinicians")
@Controller
public class ClinicianController extends BaseController {
    public static final String CREATE_VIEW = "clinicians/create";
    public static final String SHOW_VIEW = "clinicians/show";
    public static final String LIST_VIEW = "clinicians/list";
    public static final String UPDATE_VIEW = "clinicians/update";
    public static final String REDIRECT_TO_SHOW_VIEW = "redirect:/clinicians/";
    public static final String USERNAME_ALREADY_IN_USE = "sorry, username already in use.";
    private Clinicians clinicians;
    private Clinics clinics;

    @Autowired
    public ClinicianController(Clinicians clinicians, Clinics clinics) {
        this.clinicians = clinicians;
        this.clinics = clinics;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Clinician clinician, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinician", clinician);
            return CREATE_VIEW;
        }
        try {
            clinicians.add(clinician);
            uiModel.asMap().clear();
        } catch (UpdateConflictException e) {
            bindingResult.addError(new FieldError("Clinician", "username", clinician.getUsername(), false,
                    new String[]{"clinician_username_not_unique"}, new Object[]{}, USERNAME_ALREADY_IN_USE));
            uiModel.addAttribute("clinician", clinician);
            return CREATE_VIEW;
        }
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(clinician.getId(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("clinician", new Clinician());
        return CREATE_VIEW;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinician", clinicians.get(id));
        uiModel.addAttribute("itemId", id);
        return SHOW_VIEW;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("clinicians", new CliniciansView(clinicians).getAll());
        return LIST_VIEW;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Clinician clinician, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("clinician", clinician);
            return UPDATE_VIEW;
        }
        uiModel.asMap().clear();
        clinicians.update(clinician);
        return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(clinician.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinician", clinicians.get(id));
        return UPDATE_VIEW;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        clinicians.remove(clinicians.get(id));
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return REDIRECT_TO_SHOW_VIEW;
    }

    @ModelAttribute("clinicians")
    public Collection<Clinician> populateClinicians() {
        return clinicians.getAll();
    }

    @ModelAttribute("clinics")
    public Collection<Clinic> populateClinics() {
        List<Clinic> allClinics = clinics.getAll();
        Collections.sort(allClinics);
        return allClinics;
    }

    @ModelAttribute("roles")
    public Collection<Clinician.Role> populateRoles() {
        return new ArrayList<Clinician.Role>() {{
            add(Clinician.Role.Doctor);
            add(Clinician.Role.StudyNurse);
        }};
    }
}
