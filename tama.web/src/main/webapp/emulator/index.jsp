<%@page import="org.motechproject.util.DateUtil"%>
<%@page import="java.lang.reflect.Method"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Properties"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%
	boolean fakeTimeAvailable = false;
        try {
        java.lang.reflect.Method m = java.lang.ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
        m.setAccessible(true);
        m.invoke(null, java.lang.System.class, "jvmfaketime", false);
                System.registerFakeCurrentTimeMillis();



                if (request.getMethod() == "POST") {
                try{

                        String date = request.getParameter("date");
                        String time = request.getParameter("time");
                        Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                        Date timeValue = new SimpleDateFormat("HH:mm:ss").parse(time.substring(1, time.length()));
                        dateValue.setHours(timeValue.getHours());
                dateValue.setMinutes(timeValue.getMinutes());
                dateValue.setSeconds(timeValue.getSeconds());
                System.out.println("Posted date " + time.substring(1, time.length()-1));

                        System.deregisterFakeCurrentTimeMillis();

                        long diffValue = (dateValue.getTime() - System.currentTimeMillis());

                        System.registerFakeCurrentTimeMillis();
                        System.out.println("offset calculated " + diffValue);
                        System.setTimeOffset(diffValue);
                        System.out.println("Date :" + new Date());
                  } catch(java.lang.Exception e) {
                      out.println("Error: " + e.getMessage());
                      return;
                  }
                  out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                  return;
                }
                fakeTimeAvailable = true;
         } catch(Exception ignore){}
        java.util.Date curdate = new java.util.Date();
        if (!fakeTimeAvailable)
                curdate = DateUtil.now().toDate();

%><html>
  <head>
    <script type="text/javascript">
        var djConfig = {parseOnLoad: false, isDebug: false, locale: 'en_in'};
    </script>
    <%
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        Properties tamaProperties = (Properties)appCtx.getBean("tamaProperties", Properties.class);
        String applicationVersion = (String)tamaProperties.getProperty("application.version");
    %>
    <script src='<%= application.getContextPath()%>/resources-<%= applicationVersion%>/dojo/dojo.js' type="text/javascript" djConfig="parseOnLoad: true"  ></script>
    <script type="text/javascript">

          dojo.require("dijit.dijit"); // loads the optimized dijit layer
          dojo.require("dijit.form.DateTextBox");
          dojo.require("dijit.form.TimeTextBox");



          dojo.addOnLoad(function() {
                  new dijit.form.TimeTextBox({
                      name: "time",
                      value: new Date(0,0,0,<%=curdate.getHours()%>,<%=curdate.getMinutes()%>, <%=curdate.getSeconds()%>),
                      constraints: {
                          timePattern: 'HH:mm:ss',
                          clickableIncrement: 'T00:15:00',
                          visibleIncrement: 'T00:15:00',
                          visibleRange: 'T01:00:00'
                      }
                  },
                  "time");
             });
    </script>
    <link rel="stylesheet" type="text/css" href="<%= application.getContextPath() %>/resources-<%= applicationVersion%>/dijit/themes/tundra/tundra.css" />
    <style>
    .dijitPopup {
        background-color: lightgray;
        border: 0 none;
        margin: 0;
        padding: 0;
        position: absolute;
    }
    </style>
    <script>



    function displayMsg(data){
    	dojo.byId('timeMessage').style.display="";
        dojo.byId('timeMessage').innerHTML = data;
        setTimeout(function() {
            dojo.byId('timeMessage').style.display="none";
        }, 3000);	
    }
    
    function submitTime() {
    	if (dojo.byId('fakeTimeOption').checked) {
	        dojo.xhrPost({
	            form:"timeForm",
	            load: function(data, ioArgs){
	                displayMsg("Updated: " + data);
	            },
	            error: function(err, ioArgs){
	                alert(err);
	            }
	         });
    	} else {
    		var urlString = "<%=application.getContextPath() %>" + "/motech-delivery-tools/datetime/update?date=" + dojo.byId('date').value + "&hour=" + dijit.byId('time').value.getHours() + "&minute=" + dijit.byId('time').value.getMinutes();
    		dojo.xhrGet({
    			url : urlString,
    			load : function(data) { displayMsg	(data);}
    		});
    	}
     }
    </script>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
