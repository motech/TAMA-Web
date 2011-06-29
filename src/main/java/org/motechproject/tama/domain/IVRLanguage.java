package org.motechproject.tama.domain;

import de.saxsys.roo.equals.addon.RooEquals;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooEntity
@RooEquals
public class IVRLanguage {

    @NotNull
    private String name;
}
