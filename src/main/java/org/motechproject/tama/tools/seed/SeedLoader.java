package org.motechproject.tama.tools.seed;

import java.util.List;

import org.apache.log4j.Logger;

public class SeedLoader {
	private static Logger LOG = Logger.getLogger(SeedLoader.class);
	private final List<Seed> seeds;

	public SeedLoader(List<Seed> seeds) {
		this.seeds = seeds;
	}
	
	public void load() {
		LOG.info("Started loading seeds :" + seeds.toString());
		for (Seed seed : seeds) {
			seed.run();
		}
	}
}