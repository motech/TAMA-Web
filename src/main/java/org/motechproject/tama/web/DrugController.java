package org.motechproject.tama.web;

import org.motechproject.tama.domain.Drug;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "drugs", formBackingObject = Drug.class)
@RequestMapping("/drugs")
@Controller
public class DrugController {
}
