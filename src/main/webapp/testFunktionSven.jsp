<%@ page import="de.fau.amos.*"%>
<html>
  <head>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable(
        		
        		<% 
        		
        		GoogleTable t=new GoogleTable("Jahr","Text1","testen","so toll....");
        		for(int i=0;i<10;i++){
        			t.addColumn((i+1)+"",Math.random()*10,Math.random()*10,Math.random()*10);
        		}
        		out.println(t);
        		
        		%>
        		
        		
        );

        var options = {
          title: 'Company Performance',
          //curveType: 'function', //for LineChart
          //pieHole: 0.4, //for PieChart, doesn't work with is3D
          is3D: true, //for PieChart

          hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}
        };

        var chart = new google.visualization.<% 
        		out.println("PieChart");
        		//out.println("LineChart"); 
        		//out.println("ColumnChart"); 
				//out.println("AreaChart"); 
				
        		%>(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
  </body>
</html>