package org.motechproject.tama.web;

import org.motechproject.tama.domain.DosageType;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "dosagetypes", formBackingObject = DosageType.class)
@RequestMapping("/dosagetypes")
@Controller
public class DosageTypeController {
}
