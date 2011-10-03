package org.motechproject.tama.web;


import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.view.CallLogView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequestMapping("/callsummary")
@Controller
public class CallSummaryController {

    private CallLogService callLogService;

    private CallLogViewMapper callLogViewMapper;

    @Autowired
    public CallSummaryController(CallLogService callLogService, CallLogViewMapper callLogViewMapper) {
        this.callLogService = callLogService;
        this.callLogViewMapper = callLogViewMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(HttpServletRequest request, Model uiModel) {
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        List<CallLogView> callLogViews;
        if (user.isAdministrator()) {
            callLogViews = callLogViewMapper.toCallLogView(callLogService.getAll());
        } else {
            callLogViews = callLogViewMapper.toCallLogView(callLogService.getByClinicId(user.getClinicId()));
        }
        uiModel.addAttribute("callsummary", callLogViews);
        return "callsummary/list";
    }
}
