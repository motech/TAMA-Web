<%@page import="java.net.URLDecoder"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONTokener"%>
<%@page import="java.util.Enumeration"%>
<%
System.out.println("####################################################################-?\n"
		+ new java.util.Date() +  "Outbound Call request from tama");
		
application.setAttribute("phone_no", request.getParameter("phone_no"));
String dataMap = request.getParameter("url");
System.out.println("\n" + dataMap + "\n");
dataMap = URLDecoder.decode(dataMap, "UTF8");
System.out.println("\n" + dataMap + "\n");
int idx = dataMap.indexOf("dataMap=");
dataMap  = dataMap.substring(idx + "dataMap=".length());
System.out.println("\n" + dataMap + "\n");

try {
JSONObject jsobject = new JSONObject(dataMap);
if (jsobject.has("total_times_to_send"))
application.setAttribute( "total_times_to_send", jsobject.getString("total_times_to_send"));
if (jsobject.has("times_sent"))
application.setAttribute( "times_sent", jsobject.getString("times_sent"));
if (jsobject.has("dosage_id"))
application.setAttribute( "dosage_id", jsobject.getString("dosage_id"));
if (jsobject.has("regimen_id"))	application.setAttribute( "regimen_id", jsobject.getString("regimen_id"));
if (jsobject.has("call_id"))	application.setAttribute( "call_id", jsobject.getString("call_id"));
if (jsobject.has("is_outbound_call"))	application.setAttribute( "is_outbound_call", jsobject.getString("is_outbound_call"));
if (jsobject.has("outbox_call"))	application.setAttribute( "outbox_call", jsobject.getString("outbox_call"));
} catch(Exception e) {
	e.printStackTrace();
}
System.out.println("End of outbound request");
%>
