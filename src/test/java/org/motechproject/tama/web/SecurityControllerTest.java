package org.motechproject.tama.web;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;


public class SecurityControllerTest {

    @Test
    public void testChangePasswordFormShouldRedirectToChangePasswordPage() throws Exception {
        assertEquals("redirect:/changePassword",new SecurityController().changePasswordForm());
    }
}
