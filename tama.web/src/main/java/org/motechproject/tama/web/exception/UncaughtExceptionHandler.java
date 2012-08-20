package org.motechproject.tama.web.exception;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/uncaughtException")
@Controller
public class UncaughtExceptionHandler {

    private Logger logger = Logger.getLogger(UncaughtExceptionHandler.class);

    @RequestMapping("handle")
    public String handle(HttpServletRequest request, Model model) {
        Throwable exception = (Throwable) request.getAttribute("javax.servlet.error.exception");
        logger.error(exception);
        model.addAttribute("exception", exception);
        return "uncaughtException";
    }
}
