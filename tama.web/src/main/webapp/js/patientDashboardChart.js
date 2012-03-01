dojo.require("dojox.charting.Chart2D");
dojo.require("dojox.charting.axis2d.Default");
dojo.require("dojox.charting.plot2d.Lines");
dojo.require("dojox.charting.plot2d.Areas");
dojo.require("dojox.charting.themes.ThreeD");
dojo.require("dojox.charting.plot2d.Markers");
dojo.require("dojox.charting.widget.Legend");

//Please include tamaChart.js before this file.
var PatientDashboardChart = function(chartName, dataUrl, chartTitle, tooltipPrefix, dangerZoneRangeFunction){
    this.targetElement = dojo.byId(chartName + "Placeholder");
    this.noticeBanner = new Banner(chartName + "Notice");
    this.dataUrl = dataUrl;
    this.chartTitle = chartTitle;
    this.tooltipPrefix = tooltipPrefix;
    this.dangerZoneRangeFunction = dangerZoneRangeFunction;
    this.chartRenderer = new tama.Chart2D(chartName);
    this.theme = new dojox.charting.Theme({marker:{ symbol:"m0,-7 7,7 -7,7 -7,-7 z"}}); // diamond

    this.defaultDisplay = this.targetElement.style.display;
    this.hide();
    this.noticeBanner.hide()
}

PatientDashboardChart.prototype = {
    fetchData : function(onLoadHandler, onCompleteHandler, onErrorHandler){
        var self = this;
        dojo.xhrGet({
            url: this.dataUrl,
            handleAs: "json",
            load: function(data, ioArgs) { if(typeof(onLoadHandler) == "function") onLoadHandler(data); },
            error: function(err, ioArgs) { self.noticeBanner.setMessage(err); },
            handle: function() { if(typeof(onCompleteHandler) == "function") onCompleteHandler(); }
        });
    },

    draw: function(onCreateHandler){
        var self = this;
        this.fetchData(function(jsonData){
            self.render(jsonData)
        });
    },

    render: function(jsonData){
        if(jsonData.length == 0){
            this.noticeBanner.setMessage("No Data recorded for this patient");
            return;
        }

        this.show();
        var self = this;
        var chartData = new ChartData(jsonData);

        this.addPlot(chartData, "Markers", "default");
        this.addSeries("Lab result", chartData.yValues());
        this.addTooltip(function (el) { return self.tooltipPrefix + " " + el.run.data[el.index]; });

        this.plotDangerZone(jsonData);
        this.chartRenderer.render();
    },

    addPlot: function(chartData, chartType, plotName) {
        this.plotName = plotName;
        this.chartRenderer.addPlot(plotName, {type: chartType });
        this.chartRenderer.setTheme(this.theme);

        this.chartRenderer.addAxis("x", this.xAxisOptions(chartData));
        this.chartRenderer.addAxis("y", this.yAxisOptions(chartData));
    },

    addSeries: function(seriesName, yValues, color, fillColor) {
        color = color == undefined ? "blue" : color;
        fillColor = fillColor == undefined ? "lightblue" : fillColor;
        this.chartRenderer.addSeries(seriesName, yValues, {stroke: {'color': color}, fill: fillColor });
    },

    addTooltip: function(customTextFunction){
        return new dojox.charting.action2d.Tooltip(this.chartRenderer, this.plotName, {"text": customTextFunction});
    },

    plotDangerZone: function(jsonData){
        if (typeof(this.dangerZoneRangeFunction) == "function") {
            this.chartRenderer.addPlot("danger", {type:"Areas"});
            var dangerZone = this.dangerZoneRangeFunction(jsonData);
            this.chartRenderer.addSeries("Danger Range", dangerZone, {plot: "danger", stroke: {color:"white"}, fill: "rgba(255,0,0,0.7)"});
        }
    },

    hide: function(){
        this.targetElement.style.display = "none";
    },

    show: function(){
        this.targetElement.style.display = this.defaultDisplay;
    },

    xAxisOptions : function(chartData){
        return {labels: chartData.xLabels(),
                majorTicks: true,
                majorTickStep: 1,
                minorTicks: false,
                max: chartData.length() + 0.10,
                min: 0.98,
                rotation: -50 }
    },

    yAxisOptions : function(chartData) {
        return {min:0,
                max: chartData.maxY() + 7,
                title: this.chartTitle,
                vertical:true }
    }

}

var ChartData = function(jsonData){
    this.data = jsonData;
}

ChartData.prototype = {
    xLabels : function () {
        return this.data.map(function (elt, index) {
            return {value:index + 1, text:elt.date}
        });
    },

    yValues : function () {
        return this.data.map(function (elt, index) {
            return parseFloat(elt.value);
        });
    },

    maxY : function() {
        return Math.max.apply(Math, this.data.map(function(elt) { return elt.value} ));
    },

    length: function() {
        return this.data.length;
    }
}
