
var timelineDAY = 4;
var timelineWEEK = 5;
var timelineMONTH = 6;  //from date-time.js of simile timeline widget.

var SymptomsReportingWidget = function(targetDivId, timelineGraphDivId, noDataDivId, dataURL){
    this.targetDivId = targetDivId;
    this.timelineGraphDivId = timelineGraphDivId;
    this.noDataDivId = noDataDivId;
    this.dataURL = dataURL;
    this.setupButtons();
};

SymptomsReportingWidget.prototype = {
    fetchData: function(onLoad){
        var self = this;
        dojo.xhrGet({
            url: self.dataURL,
            handleAs: "json",
            load: function(json, ioArgs) {
                self.data = json;
                onLoad();
            },
            error: function(result, args) {
            }
        });
    },

    setupButtons: function(){
        var self = this;

        dojo.connect(dojo.byId("timeline-chart-in-days"), "onclick", function() {self.drawTimelineChart(timelineDAY)});
        dojo.connect(dojo.byId("timeline-chart-in-weeks"), "onclick", function() {self.drawTimelineChart(timelineWEEK)});
        dojo.connect(dojo.byId("timeline-chart-in-months"), "onclick", function() {self.drawTimelineChart(timelineMONTH)});
    },

    draw : function(interval){
        var self = this;
        this.fetchData(function(){
            if(self.data.events.length > 0) {
                dojo.byId(self.targetDivId).style.display = '';
                dojo.byId(self.noDataDivId).style.display = 'none';
                self.drawTimelineChart(interval);
            }
        });
    },

    drawTimelineChart: function(interval){
        var self = this;
        var eventSource = new Timeline.DefaultEventSource(0);

        var bandInfos = [Timeline.createBandInfo({
                            eventSource : eventSource,
                            width : "80%",
                            intervalUnit : interval,
                            intervalPixels : self.getIntervalPixels(interval)
                        }), Timeline.createBandInfo({
                            eventSource : eventSource,
                            width : "20%",
                            intervalUnit : timelineMONTH,
                            intervalPixels : 300,
                            overview : true
                        })];

        bandInfos[1].syncWith = 0;
        bandInfos[1].highlight = true;

        Timeline.create(dojo.byId(this.timelineGraphDivId), bandInfos);
        eventSource.loadJSON(this.data, this.dataURL);
    },

    getIntervalPixels: function(interval){
        if (interval == timelineDAY) return 50;
        if (interval == timelineWEEK) return 150;
        if (interval == timelineMONTH) return 700;
        return 200;
    }
}
