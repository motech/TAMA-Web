// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.motechproject.tama.domain.DosageType;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect DosageTypeController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String DosageTypeController.create(@Valid DosageType dosageType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("dosageType", dosageType);
            return "dosagetypes/create";
        }
        uiModel.asMap().clear();
        dosageType.persist();
        return "redirect:/dosagetypes/" + encodeUrlPathSegment(dosageType.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String DosageTypeController.createForm(Model uiModel) {
        uiModel.addAttribute("dosageType", new DosageType());
        return "dosagetypes/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String DosageTypeController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("dosagetype", DosageType.findDosageType(id));
        uiModel.addAttribute("itemId", id);
        return "dosagetypes/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String DosageTypeController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("dosagetypes", DosageType.findDosageTypeEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) DosageType.countDosageTypes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("dosagetypes", DosageType.findAllDosageTypes());
        }
        return "dosagetypes/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String DosageTypeController.update(@Valid DosageType dosageType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("dosageType", dosageType);
            return "dosagetypes/update";
        }
        uiModel.asMap().clear();
        dosageType.merge();
        return "redirect:/dosagetypes/" + encodeUrlPathSegment(dosageType.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String DosageTypeController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("dosageType", DosageType.findDosageType(id));
        return "dosagetypes/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String DosageTypeController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        DosageType.findDosageType(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/dosagetypes";
    }
    
    @ModelAttribute("dosagetypes")
    public Collection<DosageType> DosageTypeController.populateDosageTypes() {
        return DosageType.findAllDosageTypes();
    }
    
    String DosageTypeController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
