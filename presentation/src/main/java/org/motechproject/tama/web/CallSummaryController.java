package org.motechproject.tama.web;

import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.view.CallLogPreferencesFilter;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RooWebScaffold(path = "callsummary", formBackingObject = CallLogPreferencesFilter.class)
@RequestMapping("/callsummary")
@Controller
public class CallSummaryController {

    private CallLogService callLogService;

    private CallLogViewMapper callLogViewMapper;
    private CallLogPreferencesFilter callLogPreferencesFilter;
    private final String LIST_VIEW = "callsummary/list";
    private final String CREATE_VIEW = "callsummary/create";
    private static final int HOURS_OF_THE_DAY = 23;
    private static final int MINUTES_AND_SECONDS_TO_END_OF_DAY = 59;

    @Autowired
    public CallSummaryController(CallLogService callLogService, CallLogViewMapper callLogViewMapper) {
        this.callLogService = callLogService;
        this.callLogViewMapper = callLogViewMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@Valid CallLogPreferencesFilter callLogPreferencesFilter, BindingResult bindingResult, HttpServletRequest request, Model uiModel) {
        if (bindingResult.hasErrors()) {
            callLogPreferencesFilter = new CallLogPreferencesFilter();
            uiModel.addAttribute("logPreferences", callLogPreferencesFilter);
            return CREATE_VIEW;
        }
        uiModel.asMap().clear();
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        List<CallLogView> callLogViews;
        if (user.isAdministrator()) {
            callLogViews = callLogViewMapper.toCallLogView(callLogService.getLogsBetweenDates(DateUtil.newDateTime(callLogPreferencesFilter.getCallLogStartDate()),
                           DateUtil.newDateTime(callLogPreferencesFilter.getCallLogEndDate()).plusHours(HOURS_OF_THE_DAY).plusMinutes(MINUTES_AND_SECONDS_TO_END_OF_DAY).
                           plusSeconds(MINUTES_AND_SECONDS_TO_END_OF_DAY)));
        } else {
            callLogViews = callLogViewMapper.toCallLogView(callLogService.getByClinicId(DateUtil.newDateTime(callLogPreferencesFilter.getCallLogStartDate()),
                           DateUtil.newDateTime(callLogPreferencesFilter.getCallLogEndDate()).plusHours(HOURS_OF_THE_DAY).plusMinutes(MINUTES_AND_SECONDS_TO_END_OF_DAY).
                           plusSeconds(MINUTES_AND_SECONDS_TO_END_OF_DAY), user.getClinicId()));
        }
        uiModel.addAttribute("callsummary", callLogViews);
        return LIST_VIEW;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String filterLogs(Model uiModel) {
        populateUIModel(uiModel);
        return CREATE_VIEW;
    }

    private void populateUIModel(Model uiModel) {
        callLogPreferencesFilter = new CallLogPreferencesFilter();
        callLogPreferencesFilter.setCallLogStartDate(DateUtil.today().toDate());
        callLogPreferencesFilter.setCallLogEndDate(DateUtil.today().toDate());
        uiModel.addAttribute("logPreferences", callLogPreferencesFilter);
    }
}
