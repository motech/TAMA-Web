package org.motechproject.tama.web;


import org.motechproject.tama.TAMAMessages;
import org.motechproject.tama.repository.TAMAUsers;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/security")
@Controller
public class SecurityController extends BaseController{

    @Autowired
    private TAMAUsers tamaUsers;

    @Autowired
    private HomeController homeController;

    @RequestMapping(value = "changePassword", method = RequestMethod.GET)
    public String changePasswordForm(){
        return "redirect:/changePassword";
    }

    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public String create(@RequestParam(value = "j_oldPassword", required = true) String oldPassword,
                         @RequestParam(value = "j_newPassword", required = true) String newPassword ,
                         @RequestParam(value = "j_newPasswordConfirm", required = true) String newPasswordConfirmation,
                         Model uiModel,
                         HttpServletRequest request){
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER_ATTR);
        if(!user.getPassword().equals(oldPassword)){
            uiModel.addAttribute("errors",new FieldError("password","j_oldPassword",TAMAMessages.OLD_PASSWORD_MISMATCH));
            return "changePassword";
        }
        else{
            user.setPassword(newPassword);
            tamaUsers.put(user.getTAMAUser());
            return homeController.homePage();
        }
    }
}
