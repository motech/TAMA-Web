dojo.require("dojo.parser");
dojo.require("dijit.TooltipDialog");
dojo.require("dijit.form.TimeTextBox");

var SchedulePopup = function(id, titleText){
    this.id=id;
    var calendar = new dijit.Calendar({
                        value: new Date(),
                        isDisabledDate: function(d){
                            var startDate = new Date();
                            startDate.setDate(startDate.getDate() - 1);
                           return startDate.getTime() > d.getTime();
                        }
                    }, dojo.query(".calendar", id)[0]);

    var timebox = new dijit.form.TimeTextBox({name:"timePicker", value:new Date(),
        constraints:{timePattern:'HH:mm:ss', clickableIncrement:'T00:15:00', visibleIncrement:'T00:15:00', visibleRange:'T01:00:00'}
      }, dojo.query(".timePicker", id)[0]);

    var adjustDueDateDlg = new dijit.TooltipDialog({
                        title: titleText,
                        style: "",
                        content: dojo.byId(id)
                    });
    var okHandlerConnect = null;
    var cancelHandlerConnect = null;


    this.show = function(element, showOptions /*bool*/, okHandler) {
        var showTimeOption =  showOptions && showOptions['showTimeOption'];
        var showVisitOptions =  showOptions && showOptions['showVisitOptions'];
        dijit.popup.open({ popup: adjustDueDateDlg,around: element });

        dojo.query('.timePickerDiv', id).style('display', showTimeOption ?'':'none');
        dojo.query('.visitTypeOptions', id).style('display', showVisitOptions ?'':'none');



        if (cancelHandlerConnect == null) {
            cancelHandlerConnect  = dojo.query('.popupCancel',id).connect('onclick', function(){
                dijit.popup.close(adjustDueDateDlg);
            });
        }


        if (okHandlerConnect != null) dojo.disconnect(okHandlerConnect);

        okHandlerConnect = dojo.connect(dojo.query('.popupOk',id)[0], 'onclick',
                function(){
                    var dateValue = calendar.value;
                    if (showTimeOption){
                        dateValue.setHours(timebox.value.getHours());
                        dateValue.getMinutes(timebox.value.getMinutes());
                    }
                    var optionScheduled;
                    if (showVisitOptions) {
                        optionScheduled = {"visitOptions" : {"optionScheduled" :  dojo.query(".optionScheduled")[0].checked }};
                    }
                    okHandler(dateValue, optionScheduled);
                    dijit.popup.close(adjustDueDateDlg);
                });
    }
};
