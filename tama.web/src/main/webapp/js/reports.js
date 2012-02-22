dojo.require("dojox.grid.DataGrid");
dojo.require("dojo.data.ItemFileWriteStore");


var DailyPillReminderReportWidget = function(reportName){
    this.targetElement = dojo.byId(reportName);
    this.gridElement = dojo.byId(reportName + "Placeholder");
    this.excelLinkElement = dojo.byId(reportName + "ExcelLink");
    this.prepareDownloadLink();

    this.defaultDisplay = this.targetElement.style.display;
    this.hide();

    this.noticeBanner = new Banner(reportName + "Notice");
    this.grid = new dojox.grid.DataGrid({id: reportName, structure: this.columnDetails(), autoHeight: 20, autoWidth: true}, document.createElement('div'));
}

DailyPillReminderReportWidget.prototype = {
    fetchData : function(startDate, endDate, onLoadHandler, onCompleteHandler, onErrorHandler){
        var self = this;
        dojo.xhrGet({
            url: "reports/dailyPillReminderReport.json",
            content: {startDate: startDate, endDate: endDate},
            handleAs: "json",
            load: function(data, ioArgs) { if(typeof(onLoadHandler) == "function") onLoadHandler(data); },
            error: function(err, ioArgs) { self.noticeBanner.setMessage(err); },
            handle: function() { if(typeof(onCompleteHandler) == "function") onCompleteHandler(); }
        });
    },

    create: function(startDate, endDate, onCreateHandler){
        this.noticeBanner.hide()
        this.setDates(startDate, endDate);
        var self = this;
        var onLoadHandler = function(JSONData){
            if (JSONData.logs.length == 0 ) {
                self.noticeBanner.setMessage("No Data to display");
                self.hide();
                return;
            }

            self.render(JSONData);
        };

        this.fetchData(this.startDate, this.endDate, onLoadHandler, onCreateHandler);
    },

    render: function(JSONData){
        this.show();
        var data = { identifier: 'id', items: [] };
        data.items = this.prepareLogs(JSONData.logs);

        this.grid.setStore(new dojo.data.ItemFileReadStore({'data': data}));
        this.gridElement.appendChild(this.grid.domNode);
        this.grid.startup();
    },

    prepareLogs: function(logs){
        return logs.map(function(log, index) {
            log.id = index;
            return log;
        });
    },

    prepareDownloadLink: function(){
        var self = this;
        dojo.connect(this.excelLinkElement, "click", function(){
            document.location = "reports/dailyPillReminderReport.xls?startDate=" + self.startDate + "&endDate=" + self.endDate;
        })
    },

    hide: function(){
        this.targetElement.style.display = "none";
    },

    show: function(){
        this.targetElement.style.display = this.defaultDisplay;
    },

    setDates: function(startDate, endDate){
        this.startDate = new DateFormatter(startDate).slashFormat();
        this.endDate = new DateFormatter(endDate).slashFormat();
    },

    columnDetails : function(){
        return [[
            {name: 'Date (yyyy-mm-dd)',     field: 'date',             width: '150px'},
            {name: 'Morning Dose Time',     field: 'morningDoseTime',  width: '150px'},
            {name: 'Morning Adherence',     field: 'morningDoseStatus',width: '150px'},
            {name: 'Evening Dose Time',     field: 'eveningDoseTime',  width: '150px'},
            {name: 'Evening Adherence',     field: 'eveningDoseStatus',width: '150px'}
        ]];
    }
}
