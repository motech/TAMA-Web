<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
        <script src="<%=application.getContextPath() %>/resources/dojo/dojo.js" type="text/javascript" djConfig="parseOnLoad: true"  ></script>
        <script type="text/javascript">
            dojo.require("dijit.dijit");
            dojo.require("dijit.form.DateTextBox");
            dojo.require("dijit.form.Button");
            dojo.require("dijit.form.ComboBox");
        </script>
        <script type="text/javascript">
            function sendForm(){
                var form = dojo.byId("backfillForm");
                dojo.connect(form, "onsubmit", function(event){
                    dojo.stopEvent(event);
                    dojo.xhrPost({
                    form:"backfillForm",
                    load: function(data, ioArgs){
                        dojo.byId('message').style.display="";
                        dojo.byId('message').innerHTML = data;
                        setTimeout(function() {
                            dojo.byId('message').style.display="none";
                        }, 15000);
                    },

                    error: function(err, ioArgs){
                        dojo.byId('message').style.display = err;
                    }
                    });
                });
            }

            dojo.ready(sendForm);
        </script>

        <link rel="stylesheet" type="text/css" href="<%=application.getContextPath() %>/resources/dijit/themes/tundra/tundra.css" />
        <style>
            .dijitPopup {
                background-color: #bbeeff;
                border: 0 none;
                margin: 0;
                padding: 10px;
                position: absolute;
            }
            .dijitPopup thead {
                background-color: #447799;
                color: white;
            }
        </style>
    </head>
    <body>
        <form id="backfillForm" action="<%=application.getContextPath() %>/tama-tools/backfill/daily" method="POST">
            <h4 align="center" style="background-color:lightBlue;width:400px;">Backfill Adherence</h4>
            <table>
                <%  java.util.Date today = new java.util.Date(); %>
                <tr><td align="center" colspan="2" style="background-color:lightBlue;display:none;"></td></tr>
                <tr><td align="center" colspan="2" id="message" style="background-color:lightBlue;display:none;"></td></tr>
                <tr><td>Patient Document ID</td><td><input dojoType="dijit.form.TextBox" id="patientDocId" name="patientDocId"  value="" style="width:12em;"/></td></tr>
                <tr><td>Dosage Status</td><td>
                    <select data-dojo-type="dijit.form.ComboBox" id="dosageStatus" name="dosageStatus">
                        <option selected>NOT_RECORDED</option>
                        <option>NOT_TAKEN</option>
                        <option>TAKEN</option>
                        <option>WILL_TAKE_LATER</option>
                    </select>
                </td></tr>
                <tr><td>Start Date</td><td><input dojoType="dijit.form.DateTextBox" id="fromDateString" name="fromDateString"  value="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(today)%>" style="width:12em;"/></td></tr>
                <tr><td>Till Date</td><td><input dojoType="dijit.form.DateTextBox" id="toDateString" name="toDateString"  value="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(today)%>" style="width:12em;"/></td></tr>
                <tr><td colspan="2"> <button type="submit" data-dojo-type="dijit.form.Button" id="submit">Backfill Daily Adherence</button></td></tr>
            </table>
        </form>
    </body>
</html>