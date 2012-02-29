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
