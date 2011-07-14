package org.motechproject.tama.web;

import org.motechproject.tama.domain.IVRLanguage;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.motechproject.tama.domain.IVRLanguage;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;
import org.springframework.web.util.UriUtils;

@RooWebScaffold(path = "ivrlanguages", formBackingObject = IVRLanguage.class)
@RequestMapping("/ivrlanguages")
@Controller
public class IVRLanguageController {
    @RequestMapping(method = RequestMethod.POST)
    public String create(@Valid IVRLanguage IVRLanguage, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("IVRLanguage", IVRLanguage);
            return "ivrlanguages/create";
        }
        uiModel.asMap().clear();
        IVRLanguage.persist();
        return "redirect:/ivrlanguages/" + encodeUrlPathSegment(IVRLanguage.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
        uiModel.addAttribute("IVRLanguage", new IVRLanguage());
        return "ivrlanguages/create";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("ivrlanguage", IVRLanguage.findIVRLanguage(id));
        uiModel.addAttribute("itemId", id);
        return "ivrlanguages/show";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("ivrlanguages", IVRLanguage.findAllIVRLanguages());
        return "ivrlanguages/list";
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid IVRLanguage IVRLanguage, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("IVRLanguage", IVRLanguage);
            return "ivrlanguages/update";
        }
        uiModel.asMap().clear();
        IVRLanguage.merge();
        return "redirect:/ivrlanguages/" + encodeUrlPathSegment(IVRLanguage.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("IVRLanguage", IVRLanguage.findIVRLanguage(id));
        return "ivrlanguages/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        IVRLanguage.findIVRLanguage(id).remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/ivrlanguages";
    }

    @ModelAttribute("ivrlanguages")
    public Collection<IVRLanguage> populateIVRLanguages() {
        return IVRLanguage.findAllIVRLanguages();
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
