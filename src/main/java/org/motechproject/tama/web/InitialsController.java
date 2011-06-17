package org.motechproject.tama.web;

import org.motechproject.tama.Initials;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "initialses", formBackingObject = Initials.class)
@RequestMapping("/initialses")
@Controller
public class InitialsController {
}
