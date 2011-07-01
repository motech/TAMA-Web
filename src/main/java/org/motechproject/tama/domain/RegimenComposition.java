package org.motechproject.tama.domain;

import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Set;
import org.motechproject.tama.domain.Drug;
import java.util.HashSet;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;

@RooJavaBean
@RooToString
@RooEntity
public class RegimenComposition {

    private String regimentCompositionId;

    @DocumentReferences(backReference="regimenCompositionId", fetch = FetchType.EAGER)
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Drug> drugs = new HashSet<Drug>();

            ;
}
