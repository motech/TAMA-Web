package org.motechproject.tama.web;

import org.apache.log4j.Logger;
import org.motechproject.tama.TamaException;
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
    Logger logger = Logger.getLogger(this.getClass());
    private Actions actions;

    @Autowired
    public IVRController(Actions actions) {
        this.actions = actions;
    }

    @RequestMapping(value = "reply", method = RequestMethod.GET)
    @ResponseBody
    public String reply(@ModelAttribute IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        try {
            BaseIncomingAction action = actions.findFor(ivrRequest.callEvent());
            final String handle = action.handle(ivrRequest, request, response);
            logger.info(String.format(" XML returned: %s", handle));
            return handle;
        } catch (Exception e) {
            logger.error("Failed to handled incoming request", e);
            throw new TamaException("Failed to handled incoming request", e);
        }
    }
}
