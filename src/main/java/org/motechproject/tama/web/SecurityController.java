package org.motechproject.tama.web;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/security")
@Controller
public class SecurityController extends BaseController{

    @RequestMapping(value = "changePassword", method = RequestMethod.GET)
    public String changePasswordForm(){
        return "redirect:/changePassword";
    }
}
