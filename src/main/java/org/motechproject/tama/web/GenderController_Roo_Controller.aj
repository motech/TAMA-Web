// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.web;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.motechproject.tama.domain.Gender;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect GenderController_Roo_Controller {

    @RequestMapping(method = RequestMethod.POST)
    public String GenderController.create(@Valid Gender gender, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("gender", gender);
            return "genders/create";
        }
        uiModel.asMap().clear();
        gender.persist();
        return "redirect:/genders/" + encodeUrlPathSegment(gender.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String GenderController.createForm(Model uiModel) {
        uiModel.addAttribute("gender", new Gender());
        return "genders/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String GenderController.show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("gender", Gender.findGender(id));
        uiModel.addAttribute("itemId", id);
        return "genders/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String GenderController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("genders", Gender.findAllGenders());
        return "genders/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String GenderController.update(@Valid Gender gender, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("gender", gender);
            return "genders/update";
        }
        uiModel.asMap().clear();
        gender.merge();
        return "redirect:/genders/" + encodeUrlPathSegment(gender.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String GenderController.updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("gender", Gender.findGender(id));
        return "genders/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String GenderController.delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Gender.findGender(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/genders";
    }

    @ModelAttribute("genders")
    public Collection<Gender> GenderController.populateGenders() {
        return Gender.findAllGenders();
    }

    String GenderController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }

}
