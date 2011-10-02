package org.motechproject.tama.web;


import org.motechproject.tama.ivr.logging.domain.CallLog;
import org.motechproject.tama.ivr.logging.service.CallLogService;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.view.CallLogView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public String list(Model uiModel) {
        List<CallLogView> callLogViews = callLogViewMapper.toCallLogView(callLogService.getAll());
        uiModel.addAttribute("callsummary", callLogViews);
        return "callsummary/list";
    }
}
