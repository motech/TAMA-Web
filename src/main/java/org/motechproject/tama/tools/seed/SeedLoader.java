package org.motechproject.tama.tools.seed;

import java.util.List;

public class SeedLoader {

	private final List<Seed> seeds;

	public SeedLoader(List<Seed> seeds) {
		this.seeds = seeds;
	}
	
	public void load() {
		for (Seed seed : seeds) {
			seed.load();
		}
	}
}