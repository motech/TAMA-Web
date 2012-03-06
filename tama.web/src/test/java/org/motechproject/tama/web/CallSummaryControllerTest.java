package org.motechproject.tama.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
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
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallSummaryControllerTest {

    public static final String PATIENT_ID = "patientId";
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
    public void shouldReturnCallLogsNotFoundViewIfNoLogsAreFoundForPatient(){
        List<CallLog> callLogs = new ArrayList<CallLog>();
        List<CallLogView> callLogViews = new ArrayList<CallLogView>();
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter(PATIENT_ID);

        when(user.isAdministrator()).thenReturn(true);
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getLogsForDateRange(any(CallLogSearch.class))).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("message", CallSummaryController.PATIENT_ID_WRONG_MESSAGE);
        assertEquals("callsummary/nologs", view);
    }

    @Test
    public void shouldReturnCallLogsNotFoundViewIfNoLogsAreFoundForDateRange(){
        List<CallLog> callLogs = new ArrayList<CallLog>();
        List<CallLogView> callLogViews = new ArrayList<CallLogView>();
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter("");

        when(user.isAdministrator()).thenReturn(true);
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getLogsForDateRange(any(CallLogSearch.class))).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("message", CallSummaryController.NO_LOGS_FOR_DATE_RANGE);
        assertEquals("callsummary/nologs", view);
    }

    @Test
    public void shouldShowCallLogsForFirstPageBetweenEnteredDates() {
        CallLog callLog = setUpCallLog();
        List<CallLog> callLogs = Arrays.asList(callLog);
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", callLog, "clinic", new ArrayList<String>()));
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter(PATIENT_ID);

        when(user.isAdministrator()).thenReturn(true);
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getLogsForDateRange(any(CallLogSearch.class))).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(uiModel).addAttribute("callSummary", callLogViews);
        assertEquals("callsummary/list", view);

        ArgumentCaptor<CallLogSearch> searchArgumentCaptor = ArgumentCaptor.forClass(CallLogSearch.class);
        verify(callLogService).getLogsForDateRange(searchArgumentCaptor.capture());
        assertNull(searchArgumentCaptor.getValue().getClinicId());
        assertEquals(CallLog.CallLogType.Answered, searchArgumentCaptor.getValue().getCallLogType());
        assertEquals(PATIENT_ID.toLowerCase(), searchArgumentCaptor.getValue().getPatientId());
        assertEquals(true, searchArgumentCaptor.getValue().isSearchAllClinics());
        assertEquals(0, searchArgumentCaptor.getValue().getStartIndex().intValue());
        assertEquals(20, searchArgumentCaptor.getValue().getLimit().intValue());
    }

    @Test
    public void shouldShowCallLogsForSecondPageBetweenEnteredDates() {
        setUpCallLog();
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter(PATIENT_ID);
        callLogPreferencesFilter.setPageNumber("2");

        when(user.isAdministrator()).thenReturn(true);
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getTotalNumberOfLogs(any(CallLogSearch.class))).thenReturn(30);

        callSummaryController.list(callLogPreferencesFilter, bindingResult, request, uiModel);

        verify(callLogService).getLogsForDateRange(any(CallLogSearch.class));
    }

    @Test
    public void shouldShowOnlyLogsOfPatientsBelongingToTheClinic() {
        CallLog callLog = setUpCallLog();
        List<CallLog> callLogs = Arrays.asList(callLog);
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", callLog, "clinic", new ArrayList<String>()));
        CallLogPreferencesFilter callLogPreferencesFilter = setUpCallLogPreferencesFilter(PATIENT_ID);

        when(user.isAdministrator()).thenReturn(false);
        when(user.getClinicId()).thenReturn("clinicId");
        when(properties.getProperty(Matchers.<String>any(), Matchers.<String>any())).thenReturn("20");
        when(callLogService.getLogsForDateRange(any(CallLogSearch.class))).thenReturn(callLogs);
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
    
    @Test
    public void shouldReturnValidPageNumber() {
        assertEquals((Integer) 1, callSummaryController.getValidPageNumber("20", 10));
        assertEquals((Integer) 1, callSummaryController.getValidPageNumber("-20", 10));
        assertEquals((Integer) 1, callSummaryController.getValidPageNumber("c", 10));
        assertEquals((Integer) 3, callSummaryController.getValidPageNumber("3", 10));
    }

    private CallLogPreferencesFilter setUpCallLogPreferencesFilter(String patientId) {
        CallLogPreferencesFilter callLogPreferencesFilter = new CallLogPreferencesFilter();
        callLogPreferencesFilter.setCallLogStartDate(DateUtil.today().toDate());
        callLogPreferencesFilter.setCallLogEndDate(DateUtil.tomorrow().toDate());
        callLogPreferencesFilter.setPageNumber("1");
        callLogPreferencesFilter.setCallType("Answered");
        callLogPreferencesFilter.setPatientId(patientId);
        return callLogPreferencesFilter;
    }

    private CallLog setUpCallLog() {
        CallLog callLog = new CallLog();
        callLog.setStartTime(DateUtil.now());
        callLog.setEndTime(DateUtil.now().plusMinutes(2));
        return callLog;
    }
}
