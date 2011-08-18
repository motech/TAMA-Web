package org.motechproject.tama.web;

import com.ozonetel.kookoo.Response;
import org.apache.log4j.Logger;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.Actions;
import org.motechproject.tama.ivr.action.BaseIncomingAction;
import org.motechproject.tama.ivr.action.event.HangupEventAction;
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
    Logger log = Logger.getLogger(this.getClass());
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
        try {
            log.info(String.format("Got IVR Request: %s, SID: %s", ivrRequest.getCid(), ivrRequest.getSid()));
            BaseIncomingAction action = actions.findFor(ivrRequest.callEvent());
            log.info("IVR Action: " + action.getClass().getName());
            final String responseXml = action.handle(ivrRequest, request, response);
            log.info(String.format("Responding to IVR Request: %s, SID: %s, Response: %s", ivrRequest.getCid(), ivrRequest.getSid(), responseXml));
            return responseXml;
        } catch (Exception e) {
            log.error("Error in TAMA", e);
            Response hangupResponse = new Response();
            hangupResponse.addHangup();
            return hangupResponse.getXML();
        }
    }
}
