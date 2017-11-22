var chart = new tauCharts.Chart({
  data: datasource,
  type: 'bar',
  x: 'time',
  y: 'count'
});

chart.renderTo('#bargraph');
