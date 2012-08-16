package org.motechproject.tama.web.exception;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/uncaughtException")
@Controller
public class UncaughtExceptionHandler {

    @RequestMapping("handle")
    public String handle(HttpServletRequest request, Model model) {
        model.addAttribute("exception", request.getAttribute("javax.servlet.error.exception"));
        return "uncaughtException";
    }
}
