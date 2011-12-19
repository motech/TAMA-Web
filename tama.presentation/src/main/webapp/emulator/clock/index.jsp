<html>
<head>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
    java.lang.reflect.Method m = java.lang.ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
          m.setAccessible(true);
          m.invoke(null, java.lang.System.class, "jvmfaketime", false);
    System.registerFakeCurrentTimeMillis();

%>
<%
	if (request.getMethod() == "POST") {
		String date = request.getParameter("date");
		String time = request.getParameter("time");
		Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		Date timeValue = new SimpleDateFormat("hh:mm:ss").parse(time.substring(1, time.length()-1));
		dateValue.setHours(timeValue.getHours());
        dateValue.setMinutes(timeValue.getMinutes());
        dateValue.setSeconds(timeValue.getSeconds());
        System.out.println("Posted date " + dateValue);
        System.deregisterFakeCurrentTimeMillis();
		long diffValue = (dateValue.getTime() - System.currentTimeMillis());
		System.registerFakeCurrentTimeMillis();
		System.out.println("offset calculated " + diffValue);
		System.setTimeOffset(diffValue);
		System.out.println("Date :" + new Date());
		System.out.println("offset :" +System.getTimeOffset());
	}
%>
    <script type="text/javascript">
        var djConfig = {parseOnLoad: false, isDebug: false, locale: 'en_in'};
    </script>
    <script src="/tama/resources/dojo/dojo.js" type="text/javascript" djConfig="parseOnLoad: true"  ></script>
    <script type="text/javascript">
          dojo.require("dijit.dijit"); // loads the optimized dijit layer
          dojo.require("dijit.form.DateTextBox");
          dojo.require("dijit.form.TimeTextBox");
    </script>
    <link rel="stylesheet" type="text/css" href="/tama/resources/dijit/themes/tundra/tundra.css" />
    <link rel="stylesheet" type="text/css" media="screen" href="/tama/resources/styles/standard.css" />
</head>
<body class="tundra">
<form action="" method="POST">
<table>
<%
java.util.Date date = new java.util.Date();
%>
    <tr><td>Date</td><td><input dojoType="dijit.form.DateTextBox" id="date" name="date" value="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(date)%>"/></td></tr>
    <tr><td>Time</td><td><input dojoType="dijit.form.TimeTextBox"  id="time" name="time" value="T<%=new java.text.SimpleDateFormat("HH:mm").format(date)%>"/></td></tr>
    <tr><td colspan="2" ><input type="submit" value="Set Time" /></td></tr>
</table>

</form>
</body>
</html>