dojo.require("dojox.charting.Chart2D");
dojo.require("dojox.charting.action2d.Tooltip");

// This method is copied from http://www.reigndropsfall.net/2010/08/12/dojox-charting-axis-titles/
dojo.declare("tama.Chart2D", dojox.charting.Chart2D, {
	render: function(){
		this.inherited(arguments);
		var axes = this.axes;
		var	theme_tick = this.theme.axis.tick;
		var	theme_font = theme_tick.font;
		var	theme_font_color = theme_tick.fontColor;
		var	dim = this.dim;
		var	offsets = this.offsets;
		var	x_middle = (dim.width / 2) + (offsets.l / 2);
		var	y_middle = (dim.height / 2) - (offsets.b / 2);
		var	m = dojox.gfx.matrix;

		// For each axis defined, loop through, check if there
		// is a 'title' property defined.
		for(var i in axes){
			var axis = axes[i];
			if(axis.opt.title){
				var x, y, rotate = 0;

				// If the axis is vertical, rotate it
				if(axis.vertical){
					rotate = 270;
					y = y_middle;
					x = 30;
				}else{
					x = x_middle;
					y = dim.height - 2;
				}

				// Render the text in the middle of the chart
				var elem = axis.group.createText({
					x: x_middle,
					y: y_middle,
					text: axis.opt.title,
					align: 'middle'
				});

				// Set the font and font color
				elem.setFont( axis.opt.font || theme_font )
				    .setFill( axis.opt.fontColor || theme_font_color );

				// If the axis is vertical, rotate and move into position,
				// otherwise just move into position.
				if(rotate){
					elem.setTransform([
						m.rotategAt(rotate, x_middle, y_middle),
						m.translate(0, x - x_middle)
					]);
				}else{
					elem.setTransform(m.translate(0, y - y_middle))
				}
			}
		}
	}
});

var DAILY_ADHERENCE_TYPE = "dailyAdherenceSummary"
var WEEKLY_ADHERENCE_TYPE = "weeklyAdherenceSummary"

var AdherencePerWeekData = function(data){
    this.mergeAndSortData(data);
}

AdherencePerWeekData.prototype = {
    mergeAndSortData: function(data){
        data.dailyAdherenceSummary.forEach(function(elt){ elt.type = DAILY_ADHERENCE_TYPE});
        data.weeklyAdherenceSummary.forEach(function(elt){ elt.type = WEEKLY_ADHERENCE_TYPE});
        var mergedList = data.dailyAdherenceSummary.concat(data.weeklyAdherenceSummary);
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
            return {y: doseValue.taken, color: self.colorFor(doseValue.percentage), 'tooltip': doseValue.tooltip};
        });
    },

    totalDoseValues: function(adherenceSummaryType, tooltipPrefix){
        var self = this;
        return this.sortedList.map(function(elt) {
            var doseValue = self.doseValue(elt, adherenceSummaryType, tooltipPrefix);
            return {y: doseValue.total, 'tooltip': doseValue.tooltip};
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


var AdherenceOverTimeWidget = function(dailyAdherenceOverTimeChart, weeklyAdherenceOverTimeChart, dataUrl, noticeBanner){
    this.dailyAdherenceOverTimeChart = dailyAdherenceOverTimeChart;
    this.weeklyAdherenceOverTimeChart = weeklyAdherenceOverTimeChart;
    this.dataURL = dataUrl;
    this.noticeBanner = noticeBanner;
}

AdherenceOverTimeWidget.prototype = {
    fetchData: function(onLoad){
        dojo.xhrGet({
            url: this.dataURL,
            handleAs: "json",
            load: function(json, ioArgs) { onLoad(json); },
            error: function(result, args) { }
        });
    },

    draw: function(){
        var self = this;
        this.fetchData(function(jsonData){
            var adherencePerWeekData = new AdherencePerWeekData(jsonData);
            if(jsonData.dailyAdherenceSummary.length > 0) self.dailyAdherenceOverTimeChart.draw(adherencePerWeekData);
            if(jsonData.weeklyAdherenceSummary.length > 0) self.weeklyAdherenceOverTimeChart.draw(adherencePerWeekData);
            if(jsonData.dailyAdherenceSummary.length == 0 && jsonData.weeklyAdherenceSummary.length == 0) self.noticeBanner.setMessage("No Adherence Recorded yet");
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
