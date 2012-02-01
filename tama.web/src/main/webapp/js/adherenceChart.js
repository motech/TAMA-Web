dojo.require("dojox.charting.Chart2D");

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

var DAILY_ADHERENCE_TYPE = "Daily Adherence Trend"
var WEEKLY_ADHERENCE_TYPE = "Weekly Adherence Trend"

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
        });
    },
}


var AdherenceOverTimeChartWidget = function(targetDivId, adherenceSummaryType){
    this.targetDivId = targetDivId;
    this.adherenceSummaryType = adherenceSummaryType;
}

AdherenceOverTimeChartWidget.prototype = {
    draw : function(adherencePerWeekData){
        this.chartRenderer = new tama.Chart2D(this.targetDivId);
        this.chartRenderer.addPlot("default", {type: "Columns", gap: 5, minBarSize: 10, maxBarSize: 50});

        this.chartRenderer.addAxis("x", this.xAxisOptions(adherencePerWeekData));
        this.chartRenderer.addAxis("y", this.yAxisOptions());

        this.chartRenderer.addSeries("Adherence Over Time", adherencePerWeekData.yValues(this.adherenceSummaryType));
        this.resizeAndRenderChart(adherencePerWeekData.totalNumberOfWeeks());
    },

    resizeAndRenderChart: function(numberOfWeeks){
        var chartDiv = dojo.byId(this.targetDivId);
        var width = numberOfWeeks > 20 ? numberOfWeeks * 25 : 800;
        this.chartRenderer.resize(width, 300);
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
                max: 100,
                title: this.adherenceSummaryType + " Chart",
                font: "20px" }
    }
}
