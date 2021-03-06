package org.motechproject.tama.web;


import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.common.domain.TAMAUser;
import org.motechproject.tama.facility.repository.AllTAMAUsers;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.security.repository.AllTAMAEvents;
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
    private AllTAMAEvents allTAMAEvents;

    @Autowired
    public SecurityController(AllTAMAUsers allTAMAUsers, AllTAMAEvents allTAMAEvents) {
        this.allTAMAUsers = allTAMAUsers;
        this.allTAMAEvents = allTAMAEvents;
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.GET)
    public String changePasswordForm() {
        return "redirect:/changePassword";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public String changePassword(@RequestParam(value = "j_oldPassword", required = true) String oldPassword,
                                 @RequestParam(value = "j_newPassword", required = true) String newPassword,
                                 Model uiModel,
                                 HttpServletRequest request) {
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (!user.getPassword().equals(oldPassword)) {
            uiModel.addAttribute("errors", new FieldError("password", "j_oldPassword", TAMAMessages.OLD_PASSWORD_MISMATCH));
            return "changePassword";
        } else {
            user.setPassword(newPassword);
            allTAMAUsers.update(user.getTAMAUser(), loggedInUserId(request));
            allTAMAEvents.newChangePasswordEvent(user.getName(), user.getClinicName(), user.getClinicId(), user.getTAMAUser().getUsername());
            return "passwordReset";
        }
    }

    @RequestMapping(value = "changeUserPassword/{userType}/{id}", method = RequestMethod.GET)
    public String changeUserPassword(@PathVariable("id") String id, @PathVariable("userType") String userType, Model uiModel) {
        uiModel.addAttribute("userId", id);
        uiModel.addAttribute("userType", userType);
        return "changeUserPassword";
    }

    @RequestMapping(value = "changeUserPassword/{userType}/{id}", method = RequestMethod.POST)
    public String changeUserPasswordPost(@PathVariable("id") String id,
                                         @PathVariable("userType") String userType,
                                         @RequestParam(value = "j_newPassword", required = true) String newPassword,
                                         @RequestParam(value = "j_newPasswordConfirm", required = true) String newPasswordConfirmation,
                                         Model uiModel, HttpServletRequest request) {

        if (!newPassword.equals(newPasswordConfirmation)) {
            uiModel.addAttribute("errors", new FieldError("password", "j_newPasswordConfirm", TAMAMessages.NEW_PASSWORD_MISMATCH));
            uiModel.addAttribute("userId", id);
            uiModel.addAttribute("userType", userType);
            return "changeUserPassword";
        }
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        TAMAUser tamaUser = allTAMAUsers.getUser(id, userType);
        tamaUser.setPassword(newPassword);
        allTAMAUsers.update(tamaUser, loggedInUserId(request));
        allTAMAEvents.newChangePasswordEvent(tamaUser.getName(), tamaUser.getClinicName(), tamaUser.getClinicId(), user.getUsername());
        return "setUserPasswordSuccess";
    }

    @RequestMapping(value = "passwordReset", method = RequestMethod.GET)
    public String passwordReset() {
        return "redirect:/passwordReset";
    }
}
