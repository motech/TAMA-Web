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
import org.motechproject.tama.domain.MealAdviceType;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect MealAdviceTypeController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String MealAdviceTypeController.create(@Valid MealAdviceType mealAdviceType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("mealAdviceType", mealAdviceType);
            return "mealadvicetypes/create";
        }
        uiModel.asMap().clear();
        mealAdviceType.persist();
        return "redirect:/mealadvicetypes/" + encodeUrlPathSegment(mealAdviceType.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String MealAdviceTypeController.createForm(Model uiModel) {
        uiModel.addAttribute("mealAdviceType", new MealAdviceType());
        return "mealadvicetypes/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String MealAdviceTypeController.show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("mealadvicetype", MealAdviceType.findMealAdviceType(id));
        uiModel.addAttribute("itemId", id);
        return "mealadvicetypes/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String MealAdviceTypeController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("mealadvicetypes", MealAdviceType.findMealAdviceTypeEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) MealAdviceType.countMealAdviceTypes() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("mealadvicetypes", MealAdviceType.findAllMealAdviceTypes());
        }
        return "mealadvicetypes/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String MealAdviceTypeController.update(@Valid MealAdviceType mealAdviceType, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("mealAdviceType", mealAdviceType);
            return "mealadvicetypes/update";
        }
        uiModel.asMap().clear();
        mealAdviceType.merge();
        return "redirect:/mealadvicetypes/" + encodeUrlPathSegment(mealAdviceType.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String MealAdviceTypeController.updateForm(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("mealAdviceType", MealAdviceType.findMealAdviceType(id));
        return "mealadvicetypes/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String MealAdviceTypeController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        MealAdviceType.findMealAdviceType(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/mealadvicetypes";
    }
    
    @ModelAttribute("mealadvicetypes")
    public Collection<MealAdviceType> MealAdviceTypeController.populateMealAdviceTypes() {
        return MealAdviceType.findAllMealAdviceTypes();
    }
    
    String MealAdviceTypeController.encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
