package org.motechproject.tama.web;

import org.motechproject.tama.ivr.controller.TAMACallFlowController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DialController {

    @Autowired
    protected DialController() {
    }

    @RequestMapping(TAMACallFlowController.DIAL_URL)
    public void dial() {
        ;
    }

}