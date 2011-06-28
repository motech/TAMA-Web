package org.motechproject.tama.web;

import org.motechproject.tama.domain.Gender;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "genders", formBackingObject = Gender.class)
@RequestMapping("/genders")
@Controller
public class GenderController {
}
