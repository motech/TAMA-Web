package org.motechproject.tama.util;

import java.util.UUID;

public class UUIDUtil {

	public static String newUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replace("-","");
	}
}
