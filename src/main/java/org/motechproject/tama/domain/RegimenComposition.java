package org.motechproject.tama.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;

import org.ektorp.docref.DocumentReferences;
import org.ektorp.docref.FetchType;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class RegimenComposition {

    private String regimentCompositionId;

    @DocumentReferences(backReference="regimenCompositionId", fetch = FetchType.EAGER)
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Drug> drugs = new HashSet<Drug>();
}