<script src="js/Recording.js"></script>
<script type="text/javascript">
    $(document).ready(function(){
        $('#missedCallButton').click(function () {
            dojo.xhrPost({
                url:'<%=application.getContextPath() %>/ivr/reply/callback?external_id=' + $('#missedCall').val() + "&call_type=Outbox",
                content:{ 'phone_no':$('#phone').val(), 'status':'ring', 'sid':callId},
                load:function () {
                    alert('Posted missed call');
                }
            })
        });

        window.cacheControl = new CacheControl();
        window.recording = new Recording(window.cacheControl);
    });
</script>
<script type="text/javascript">
/**
* jQuery Cookie plugin
*
* Copyright (c) 2010 Klaus Hartl (stilbuero.de)
* Dual licensed under the MIT and GPL licenses:
* http://www.opensource.org/licenses/mit-license.php
* http://www.gnu.org/licenses/gpl.html
*
*/
jQuery.cookie = function (key, value, options) {

    // key and at least value given, set cookie...
    if (arguments.length > 1 && String(value) !== "[object Object]") {
        options = jQuery.extend({}, options);

        if (value === null || value === undefined) {
            options.expires = -1;
        }

        if (typeof options.expires === 'number') {
            var days = options.expires, t = options.expires = new Date();
            t.setDate(t.getDate() + days);
        }

        value = String(value);

        return (document.cookie = [
            encodeURIComponent(key), '=',
            options.raw ? value : encodeURIComponent(value),
            options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
            options.path ? '; path=' + options.path : '',
            options.domain ? '; domain=' + options.domain : '',
            options.secure ? '; secure' : ''
        ].join(''));
    }

    // key and possibly options given, get cookie...
    options = value || {};
    var result, decode = options.raw ? function (s) { return s; } : decodeURIComponent;
    return (result = new RegExp('(?:^|; )' + encodeURIComponent(key) + '=([^;]*)').exec(document.cookie)) ? decode(result[1]) : null;
};
</script>

<script>
var contextRoot = "<%=application.getContextPath() %>/";
var collectdtmf = 1;
var dtmf = "";
var callId;

function deleteCookie()
{
var d = new Date();
document.cookie = "v0=1;expires=" + d.toGMTString() + ";" + "path=<%=application.getContextPath() %>/ivr/;";

alert(document.cookie);
}
function pollCall() {
	if ($('#poll_call').is(':checked')	)
	$.getJSON("<%=application.getContextPath() %>/emulator/calls.jsp?phone=" + $('#phone').val() , function(data){
		if (data.phone_no){
			if (confirm("Incoming call.. Receive?")) {
				 $('#phone').val(data.phone_no);
				 $('#dosage_id').val(data.dosage_id);
				 $('#regimen_id').val(data.regimen_id);
				 $('#times_sent').val(data.times_sent);
				 $('#total').val(data.total_times_to_send);
				 $('#call_id').val(data.call_id);
                 $('#is_outbound_call').attr('checked',(data.is_outbound_call === "true"));
                 $('#outbox_call').attr('checked',(data.outbox_call === "true"));
			}
		}
		setTimeout(pollCall, 1000);
	});
}

$(function(){
	setTimeout(pollCall, 500);
});


function playfile(id) {
	if (!$('#mute').is(":checked"))
		document.getElementById(id).play();
	//console.log(new Date());
}

function playAll(idx) {
var duration = 0;
var audioslist = $('.audio');
var i = idx;
if (typeof idx == "undefined") i=0;
for (; i<audioslist.length; i++){
	var ad = audioslist[i].id;
	window.setTimeout("playfile('" + ad+ "');", (1+duration*1000));
	if (isNaN(audioslist[i].duration)){ setTimeout("playAll(" + i + ")", 500);  return;}
	else {
		duration += audioslist[i].duration;
	}
}	
}


function call(path) {
	try{
	
	$.ajax({url:path,
			crossDomain:true,
            beforeSend : function(xhr) {
               //xhr.setRequestHeader('Cookie', "");
               //xhr.setRequestHeader("X-Set-Cookie", "foo2=quux");
             },
			success: function(data) {
				//alert(data);
				$('#response').val(data);
                if ($(data).length == 1 && $(data)[0].childElementCount == 0) {
                    send('');
                    return;
                }
				var html = "";//'<audio controls="controls" id="message" autoplay="autoplay">';
				//var msg = ""
				collectdtmf = $(data).find('collectdtmf').attr('l');
				$(data).find('playaudio').each(function() {
					var filename = $(this).text();//.replace(/.*\//, "");
					if(filename.indexOf("signature_music") === -1){
					    window.recording.record(filename);
					}
					var text = filename;//.replace(/.wav/,"");
					html += '<audio src="' + filename + '" autostart=false width=1 height=1 id="'+filename+'" enablejavascript="true" class="audio"/>' +
					'<button id="' + filename+ '" onclick="play(\'' +filename+ '\');">&raquo;'+text+' </button>';
					html += '<source src="' + filename+ '" type="audio/wave" />';
					//msg+=text + " ";
				});
				$(data).find('playtext').each(function() {
					html+= "<div>" +  $(this).text() + "</div>"
				});
				//html += "</audio>";
				//html ='<button  onclick="play(\'message\');">&raquo;'+msg+' </button><span style="float:right;">&nbsp;'+ html + '</span>';
                if ($(data).find('playaudio').length > 0 && $(data).find('collectdtmf').length == 0) html+= '<button onclick="send()">Continue</button>'
				$('#result').html(html);
				
				playAll();
			    },
			error:function(x, status, err) {alert('error ' + status + err);}
			});	
	} catch(e) {
		alert(e);
	}
}

