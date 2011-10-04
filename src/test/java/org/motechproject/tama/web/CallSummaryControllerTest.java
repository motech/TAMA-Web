package org.motechproject.tama.web;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
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

    @Before
    public void setUp() {
        initMocks(this);
        callSummaryController = new CallSummaryController(callLogService, callLogViewMapper);

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    @Test
    public void shouldShowAllCallLogsBetweenEnteredDates() {
        List<CallLog> callLogs = Arrays.asList(new CallLog());
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", new CallLog()));


        when(user.isAdministrator()).thenReturn(true);
        when(callLogService.getLogsBetweenDates(any(DateTime.class), any(DateTime.class))).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);

        String view = callSummaryController.list(new Date(), new Date() ,request, uiModel);

        verify(uiModel).addAttribute("callsummary", callLogViews);
        assertEquals("callsummary/list", view);
    }

    @Test
    public void shouldShowOnlyLogsOfPatientsBelongingToTheClinic() {
        List<CallLog> callLogs = Arrays.asList(new CallLog());
        List<CallLogView> callLogViews = Arrays.asList(new CallLogView("patientId", new CallLog()));
        Date callLogStartDate = DateUtil.now().toDate();
        Date callLogEndDate = DateUtil.now().plusDays(1).toDate();

        when(user.isAdministrator()).thenReturn(false);
        when(user.getClinicId()).thenReturn("clinicId");
        when(callLogService.getByClinicId(DateUtil.newDateTime(callLogStartDate), DateUtil.newDateTime(callLogEndDate), "clinicId")).thenReturn(callLogs);
        when(callLogViewMapper.toCallLogView(callLogs)).thenReturn(callLogViews);

        String view = callSummaryController.list(callLogStartDate, callLogEndDate, request, uiModel);

        verify(uiModel).addAttribute("callsummary", callLogViews);
        assertEquals("callsummary/list", view);
    }
}
