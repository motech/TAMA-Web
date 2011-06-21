package org.motechproject.tama.web;

import org.motechproject.tama.Title;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "titles", formBackingObject = Title.class)
@RequestMapping("/titles")
@Controller
public class TitleController {
}
