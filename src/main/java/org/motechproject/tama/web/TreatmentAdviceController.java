package org.motechproject.tama.web;

import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RooWebScaffold(path = "treatmentadvices", formBackingObject = TreatmentAdvice.class)
@RequestMapping("/treatmentadvices")
@Controller
public class TreatmentAdviceController {
	
	@ModelAttribute("regimens")
	public List<Regimen> regimens() {
		return Regimen.findAllRegimens();
	}

	@ModelAttribute("regimenCompositions")
	public List<String> regimenCompositions() {
        ArrayList<String> regimenCompositions = new ArrayList<String>();
        for(Regimen regimen : regimens()) {
            for (RegimenComposition composition : regimen.getCompositions()) {
                regimenCompositions.add(composition.getRegimentCompositionId());
            }
        }
        return regimenCompositions;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/regimenCompositionsFor")
	public @ResponseBody Set<String> regimenCompositionsFor(@RequestParam String regimenId) {
        Set<String> regimenCompositions = new HashSet<String>();
        Set<RegimenComposition> compositions = Regimen.findRegimen(regimenId).getCompositions();
        for (RegimenComposition composition : compositions) {
            regimenCompositions.add(composition.getDisplayName());
        }
        return regimenCompositions;
	}

}
