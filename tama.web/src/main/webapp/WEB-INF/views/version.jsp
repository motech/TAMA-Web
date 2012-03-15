<%@page import="java.util.Properties" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
 "http://www.w3.org/TR/html4/loose.dtd">

<%
 Properties prop = new Properties();
 String version = "0";
 Object resourceAsStream = getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF");
 if (resourceAsStream != null) {
    prop.load(getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
    version = prop.getProperty("Hudson-Build-Number");
 }
%>
Version: TAMA Pilot  - <% out.println(version); %>

