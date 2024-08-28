function plotData(jsonData, plotAreaTag, opCountTag, avgOpTimeTag){
			var plotarea = $(plotAreaTag);
			var data=jsonData.opsCompleted;
			var opCount=jsonData.opCount;
			var opPerSecond=jsonData.opPerSecond/1000;
			
			var temp;
			for(var i=0;i<data.length;i++){
				temp = data[i][0];
				data[i][0] = data[i][1];				
				data[i][1] = temp;
			}
			var previousValue = data[0][1];
			for(var i=1;i<data.length;i++){
				temp = data[i][1];
				data[i][1] = (data[i][1]-previousValue);
				previousValue = temp;
			}
			document.getElementById(opCountTag).innerHTML=opCount
			document.getElementById(avgOpTimeTag).innerHTML=opPerSecond
		  	  
			tickFormatter = function(val, axis){
			   return '<span style="font-size:10px">'+(val-250)+"-" + val+'</span>'
			}
			var options = {
				xaxes: [{
					axisLabel: 'operation ID',
					tickSize: 250,
					tickFormatter: tickFormatter,
					axisLabelUseCanvas: true,
					axisLabelPadding: 15,
					tickLength: 0
				}],
				yaxes: [{
					position: 'left',
					axisLabel: 'bar',
					min:0,
					axisLabelUseCanvas: true,
					axisLabel: 'time (s)'
				}], 
				grid: {
					color: '#646464',
					borderColor: 'transparent',
					borderWidth: 2,
					hoverable: true
				},
				series: {
					bars: {
						show: true,
						barWidth: 200,
						align: 'center'
					},
					
					}
				};

			$.plot(plotarea,[{ data: data }]
					,options);
			function showTooltip(x, y, contents) {
				$('<div id="tooltip">' + contents + '</div>').css({
					top: y - 16,
					left: x + 20
				}).appendTo('body').fadeIn();
			}
			 
			var previousPoint = null;
			 
			$(plotAreaTag).bind('plothover', function (event, pos, item) {
				if (item) {
					if (previousPoint != item.dataIndex) {
						previousPoint = item.dataIndex;
						$('#tooltip').remove();
						var	time = item.datapoint[1];
						var msg = "Average call time: "+(time/250)+"ms"
						showTooltip(item.pageX, item.pageY, msg);
					}
				} else {
					$('#tooltip').remove();
					previousPoint = null;
				}
			});
				
				
		   
			
}