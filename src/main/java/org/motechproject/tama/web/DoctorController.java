package org.motechproject.tama.web;

import org.motechproject.tama.Doctor;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "doctors", formBackingObject = Doctor.class)
@RequestMapping("/doctors")
@Controller
public class DoctorController {
}
