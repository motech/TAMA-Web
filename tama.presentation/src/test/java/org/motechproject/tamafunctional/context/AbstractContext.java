package org.motechproject.tamafunctional.context;


import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractContext implements Context{

    private final List<Context> dependencies;

    public AbstractContext(Context... dependencies){
        this.dependencies = Arrays.asList(dependencies);
    }

    @Override
    public final void build(WebDriver webDriver) {
        this.buildDependencies(dependencies, webDriver);
        this.create(webDriver);
    }

    protected abstract void create(WebDriver webDriver);

    private void buildDependencies(List<Context> preConditions, WebDriver webDriver) {
        for(Context preCondition : preConditions){
            preCondition.build(webDriver);
        }
    }
}
