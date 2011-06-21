package org.motechproject.tama;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooEntity
public class Title {

    @NotNull
    private String type;
}
