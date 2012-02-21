package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.service.CallLogService;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
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
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
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

    @Mock
    Properties properties;


    @Before
    public void setUp() {
        initMocks(this);
        callSummaryController = new CallSummaryController(callLogService, callLogViewMapper, properties);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldShowCallLogsForFirstPageBetweenEnteredDates() {
        CallLog callLog = setUpCallLog();
        List<CallLog> callLogs = Arrays.asList(callLog);
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", callLog, "clinic", new ArrayList<String>()));
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter();

        when(user.isAdministrator()).thenReturn(true);
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getLogsForDateRange(any(DateTime.class), any(DateTime.class), anyInt())).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("callSummary", callLogViews);
        assertEquals("callsummary/list", view);

        verify(callLogService).getLogsForDateRange(any(DateTime.class), any(DateTime.class), eq(0));
    }

    @Test
    public void shouldShowCallLogsForSecondPageBetweenEnteredDates() {
        setUpCallLog();
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter();
        callLogPreferencesFilter.setPageNumber(2);

        when(user.isAdministrator()).thenReturn(true);
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");

        callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(callLogService).getLogsForDateRange(any(DateTime.class), any(DateTime.class), eq(20));
    }

    @Test
    public void shouldShowOnlyLogsOfPatientsBelongingToTheClinic() {
        CallLog callLog = setUpCallLog();
        List<CallLog> callLogs = Arrays.asList(callLog);
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", callLog, "clinic", new ArrayList<String>()));
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter();

        when(user.isAdministrator()).thenReturn(false);
        when(user.getClinicId()).thenReturn("clinicId");
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getLogsForDateRangeAndClinic(DateUtil.newDateTime(callLogPreferencesFilter.getCallLogStartDate()), DateUtil.newDateTime(callLogPreferencesFilter.getCallLogEndDate()).
                plusHours(23).plusMinutes(59).plusSeconds(59), "clinicId", 0)).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("callSummary", callLogViews);
        assertEquals("callsummary/list", view);
    }
    
    @Test
    public void shouldReturnTotalNumberOfPages() {
        assertEquals((Integer) 1, callSummaryController.calculateTotalNumberOfPages(10, 10));
        assertEquals((Integer) 2, callSummaryController.calculateTotalNumberOfPages(10, 11));
        assertEquals((Integer) 1, callSummaryController.calculateTotalNumberOfPages(10, 9));
    }

    private CallLogPreferencesFilter setUpCallLogPreferencesFilter() {
        CallLogPreferencesFilter callLogPreferencesFilter = new CallLogPreferencesFilter();
        callLogPreferencesFilter.setCallLogStartDate(DateUtil.today().toDate());
        callLogPreferencesFilter.setCallLogEndDate(DateUtil.tomorrow().toDate());
        callLogPreferencesFilter.setPageNumber(1);
        return callLogPreferencesFilter;
    }

    private CallLog setUpCallLog() {
        CallLog callLog = new CallLog();
        callLog.setStartTime(DateUtil.now());
        callLog.setEndTime(DateUtil.now().plusMinutes(2));
        return callLog;
    }
}
