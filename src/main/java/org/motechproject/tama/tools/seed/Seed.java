package org.motechproject.tama.tools.seed;

import org.apache.log4j.Logger;

public abstract class Seed
{
	
	Logger LOGGER = Logger.getLogger(this.getClass());
	
	public void run() {
		preLoad();
		load();
		postLoad();
	}
	
	private void postLoad() {
		LOGGER.info("Seed finished.");
	}

	private void preLoad() {
		LOGGER.info("Seed started.");
	}

	protected abstract void load();
	
}