function newCall(){
	callId = Math.floor(Math.random()*10000000000);
	var phone = $('#phone').val();
	var symptoms_reporting_option = ($('#symptoms_reporting').is(":checked")?"&symptoms_reporting=true":"");
	call(contextRoot + 'ivr/reply?event=NewCall&cid='+phone+'&sid=' + callId + symptoms_reporting_option);
}
function endCall() {
	var phone = $('#phone').val();
	var symptoms_reporting_option = ($('#symptoms_reporting').is(":checked")?"&symptoms_reporting=true":"");
    deleteCookie();
	call(contextRoot + 'ivr/reply?event=Hangup&cid='+phone+'&sid=' + callId + symptoms_reporting_option);
    $.cookie('current_decision_tree_position', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('preferred_lang_code', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('call_id', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('call_detail_record_id', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('LastCompletedTree', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('LastPlayedVoiceMessageID', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('outboxCompleted', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('lastPlayedHealthTip', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('healthTipsPlayedCount', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('switch_to_dial_state', null , {'path':'<%=application.getContextPath() %>/ivr/'});
    $.cookie('number_of_clinicians_called', null , {'path':'<%=application.getContextPath() %>/ivr/'});
}

function login() {
	var pin = $('#pin').val();
	var phone = $('#phone').val();
	var dosageId = $('#dosage_id').val();
	var regimen_id = $('#regimen_id').val();
	var times_sent = $('#times_sent').val();
	var total = $('#total').val();
	var call_id = $('#call_id').val();
    var is_outbound_call = $('#is_outbound_call').is(":checked")?"true":"";
    var outbox_call = $('#outbox_call').is(":checked")?"true":"";
	var symptoms_reporting_option = ($('#symptoms_reporting').is(":checked")?"&symptoms_reporting=true":"");
	var dataMap = "";
	if (is_outbound_call == 'true')
		dataMap = ($('#symptoms_reporting').is(":checked"))?"":('&dataMap={%27dosage_id%27:%27'+dosageId+'%27, %27regimen_id%27:%27' + regimen_id + '%27, %27times_sent%27:%27' +times_sent+'%27, %27total_times_to_send%27:%27'+total+'%27, %27call_id%27:%27'+call_id+'%27, %27outbox_call%27:%27'+outbox_call+'%27, %27is_outbound_call%27:%27'+is_outbound_call+'%27}');
	call(contextRoot + 'ivr/reply?event=GotDTMF&cid='+phone+'&data=' + pin + '&sid=' + callId + dataMap + symptoms_reporting_option);
}
function send(i){
	if (i==='') dtmf=i;
	else {
		dtmf += i;
		if (typeof collectdtmf != "undefined" && collectdtmf>dtmf.length) {
			return;
		}
	}


	var phone = $('#phone').val();
	var dosageId = $('#dosage_id').val();
	var regimen_id = $('#regimen_id').val();
	var times_sent = $('#times_sent').val();
	var total = $('#total').val();
	var symptoms_reporting_option = ($('#symptoms_reporting').is(":checked")?"&symptoms_reporting=true":"");
    var is_outbound_call = $('#is_outbound_call').is(":checked")?"true":"";
    var outbox_call = $('#outbox_call').is(":checked")?"true":"";
	var dataMap = "";
    var event = "event=GotDTMF&";
    if (collectdtmf) event = "event=GotDTMF&" +'&data=' + dtmf + "&";
	if (is_outbound_call == 'true')
		dataMap = ($('#symptoms_reporting').is(":checked"))?"":('&dataMap={%27dosage_id%27:%27'+dosageId+'%27, %27regimen_id%27:%27' + regimen_id + '%27, %27times_sent%27:%27' +times_sent+'%27, %27total_times_to_send%27:%27'+total+'%27, %27outbox_call%27:%27'+outbox_call+'%27, %27is_outbound_call%27:%27'+is_outbound_call+'%27}');
	call(contextRoot + 'ivr/reply?' + event + 'cid='+phone + 'sid=' + callId + dataMap + symptoms_reporting_option);
	dtmf="";
}
function play(i){
	var el = document.getElementById(i);
	el.play();
}

</script>
<style>
.optional{
	display:none;
}
</style>
</head>
<body>
<div>
<div id="result">

</div>
<br/>
<button onclick="newCall();">New Call</button>
<button onclick="login();">Login</button>
<button onclick="endCall();">End Call</button>
<a href="playFiles.jsp">Play Files</a>
<table><tr><td>
 <table>
 <tr><td><button onclick="send(1);">1</button> </td><td><button onclick="send(2);">2</button></td><td><button onclick="send(3);">3</button></td></tr>
 <tr><td><button onclick="send(4);">4</button> </td><td><button onclick="send(5);">5</button></td><td><button onclick="send(6);">6</button></td></tr>
 <tr><td><button onclick="send(7);">7</button> </td><td><button onclick="send(8);">8</button></td><td><button onclick="send(9);">9</button></td></tr>
 <tr><td colspan="2" ><button onclick="send(0);">0</button></td><td><button onclick="send('');">blank</button></td></tr>
</table>
</td><td style="width:450px;"></td><td>
<form id="timeForm" action="" method="POST">
<table>
  <tr><td><input id="fakeTimeOption" type="radio" name="type" value="Use Faketime" <%=(fakeTimeAvailable?"checked=\'true\'":"disabled") %>> Use Faketime</td>
  	  <td><input type="radio" name="type" value=""/ <%=(fakeTimeAvailable?"":"checked=\'true\'") %>>Use DateUtil hack</td></tr>
  <tr><td align="center" colspan="2" id="timeMessage" style="background-color:lightBlue;display:none;"></td></tr>
    <tr><td>Date</td><td><input dojoType="dijit.form.DateTextBox" id="date" name="date"  value="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(curdate)%>" style="width:12em;"/></td></tr>
    <tr><td>Time</td><td><input   id="time" name="time" timePattern='HH:mm:ss' value="T<%=new java.text.SimpleDateFormat("HH:mm:ss").format(curdate)%>" style="width:12em;"/></td></tr>
    <tr><td colspan="2" align="right" ></td></tr>
</table>

</form>
<button onclick="submitTime();">Set Time</button>
</td></tr></table>
<br><br>
    <label for="missedCall">Patient Doc Id  </label><input id="missedCall" type="text"/><button id="missedCallButton">Missed Call</button>
    <br><br>
<div><input type="checkbox" id="mute"></input> Mute audio <span><input type="checkbox" id="symptoms_reporting"></input> Symptoms Reporting call
<input type="checkbox" id="poll_call" ></input> Accept incoming call
</span></div>
<button onclick="$('.optional').toggle(600);">Show / Hide Params</button>
<table id="params">
	<tr><td>Phone Number</td><td><input type="text" id="phone" value="1234567899"/></td><td>(From Tama patient profile)</td></tr>
	<tr><td>PIN</td><td><input type="text" id="pin" value="1111"/></td></tr>
	<tr class="optional"><td>Dosage Id</td><td><input type="text" id="dosage_id" value=""/></td><td>from couch db :motech-pillreminder (uuid something like 1d3d5270-9229-4ff8-95e7-1a93e6c6a6c6)</td></tr>
	<tr class="optional"><td>Regimen Id</td><td><input type="text" id="regimen_id" value=""/></td><td>from couch db :motech-pillreminder (uuid)</td></tr>
	<tr class="optional"><td>Number of call retries</td><td><input type="text" id="times_sent" value="1"/></td><td>Incase of retry calls last call message will be different</td></tr>
	<tr class="optional"><td>Total retries </td><td><input type="text" id="total" value="3"/></td></tr>
	<tr class="optional"><td>call id</td><td><input type="text" id="call_id" value=""/></td></tr>
	<tr class="optional"><td>outbound call?</td><td><input type="checkbox" id="is_outbound_call" value="false"/></td></tr>
    <tr class="optional"><td>outbox call</td><td><input type="checkbox" id="outbox_call" value="false"/></td></tr>
    <tr><td colspan="2"><textarea rows="10" cols="100" id="response"></textarea></td></tr>
</table>
</div>
</body>
</html>