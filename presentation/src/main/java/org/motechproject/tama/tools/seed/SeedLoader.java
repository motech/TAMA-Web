package org.motechproject.tama.tools.seed;

import org.apache.log4j.Logger;

import java.util.List;

public class SeedLoader {
	private static Logger LOG = Logger.getLogger(SeedLoader.class);
	private final List<Seed> seeds;

	public SeedLoader(List<Seed> seeds) {
		this.seeds = seeds;
	}
	
	public void load() throws InterruptedException {
		LOG.info("Started loading seeds :" + seeds.toString());
		for (Seed seed : seeds) {
			seed.run();
		}
	}
}