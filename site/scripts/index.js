require("purecss/build/pure-min.css");
require("c3/c3.min.css");
import css from 'Styles/main.scss';
import d3 from 'd3';
import c3 from 'c3';
import flatpickr from 'flatpickr';

var now = new Date().toISOString();
var two_days_ago = new Date(new Date().getTime() - (2 * 24 * 60 * 60 * 1000)).toISOString();

var chart = c3.generate({
  bindto: '#chart',
  data: {
    columns: [
      ['data1']
    ]
  }
});

fetch(API_URL + "/api/bikes?start_time=" + two_days_ago + "&end_time=" + now).then(function(response) {
  return response.json();
}).then(function(data) {
  var numbers = data.map(x => x.thishour);
  chart.load({
    columns: [
      ['data1'].concat(numbers)
    ]
  });
});
