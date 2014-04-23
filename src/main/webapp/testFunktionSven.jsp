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
        		t.addColumn("wie auch immer",3.4,5.8,2.9);
        		t.addColumn("yeah",1.5,8.6,3.7);
        		out.println(t);
        		
        		%>
        		
        		
        );

        var options = {
          title: 'Company Performance',
          hAxis: {title: 'Year', titleTextStyle: {color: 'red'}}
        };

        var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
        chart.draw(data, options);
      }
    </script>
  </head>
  <body>
    <div id="chart_div" style="width: 900px; height: 500px;"></div>
  </body>
</html>