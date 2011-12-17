<%
String phone = (String)application.getAttribute("phone_no");
if (phone != null && phone.length()>0){
	response.setContentType("text/javascript");
	out.print("{\"phone_no\":\"" + phone + "\"," + 
				"\"regimen_id\":\"" + (String)application.getAttribute("regimen_id") + "\"," +
				"\"total_times_to_send\":" + (String)application.getAttribute("total_times_to_send") + "," + 
				"\"times_sent\":" + (String)application.getAttribute("times_sent") + "," + 
				"\"dosage_id\":\"" + (String)application.getAttribute("dosage_id") + "\"," +
				"\"call_id\":\"" + (String)application.getAttribute("call_id") + "\"," +
				"\"outbox_call\":\"" + (String)application.getAttribute("outbox_call") + "\"," +
				"\"is_outbound_call\":\"" + (String)application.getAttribute("is_outbound_call") + "\"" +
			   "}");
	application.setAttribute("phone_no", null);
} else {
	out.print("{}");
}
%>