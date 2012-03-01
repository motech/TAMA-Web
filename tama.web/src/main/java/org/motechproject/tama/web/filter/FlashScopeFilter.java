package org.motechproject.tama.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class FlashScopeFilter implements Filter{
    private final String FLASH_SESSION_KEY = "FLASH_SESSION_KEY";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            setFlashParamsInRequestAttributesAndRemoveThemFromSession((HttpServletRequest) servletRequest);
        }

        filterChain.doFilter(servletRequest, servletResponse);

        if (servletRequest instanceof HttpServletRequest) {
            storeFlashParamsInSession((HttpServletRequest) servletRequest);
        }
    }

    @Override
    public void destroy() {

    }

    private void setFlashParamsInRequestAttributesAndRemoveThemFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            setFlashParamsInRequestAttributes(request, session);
            session.removeAttribute(FLASH_SESSION_KEY);

        }
    }

    private void setFlashParamsInRequestAttributes(HttpServletRequest request, HttpSession session) {
        Map<String, Object> flashParams = (Map<String, Object>) session.getAttribute(FLASH_SESSION_KEY);
        if(flashParams != null){
            for (Map.Entry<String, Object> params : flashParams.entrySet()) {
                request.setAttribute(params.getKey(), params.getValue());
            }
        }
    }

    private void storeFlashParamsInSession(HttpServletRequest request) {
        Map<String, Object> flashParams = new HashMap<String, Object>();
        Enumeration attributeNames = request.getAttributeNames();
        while(attributeNames.hasMoreElements()) {
            String attributeName = (String) attributeNames.nextElement();
            if (attributeName.startsWith("flash.")){
                String flashAttributeName = attributeName.substring(6, attributeName.length());
                flashParams.put(flashAttributeName, request.getAttribute(attributeName));
            }
        }

        HttpSession session = request.getSession(false);
        session.setAttribute(FLASH_SESSION_KEY, flashParams);
    }
}
