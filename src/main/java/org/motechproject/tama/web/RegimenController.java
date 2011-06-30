package org.motechproject.tama.web;

import org.motechproject.tama.domain.Regimen;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "regimens", formBackingObject = Regimen.class)
@RequestMapping("/regimens")
@Controller
public class RegimenController {
}
