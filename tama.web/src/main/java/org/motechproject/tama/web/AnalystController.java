package org.motechproject.tama.web;

import org.motechproject.tama.refdata.repository.AllAnalysts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/analysts")
@Controller
public class AnalystController {

    public static final String ANALYSTS = "analysts";

    private AllAnalysts allAnalysts;

    @Autowired
    public AnalystController(AllAnalysts allAnalysts) {
        this.allAnalysts = allAnalysts;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model uiModel) {
        uiModel.addAttribute(ANALYSTS, allAnalysts.getAll());
        return "analysts/list";
    }
}
