package org.motechproject.tama.web;

import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String homePage(HttpServletRequest request) {
        AuthenticatedUser user = (AuthenticatedUser) request.getSession().getAttribute(LoginSuccessHandler.LOGGED_IN_USER);
        if (user.isAnalyst()) {
            return "redirect:/analysisData";
        } else if (user.isAdministrator()) {
            return "redirect:/clinics";
        }
        return "redirect:/patients";
    }
}
