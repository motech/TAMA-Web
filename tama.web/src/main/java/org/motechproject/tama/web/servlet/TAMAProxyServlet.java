package org.motechproject.tama.web.servlet;

import com.jsos.httpproxy.HttpProxyServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class TAMAProxyServlet extends HttpProxyServlet {

    @Override
    public void init() throws ServletException {
        super.init();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(new TAMAServletConfig(config));
    }
}

