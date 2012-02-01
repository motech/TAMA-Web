dojo.require("dojox.charting.Chart2D");

var AdherencePerWeekData = function(data){
    this.mergeAndSortData(data);
}

AdherencePerWeekData.prototype = {
    mergeAndSortData: function(data){
        data.dailyAdherenceSummary.forEach(function(elt){ elt.type = "dailyAdherenceSummary"});
        data.weeklyAdherenceSummary.forEach(function(elt){ elt.type = "weeklyAdherenceSummary"});
        var mergedList = data.dailyAdherenceSummary.concat(data.weeklyAdherenceSummary);
        this.sortedList = mergedList.sort(function(eltA, eltB){ return (eltA.date > eltB.date) ? 1 : -1; });
    },

    xLabels: function(){
        var dateFormatter = function(dateString) {
            var date = new Date(dateString);
            return date.toString("MMM d yyyy");
        };
        return this.sortedList.map(function(elt, index) { return {value: index + 1, text: dateFormatter(elt.date)}});
    },

    yValues: function(adherenceSummaryType){
        var self = this;
        return this.sortedList.map(function(elt) {
            var percentage = (elt.type == adherenceSummaryType) ? elt.percentage : 0;
            return {y: percentage, color: self.colorFor(percentage)};
        });
    },

    colorFor: function(percentage){
        if(percentage >= 90 ) return "green";
        if(percentage >= 70 ) return "orange";
        return "red";
    },

    totalNumberOfWeeks: function(){
        return this.sortedList.length;
    }
}


var AdherenceOverTimeWidget = function(dailyAdherenceOverTimeChart, weeklyAdherenceOverTimeChart, dataUrl){
    this.dailyAdherenceOverTimeChart = dailyAdherenceOverTimeChart;
    this.weeklyAdherenceOverTimeChart = weeklyAdherenceOverTimeChart;
    this.dataURL = dataUrl;
}

AdherenceOverTimeWidget.prototype = {
    fetchData: function(onLoad){
        dojo.xhrGet({
            url: this.dataURL,
            handleAs: "json",
            load: function(json, ioArgs) { onLoad(new AdherencePerWeekData(json)); },
            error: function(result, args) { }
        });
    },

    draw: function(){
        var self = this;
        this.fetchData(function(adherencePerWeekData){
            self.dailyAdherenceOverTimeChart.draw(adherencePerWeekData);
            self.weeklyAdherenceOverTimeChart.draw(adherencePerWeekData);
        });
    },
}


var AdherenceOverTimeChartWidget = function(targetDivId, adherenceSummaryType){
    this.targetDivId = targetDivId;
    this.adherenceSummaryType = adherenceSummaryType;
    this.chartRenderer = new dojox.charting.Chart2D(this.targetDivId);
    this.chartRenderer.addPlot("default", {type: "Columns", gap: 5, minBarSize: 10, maxBarSize: 50});
}

AdherenceOverTimeChartWidget.prototype = {
    draw : function(adherencePerWeekData){
        this.chartRenderer.addAxis("x", this.xAxisOptions(adherencePerWeekData));
        this.chartRenderer.addAxis("y", {vertical:true, min:0, max: 100});
        this.chartRenderer.addSeries("Adherence Over Time", adherencePerWeekData.yValues(this.adherenceSummaryType));
        this.resizeAndRenderChart(adherencePerWeekData.totalNumberOfWeeks());
    },

    resizeAndRenderChart: function(numberOfWeeks){
        var chartDiv = dojo.byId(this.targetDivId);
        var width = numberOfWeeks > 20 ? numberOfWeeks * 25 : parseInt(chartDiv.style.width);
        this.chartRenderer.resize(width, parseInt(chartDiv.style.height));
    },

    xAxisOptions : function(adherencePerWeekData){
        return {labels: adherencePerWeekData.xLabels(),
                majorTicks: true,
                majorTickStep: 1,
                minorTicks: false,
                rotation: -50 }
    }
}
