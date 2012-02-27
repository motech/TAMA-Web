package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.tama.ivr.service.CallLogService;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallLogPageNavigator;
import org.motechproject.tama.web.view.CallLogPreferencesFilter;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@RequestMapping("/callsummary")
@Controller
public class CallSummaryController {

    private CallLogService callLogService;

    private CallLogViewMapper callLogViewMapper;
    private final String LIST_VIEW = "callsummary/list";
    private final String CREATE_VIEW = "callsummary/create";
    private static final int HOURS_OF_THE_DAY = 23;
    private static final int MINUTES_AND_SECONDS_TO_END_OF_DAY = 59;
    private Properties properties;

    @Autowired
    public CallSummaryController(CallLogService callLogService, CallLogViewMapper callLogViewMapper, @Qualifier("ivrProperties") Properties properties) {
        this.callLogService = callLogService;
        this.callLogViewMapper = callLogViewMapper;
        this.properties = properties;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String list(@Valid CallLogPreferencesFilter filter, BindingResult bindingResult, HttpServletRequest request, Model uiModel) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("logPreferences", new CallLogPreferencesFilter());
            return CREATE_VIEW;
        }

        DateTime startDate = DateUtil.newDateTime(filter.getCallLogStartDate());
        DateTime endDate = DateUtil.newDateTime(filter.getCallLogEndDate()).plusHours(HOURS_OF_THE_DAY)
                .plusMinutes(MINUTES_AND_SECONDS_TO_END_OF_DAY).plusSeconds(MINUTES_AND_SECONDS_TO_END_OF_DAY);

        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        Integer totalNumberOfPages = getTotalNumberOfPages(user, startDate, endDate, filter.getCallType());
        Integer pageNumber = getValidPageNumber(filter.getPageNumber(), totalNumberOfPages);
        
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(getCallLogsForPage(user, startDate, endDate, filter.getCallType(), pageNumber));

        CallLogPageNavigator callLogPageNavigator = new CallLogPageNavigator(pageNumber, filter.getCallLogStartDate(), filter.getCallLogEndDate(), filter.getCallType(), totalNumberOfPages);

        uiModel.asMap().clear();
        uiModel.addAttribute("callSummary", callLogViews);
        uiModel.addAttribute("pageNavigator", callLogPageNavigator);
        return LIST_VIEW;
    }

    private Integer getTotalNumberOfPages(AuthenticatedUser user, DateTime startDate, DateTime endDate, String callType) {
        return calculateTotalNumberOfPages(getMaxNumberOfCallLogsPerPage(), getTotalNumberOfCallLogs(user, startDate, endDate, callType));
    }

    Integer getValidPageNumber(String pageNumber, Integer totalNumberOfPages) {
        try{
            Integer pageNo = Integer.parseInt(pageNumber);
            return (pageNo > totalNumberOfPages || pageNo < 1) ? 1 : pageNo;
        } catch(NumberFormatException e) {
            return 1;
        }
    }

    private List<CallLog> getCallLogsForPage(AuthenticatedUser user, DateTime startDate, DateTime endDate, String callType, Integer pageNumber) {
        final Integer maxNumberOfCallLogsPerPage = getMaxNumberOfCallLogsPerPage();
        final int startIndex = getStartIndex(pageNumber, maxNumberOfCallLogsPerPage);
        final CallLogSearch callLogSearch = new CallLogSearch(startDate, endDate, CallLog.CallLogType.valueOf(callType), user.isAdministrator(), user.getClinicId());
        callLogSearch.setPaginationParams(startIndex, maxNumberOfCallLogsPerPage);
        return callLogService.getLogsForDateRange(callLogSearch);
    }

    private Integer getTotalNumberOfCallLogs(AuthenticatedUser user, DateTime startDate, DateTime endDate, String callType) {
        final CallLogSearch callLogSearch = new CallLogSearch(startDate, endDate, CallLog.CallLogType.valueOf(callType), user.isAdministrator(), user.getClinicId());
        return callLogService.getTotalNumberOfLogs(callLogSearch);
    }

    private Integer getMaxNumberOfCallLogsPerPage() {
        return  Integer.parseInt(properties.getProperty(CallLogService.MAX_NUMBER_OF_CALL_LOGS_PER_PAGE, "20"));
    }

    private int getStartIndex(Integer pageNumber, Integer callLogsPerPage) {
        return pageNumber == 1 ? 0 : ((pageNumber -1) * callLogsPerPage);
    }

    Integer calculateTotalNumberOfPages(Integer maxCallLogsPerPage, Integer totalNumberOfCallLogs) {
        int numberOfPages = totalNumberOfCallLogs / maxCallLogsPerPage;
        int remainder = totalNumberOfCallLogs % maxCallLogsPerPage;
        if(remainder != 0) numberOfPages ++;
        return numberOfPages;
    }

    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String filterLogs(Model uiModel) {
        populateUIModel(uiModel);
        return CREATE_VIEW;
    }

    private void populateUIModel(Model uiModel) {
        CallLogPreferencesFilter callLogPreferencesFilter = new CallLogPreferencesFilter();
        callLogPreferencesFilter.setCallLogStartDate(DateUtil.today().toDate());
        callLogPreferencesFilter.setCallLogEndDate(DateUtil.today().toDate());
        callLogPreferencesFilter.setPageNumber("1");
        uiModel.addAttribute("callTypes", Arrays.asList(CallLog.CallLogType.values()));
        uiModel.addAttribute("logPreferences", callLogPreferencesFilter);
    }
}
