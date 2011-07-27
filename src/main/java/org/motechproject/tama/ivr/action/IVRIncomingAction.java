package org.motechproject.tama.ivr.action;

import org.motechproject.tama.ivr.IVRRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IVRIncomingAction {
    String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response);
    String getKey();
}
