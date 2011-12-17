package org.motechproject.tama.web;


import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllTAMAUsers;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/security")
@Controller
public class SecurityController extends BaseController {

    private AllTAMAUsers allTAMAUsers;

    @Autowired
    public SecurityController(AllTAMAUsers allTAMAUsers) {
        this.allTAMAUsers = allTAMAUsers;
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.GET)
    public String changePasswordForm() {
        return "redirect:/changePassword";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public String changePassword(@RequestParam(value = "j_oldPassword", required = true) String oldPassword,
                                 @RequestParam(value = "j_newPassword", required = true) String newPassword,
                                 @RequestParam(value = "j_newPasswordConfirm", required = true) String newPasswordConfirmation,
                                 Model uiModel,
                                 HttpServletRequest request) {
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (!user.getPassword().equals(oldPassword)) {
            uiModel.addAttribute("errors", new FieldError("password", "j_oldPassword", TAMAMessages.OLD_PASSWORD_MISMATCH));
            return "changePassword";
        } else {
            user.setPassword(newPassword);
            allTAMAUsers.update(user.getTAMAUser());
            return "passwordReset";
        }
    }

    @RequestMapping(value = "resetClinicianPassword/{id}", method = RequestMethod.GET)
    public String resetClinicianPassword(@PathVariable("id") String id, Model uiModel) {
        uiModel.addAttribute("clinicianId", id);
        return "setClinicianPassword";
    }

    @RequestMapping(value = "resetClinicianPassword/{id}", method = RequestMethod.POST)
    public String resetClinicianPasswordPost(@PathVariable("id") String id, @RequestParam(value = "j_newPassword", required = true) String newPassword,
                                             @RequestParam(value = "j_newPasswordConfirm", required = true) String newPasswordConfirmation,
                                             Model uiModel) {
        if (!newPassword.equals(newPasswordConfirmation)) {
            uiModel.addAttribute("errors", new FieldError("password", "j_newPasswordConfirm", TAMAMessages.NEW_PASSWORD_MISMATCH));
            uiModel.addAttribute("clinicianId", id);
            return "setClinicianPassword";
        }
        Clinician clinician = allTAMAUsers.getClinician(id);
        clinician.setPassword(newPassword);
        allTAMAUsers.update(clinician);
        return "setClinicianPasswordSuccess";
    }

    @RequestMapping(value = "passwordReset", method = RequestMethod.GET)
    public String passwordReset() {
        return "redirect:/passwordReset";
    }
}
