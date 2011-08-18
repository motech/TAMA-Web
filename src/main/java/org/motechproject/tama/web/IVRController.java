package org.motechproject.tama.web;

import org.apache.log4j.Logger;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.Actions;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ivr")
public class IVRController {

    Logger LOG = Logger.getLogger(this.getClass());
    @Autowired
    private Actions actions;

    public IVRController() {
    }

    public IVRController(Actions actions) {
        this.actions = actions;
    }

    @RequestMapping(value = "reply", method = RequestMethod.GET)
    @ResponseBody
    public String reply(@ModelAttribute IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        BaseIncomingAction action = actions.findFor(ivrRequest.callEvent());
        return action.handle(ivrRequest, request, response);
    }

}
