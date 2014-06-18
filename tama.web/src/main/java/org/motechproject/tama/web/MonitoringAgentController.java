package org.motechproject.tama.web;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.ektorp.UpdateConflictException;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.MonitoringAgent;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.facility.repository.AllMonitoringAgents;
import org.motechproject.tama.web.view.MonitoringAgentsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/monitoringAgents")
@Controller
public class MonitoringAgentController extends BaseController {
	public static final String CREATE_VIEW = "monitoringAgents/create";
	public static final String SHOW_VIEW = "monitoringAgents/show";
	public static final String LIST_VIEW = "monitoringAgents/list";
	public static final String UPDATE_VIEW = "monitoringAgents/update";
	public static final String REDIRECT_TO_SHOW_VIEW = "redirect:/monitoringAgents/";
	public static final String NAME_ALREADY_IN_USE = "sorry, Name already in use.";
	private AllMonitoringAgents allMonitoringAgents;
	private AllClinics allClinics;

	@Autowired
	public MonitoringAgentController(AllMonitoringAgents allMonitoringAgents,
			AllClinics allClinics) {
		this.allMonitoringAgents = allMonitoringAgents;
		this.allClinics = allClinics;
	}


	@RequestMapping(method = RequestMethod.POST)
	public String create(@Valid MonitoringAgent monitoringAgent,
			BindingResult bindingResult, Model uiModel,
			HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("monitoringAgent", monitoringAgent);
			return CREATE_VIEW;
		}
		try {
			allMonitoringAgents.add(monitoringAgent,
					loggedInUserId(httpServletRequest));
			uiModel.asMap().clear();
		} catch (UpdateConflictException e) {
			bindingResult.addError(new FieldError("MonitoringAgent", "name",
					monitoringAgent.getName(), false,
					new String[] { "monitoring_agent_name_not_unique" },
					new Object[] {}, NAME_ALREADY_IN_USE));
			uiModel.addAttribute("monitoringAgent", monitoringAgent);
			return CREATE_VIEW;
		}
		return REDIRECT_TO_SHOW_VIEW
				+ encodeUrlPathSegment(monitoringAgent.getId(),
						httpServletRequest);
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(Model uiModel) {
		uiModel.addAttribute("monitoringAgent", new MonitoringAgent());
		return CREATE_VIEW;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String show(@PathVariable("id") String id, Model uiModel) {
		uiModel.addAttribute("monitoringAgent", allMonitoringAgents.get(id));
		uiModel.addAttribute("itemId", id);
		return SHOW_VIEW;
	}
	
    @RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        uiModel.addAttribute("monitoringAgents", new MonitoringAgentsView(allMonitoringAgents).getAll());
        return LIST_VIEW;
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid MonitoringAgent monitoringAgent,
			BindingResult bindingResult, Model uiModel,
			HttpServletRequest httpServletRequest) {
    	if (bindingResult.hasErrors()) {
			uiModel.addAttribute("monitoringAgent", monitoringAgent);
			return UPDATE_VIEW;
    	}
    	 uiModel.asMap().clear();
         allMonitoringAgents.update(monitoringAgent, loggedInUserId(httpServletRequest));
         return REDIRECT_TO_SHOW_VIEW + encodeUrlPathSegment(monitoringAgent.getId().toString(), httpServletRequest);
    }
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("monitoringAgent", allMonitoringAgents.get(id));
        return UPDATE_VIEW;
    }
    
    @ModelAttribute("monitoringAgents")
    public Collection<MonitoringAgent> populateMonitoringAgents(){
    	return allMonitoringAgents.getAll();
    }
    
    @ModelAttribute("clinics")
    public Collection<Clinic> populateClinics() {
        List<Clinic> allClinics = this.allClinics.getAll();
        Collections.sort(allClinics);
        return allClinics;
    }
	
}