dojo.require("dojox.grid.DataGrid");
dojo.require("dojo.data.ItemFileWriteStore");


var DailyPillReminderReportWidget = function(targetDivId){
    this.targetDivId = targetDivId;
    this.grid = new dojox.grid.DataGrid({id: 'dailyPillReminderReportGrid', structure: this.columnDetails(), autoHeight: 20, autoWidth: true}, document.createElement('div'));
}

DailyPillReminderReportWidget.prototype = {
    fetchData : function(startDate, endDate, onLoadHandler){
        dojo.xhrGet({
            url: "reports/dailyPillReminderReport",
            content: {startDate: new DateFormatter(startDate).slashFormat(), endDate: new DateFormatter(endDate).slashFormat()},
            handleAs: "json",
            load: function(data, ioArgs){ onLoadHandler(data); },
            error: function(err,ioArgs){ }
        });
    },

    create: function(startDate, endDate){
        var self = this;
        this.fetchData(startDate, endDate, function(JSONData){
            var data = { identifier: 'id', items: [] };
            data.items = JSONData.logs.map(function(log, index) { log.id = index; return log} );

            self.grid.setStore(new dojo.data.ItemFileReadStore({'data': data}));
            dojo.byId(self.targetDivId).appendChild(self.grid.domNode);
            self.grid.startup();
        });
    },

    columnDetails : function(){
        return [[
            {name: 'Date',                  field: 'date',         width: '150px'},
            {name: 'Morning Dose Time',     field: 'morningTime',  width: '150px'},
            {name: 'Morning Adherence',     field: 'morningStatus',width: '150px'},
            {name: 'Evening Dose Time',     field: 'eveningTime',  width: '150px'},
            {name: 'Evening Adherence',     field: 'eveningStatus',width: '150px'}
        ]];
    }
}
