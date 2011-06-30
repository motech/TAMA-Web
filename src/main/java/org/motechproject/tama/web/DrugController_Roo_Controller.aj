// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.web;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.motechproject.tama.domain.Brand;
import org.motechproject.tama.domain.Drug;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect DrugController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String DrugController.create(@Valid Drug drug, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("drug", drug);
            return "drugs/create";
        }
        uiModel.asMap().clear();
        drug.persist();
        return "redirect:/drugs/" + encodeUrlPathSegment(drug.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String DrugController.createForm(Model uiModel) {
        uiModel.addAttribute("drug", new Drug());
        return "drugs/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String DrugController.show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("drug", Drug.findDrug(id));
        uiModel.addAttribute("itemId", id);
        return "drugs/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String DrugController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("drugs", Drug.findDrugEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Drug.countDrugs() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("drugs", Drug.findAllDrugs());
        }
        return "drugs/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String DrugController.update(@Valid Drug drug, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("drug", drug);
            return "drugs/update";
        }
        uiModel.asMap().clear();
        drug.merge();
        return "redirect:/drugs/" + encodeUrlPathSegment(drug.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String DrugController.updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("drug", Drug.findDrug(id));
        return "drugs/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String DrugController.delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Drug.findDrug(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/drugs";
    }
    
    @ModelAttribute("brands")
    public Collection<Brand> DrugController.populateBrands() {
        return Brand.findAllBrands();
    }
    
    @ModelAttribute("drugs")
    public Collection<Drug> DrugController.populateDrugs() {
        return Drug.findAllDrugs();
    }
    
    String DrugController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
