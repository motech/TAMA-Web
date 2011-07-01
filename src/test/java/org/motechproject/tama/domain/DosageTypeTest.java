package org.motechproject.tama.domain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.ektorp.DocumentNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.repository.DosageTypes;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DosageType.class)
public class DosageTypeTest {

	@Test
	public void shouldReturnNullIfNoDocumentFound() {
		String dosageTypeId = "id";
		DosageTypes dosageTypes = mock(DosageTypes.class);
		when(dosageTypes.get(dosageTypeId)).thenThrow(new DocumentNotFoundException("notFound"));
		
		PowerMockito.spy(DosageType.class);
		when(DosageType.dosageTypes()).thenReturn(dosageTypes);
		
		DosageType dosageType = DosageType.findDosageType(dosageTypeId);
		Assert.assertNull(dosageType);
	}
}
