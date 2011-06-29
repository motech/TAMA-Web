package org.motechproject.tama.web;

import org.motechproject.tama.domain.IVRLanguage;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "ivrlanguages", formBackingObject = IVRLanguage.class)
@RequestMapping("/ivrlanguages")
@Controller
public class IVRLanguageController {
}
