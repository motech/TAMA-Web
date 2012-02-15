dojo.require("dojox.grid.DataGrid");
dojo.require("dojo.data.ItemFileWriteStore");


var DailyPillReminderReportWidget = function(reportName){
    this.targetDivId = reportName + "Placeholder";
    this.placeholderElement = dojo.byId(this.targetDivId);

    this.defaultDisplay = this.placeholderElement.style.display;
    this.noticeBanner = new Banner(reportName + "Notice");
    this.grid = new dojox.grid.DataGrid({id: reportName, structure: this.columnDetails(), autoHeight: 20, autoWidth: true}, document.createElement('div'));
}

DailyPillReminderReportWidget.prototype = {
    fetchData : function(startDate, endDate, onLoadHandler, onCompleteHandler, onErrorHandler){
        var self = this;
        dojo.xhrGet({
            url: "reports/dailyPillReminderReport",
            content: {startDate: new DateFormatter(startDate).slashFormat(), endDate: new DateFormatter(endDate).slashFormat()},
            handleAs: "json",
            load: function(data, ioArgs) { if(typeof(onLoadHandler) == "function") onLoadHandler(data); },
            error: function(err, ioArgs) { self.noticeBanner.setMessage(err); },
            handle: function(){ if(typeof(onCompleteHandler) == "function") onCompleteHandler(); }
        });
    },

    create: function(startDate, endDate, onCreateHandler){
        this.noticeBanner.hide()
        var self = this;
        var onLoadHandler = function(JSONData){
            if (JSONData.logs.length == 0 ) {
                self.noticeBanner.setMessage("No Data to display");
                self.hide();
                return;
            }

            self.render(JSONData);
        };

        this.fetchData(startDate, endDate, onLoadHandler, onCreateHandler);
    },

    render: function(JSONData){
        this.show();
        var data = { identifier: 'id', items: [] };
        data.items = this.prepareLogs(JSONData.logs);

        this.grid.setStore(new dojo.data.ItemFileReadStore({'data': data}));
        dojo.byId(this.targetDivId).appendChild(this.grid.domNode);
        this.grid.startup();
    },

    prepareLogs: function(logs){
        return logs.map(function(log, index) {
            log.id = index;
            log.date = new DateFormatter(new Date(log.date)).slashFormat();
            return log;
        });
    },

    hide: function(){
        this.placeholderElement.style.display = "none";
    },

    show: function(){
        this.placeholderElement.style.display = this.defaultDisplay;
    },

    columnDetails : function(){
        return [[
            {name: 'Date',                  field: 'date',             width: '150px'},
            {name: 'Morning Dose Time',     field: 'morningDoseTime',  width: '150px'},
            {name: 'Morning Adherence',     field: 'morningDoseStatus',width: '150px'},
            {name: 'Evening Dose Time',     field: 'eveningDoseTime',  width: '150px'},
            {name: 'Evening Adherence',     field: 'eveningDoseStatus',width: '150px'}
        ]];
    }
}
