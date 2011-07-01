// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.web;

import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.String;
import java.lang.String;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect RegimenController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String RegimenController.create(@Valid Regimen regimen, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("regimen", regimen);
            return "regimenCompositions/create";
        }
        uiModel.asMap().clear();
        regimen.persist();
        return "redirect:/regimenCompositions/" + encodeUrlPathSegment(regimen.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String RegimenController.createForm(Model uiModel) {
        uiModel.addAttribute("regimen", new Regimen());
        return "regimenCompositions/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String RegimenController.show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("regimen", Regimen.findRegimen(id));
        uiModel.addAttribute("itemId", id);
        return "regimenCompositions/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String RegimenController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("regimenCompositions", Regimen.findRegimenEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Regimen.countRegimens() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("regimenCompositions", Regimen.findAllRegimens());
        }
        return "regimenCompositions/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String RegimenController.update(@Valid Regimen regimen, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("regimen", regimen);
            return "regimenCompositions/update";
        }
        uiModel.asMap().clear();
        regimen.merge();
        return "redirect:/regimenCompositions/" + encodeUrlPathSegment(regimen.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String RegimenController.updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("regimen", Regimen.findRegimen(id));
        return "regimenCompositions/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String RegimenController.delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Regimen.findRegimen(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/regimenCompositions";
    }
    
    @ModelAttribute("regimenCompositions")
    public Collection<Regimen> RegimenController.populateRegimens() {
        return Regimen.findAllRegimens();
    }
    
    @ModelAttribute("regimencompositions")
    public Collection<RegimenComposition> RegimenController.populateRegimenCompositions() {
        return RegimenComposition.findAllRegimenCompositions();
    }
    
    String RegimenController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
