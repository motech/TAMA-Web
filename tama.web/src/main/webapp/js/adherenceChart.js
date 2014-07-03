dojo.require("dojox.charting.Chart2D");
dojo.require("dojox.charting.action2d.Tooltip");

var DAILY_ADHERENCE_TYPE = "dailyAdherenceSummary"
var WEEKLY_ADHERENCE_TYPE = "weeklyAdherenceSummary"

var AdherencePerWeekData = function(data, isDaily){
    this.mergeAndSortData(data, isDaily);
}

AdherencePerWeekData.prototype = {
    mergeAndSortData: function(data, isDaily){
	    var mergedList = null;
		if(isDaily){
        	data.dailyAdherenceSummary.forEach(function(elt){ elt.type = DAILY_ADHERENCE_TYPE});
        	 mergedList = data.dailyAdherenceSummary;
		}else{
        	data.weeklyAdherenceSummary.forEach(function(elt){ elt.type = WEEKLY_ADHERENCE_TYPE});
        	mergedList = data.weeklyAdherenceSummary;
		}
        this.sortedList = mergedList.sort(function(eltA, eltB){ return (eltA.date > eltB.date) ? 1 : -1; });
    },

    xLabels: function(){
        var dateFormatter = function(dateString) {
            var date = new Date(dateString);
            return date.toDateString().replace(/[a-z]* /i,''); //MMM dd yyyy
        };
        return this.sortedList.map(function(elt, index) { return {value: index + 1, text: dateFormatter(elt.date)}});
    },

    doseValue : function(summary, adherenceSummaryType, tooltipPrefix){
        if (summary.type != adherenceSummaryType) return {taken: 0, total: 0, percentage: 0, tooltip: ""};
        var tooltipValue = "" + tooltipPrefix + ":: " + summary.taken + " out of " + summary.total;
        return {taken: summary.taken, total: summary.total, percentage: summary.percentage, tooltip: tooltipValue};
    },

    takenDoseValues: function(adherenceSummaryType, tooltipPrefix){
        var self = this;
        return this.sortedList.map(function(elt) {
            var doseValue = self.doseValue(elt, adherenceSummaryType, tooltipPrefix);
            return {y: doseValue.taken, color: "green", tooltip: doseValue.tooltip};
        });
    },

    totalDoseValues: function(adherenceSummaryType, tooltipPrefix){
        var self = this;
        return this.sortedList.map(function(elt) {
            var doseValue = self.doseValue(elt, adherenceSummaryType, tooltipPrefix);
            return {y: doseValue.total, color: "red", tooltip: doseValue.tooltip};
        });
    },

    totalNumberOfWeeks: function(){
        return this.sortedList.length;
    }
}


var AdherenceOverTimeWidget = function(dailyAdherenceOverTimeChart, weeklyAdherenceOverTimeChart, dataUrl, noticeBanner){
    this.dailyAdherenceOverTimeChart = dailyAdherenceOverTimeChart;
    this.weeklyAdherenceOverTimeChart = weeklyAdherenceOverTimeChart;
    this.dataURL = dataUrl;
    this.noticeBanner = noticeBanner;
}

AdherenceOverTimeWidget.prototype = {
    fetchData: function(onLoad){
        AjaxCall.get({
            url: this.dataURL,
            load: function(json, ioArgs) { onLoad(json); }
        });
    },

    draw: function(){
        var self = this;
        this.fetchData(function(jsonData){
            var adherencePerWeekDataDaily = new AdherencePerWeekData(jsonData, true);
             var adherencePerWeekDataWeekly = new AdherencePerWeekData(jsonData, false);
            if(jsonData.dailyAdherenceSummary.length > 0) self.dailyAdherenceOverTimeChart.draw(adherencePerWeekDataDaily);
            if(jsonData.weeklyAdherenceSummary.length > 0) self.weeklyAdherenceOverTimeChart.draw(adherencePerWeekDataWeekly);
            if(jsonData.dailyAdherenceSummary.length == 0 && jsonData.weeklyAdherenceSummary.length == 0) {
            	self.noticeBanner.setMessage("No Adherence Recorded yet.");
            }
        });
    },
}


var AdherenceOverTimeChartWidget = function(targetDivId, adherenceSummaryType, yLabel, maxY){
    this.targetDivId = targetDivId;
    this.adherenceSummaryType = adherenceSummaryType;
    this.yLabel = yLabel;
    this.maxY = maxY;
}

AdherenceOverTimeChartWidget.prototype = {
    draw : function(adherencePerWeekData){
        var tooltipPrefix = this.yLabel + " taken ";
        var plotName = "default";
        this.chartRenderer = new tama.Chart2D(this.targetDivId);
        this.chartRenderer.addPlot("grid", this.gridOptions());

        this.chartRenderer.addPlot(plotName, {type: "Columns", gap: 5, minBarSize: 10, maxBarSize: 50});
        this.chartRenderer.addAxis("x", this.xAxisOptions(adherencePerWeekData));
        this.chartRenderer.addAxis("y", this.yAxisOptions());

        this.chartRenderer.addSeries("Taken ", adherencePerWeekData.takenDoseValues(this.adherenceSummaryType, tooltipPrefix));
        this.chartRenderer.addSeries("Total", adherencePerWeekData.totalDoseValues(this.adherenceSummaryType, tooltipPrefix));

        new dojox.charting.action2d.Tooltip(this.chartRenderer, plotName);
        this.resizeAndRenderChart(adherencePerWeekData.totalNumberOfWeeks());
        this.displayLegend();
    },

    resizeAndRenderChart: function(numberOfWeeks){
        var chartDiv = dojo.byId(this.targetDivId);
        var width = numberOfWeeks > 20 ? numberOfWeeks * 25 : 800;
        var height = this.maxY > 10 ? 350 : 250;
        this.chartRenderer.resize(width, height);
    },

    displayLegend : function(){
        var legend = dojo.byId(this.targetDivId + "Legend");
        legend.style.display="";
    },

    xAxisOptions : function(adherencePerWeekData){
        return {labels: adherencePerWeekData.xLabels(),
                majorTicks: true,
                majorTickStep: 1,
                minorTicks: false,
                rotation: -50 }
    },

    yAxisOptions : function(){
        return {vertical:true,
                min:0,
                max: this.maxY,
                majorTicks: true,
                majorTickStep: 1,
                minorTicks: false,
                title: this.yLabel,
                font: "20px" }
    },

    gridOptions : function(){
        return {type: "Grid",
            hMajorLines: true,
            hMinorLines: true,
            vMajorLines: false,
            vMinorLines: false }
    }
}