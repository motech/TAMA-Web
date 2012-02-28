dojo.require("dojox.charting.Chart2D");
dojo.require("dojox.charting.axis2d.Default");
dojo.require("dojox.charting.plot2d.Lines");
dojo.require("dojox.charting.plot2d.Areas");
dojo.require("dojox.charting.themes.ThreeD");
dojo.require("dojox.charting.plot2d.Markers");

var PatientDashboardChart = function(chartName, dataUrl, tooltipPrefix, dangerZoneRangeFunction){
    this.targetElement = dojo.byId(chartName);
    this.placeholderElement = dojo.byId(chartName + "Placeholder");
    this.noticeBanner = new Banner(chartName + "Notice");
    this.dataUrl = dataUrl;
    this.tooltipPrefix = tooltipPrefix;
    this.dangerZoneRangeFunction = dangerZoneRangeFunction;
    this.chartRenderer = new dojox.charting.Chart2D(chartName);
    this.theme = new dojox.charting.Theme({marker:{ symbol:"m0,-7 7,7 -7,7 -7,-7 z"}}); // diamond

    this.defaultDisplay = this.targetElement.style.display;
    this.hide();
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
        this.noticeBanner.hide()
        this.fetchData(function(jsonData){
            if(jsonData.length == 0){
                self.noticeBanner.setMessage("No Data recorded for this patient");
                return;
            }
            self.render(jsonData)
        });
    },

    render: function(jsonData){
        this.show();
        var self = this;
        var chartData = new ChartData(jsonData);

        this.chartRenderer.addPlot("default", {type:"Lines", markers:true });
        this.chartRenderer.setTheme(this.theme);

        this.chartRenderer.addAxis("x", this.xAxisOptions(chartData));
        this.chartRenderer.addAxis("y", {min:0, max: chartData.maxY() + 7, vertical:true});
        this.chartRenderer.addSeries("Lab result", chartData.yValues(), {stroke: {color: "blue"},
                                                                         fill: "lightblue"
                                                                        });

        new dojox.charting.action2d.Tooltip(this.chartRenderer, "default", {"text":function (el) {
            return self.tooltipPrefix + " " + el.run.data[el.index];
        }});

        this.plotDangerZone(jsonData);
        this.chartRenderer.render();
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
            return parseInt(elt.value);
        });
    },

    maxY : function() {
        return Math.max.apply(Math, this.data.map(function(elt) { return elt.value} ));
    },

    length: function() {
        return this.data.length;
    }
}
