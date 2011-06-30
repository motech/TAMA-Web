package org.motechproject.tama.web;

import org.motechproject.tama.domain.MealAdviceType;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "mealadvicetypes", formBackingObject = MealAdviceType.class)
@RequestMapping("/mealadvicetypes")
@Controller
public class MealAdviceTypeController {
}
