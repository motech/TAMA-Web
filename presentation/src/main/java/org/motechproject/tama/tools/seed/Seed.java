package org.motechproject.tama.tools.seed;

import org.apache.log4j.Logger;

public abstract class Seed {
    Logger LOG = Logger.getLogger(this.getClass());

    public void run() throws InterruptedException {
        preLoad();
        load();
        postLoad();
    }

    private void postLoad() {
        LOG.info("Seed finished.");
    }

    private void preLoad() {
        LOG.info("Seed started.");
    }

    protected abstract void load() throws InterruptedException;

}