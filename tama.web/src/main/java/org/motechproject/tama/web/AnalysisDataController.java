package org.motechproject.tama.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("analysisData")
public class AnalysisDataController extends BaseController {

    @RequestMapping(method = RequestMethod.GET)
    public String show() {
        return "redirect:/callsummary?form";
    }
}
