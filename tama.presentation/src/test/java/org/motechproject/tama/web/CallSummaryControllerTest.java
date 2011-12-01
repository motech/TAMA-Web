package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tamadomain.domain.CallLog;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tamacallflow.service.CallLogService;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.view.CallLogPreferencesFilter;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallSummaryControllerTest {

    private CallSummaryController callSummaryController;

    @Mock
    private Model uiModel;

    @Mock
    private CallLogService callLogService;

    @Mock
    private CallLogViewMapper callLogViewMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private AuthenticatedUser user;

    @Mock
    BindingResult bindingResult;


    @Before
    public void setUp() {
        initMocks(this);
        callSummaryController = new CallSummaryController(callLogService, callLogViewMapper);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldShowAllCallLogsBetweenEnteredDates() {
        CallLog callLog = setUpCallLogs();
        List<CallLog> callLogs = Arrays.asList(callLog);
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", callLog, "clinic", new ArrayList<String>()));
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter();

        when(user.isAdministrator()).thenReturn(true);
        when(callLogService.getLogsBetweenDates(any(DateTime.class), any(DateTime.class))).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("callsummary", callLogViews);
        assertEquals("callsummary/list", view);
    }

    @Test
    public void shouldShowOnlyLogsOfPatientsBelongingToTheClinic() {
        CallLog callLog = setUpCallLogs();
        List<CallLog> callLogs = Arrays.asList(callLog);
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", callLog, "clinic", new ArrayList<String>()));
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter();

        when(user.isAdministrator()).thenReturn(false);
        when(user.getClinicId()).thenReturn("clinicId");
        when(callLogService.getByClinicId(DateUtil.newDateTime(callLogPreferencesFilter.getCallLogStartDate()), DateUtil.newDateTime(callLogPreferencesFilter.getCallLogEndDate()).
                plusHours(23).plusMinutes(59).plusSeconds(59), "clinicId")).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("callsummary", callLogViews);
        assertEquals("callsummary/list", view);
    }

    private CallLogPreferencesFilter setUpCallLogPreferencesFilter() {
        CallLogPreferencesFilter callLogPreferencesFilter = new CallLogPreferencesFilter();
        callLogPreferencesFilter.setCallLogStartDate(DateUtil.today().toDate());
        callLogPreferencesFilter.setCallLogEndDate(DateUtil.tomorrow().toDate());
        return callLogPreferencesFilter;
    }

    private CallLog setUpCallLogs() {
        CallLog callLog = new CallLog();
        callLog.setStartTime(DateUtil.now());
        callLog.setEndTime(DateUtil.now().plusMinutes(2));
        return callLog;
    }
}
