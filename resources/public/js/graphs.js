var chart = new tauCharts.Chart({
  data: datasource,
  type: 'bar',
  x: 'time',
  y: 'count',
  plugins: [
    tauCharts.api.plugins.get('tooltip')()
  ]
});

chart.renderTo('#bargraph');
