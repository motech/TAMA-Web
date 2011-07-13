package org.motechproject.tama.web;

import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.action.IVRAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/ivr")
public class IVRController {

    public String reply(@ModelAttribute IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        IVRAction action = ivrRequest.getAction();
        return action.handle(ivrRequest, request, response);
    }

}
