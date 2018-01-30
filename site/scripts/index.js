require("purecss/build/pure-min.css");
require("c3/c3.min.css");
require("flatpickr/dist/flatpickr.css");
import css from 'Styles/main.scss';
import d3 from 'd3';
import c3 from 'c3';
import flatpickr from "flatpickr";

/* var now = new Date().toISOString();
 * var two_days_ago = new Date(new Date().getTime() - (2 * 24 * 60 * 60 * 1000)).toISOString();
 * */
var chart = c3.generate({
  bindto: '#chart',
  data: {
    columns: [
      ['data1']
    ]
  }
});

var range_pickr = flatpickr("#range_pickr", {
  mode: "range",
  onClose: update_timeline
});

function update_timeline(selectedDates, dateStr, instace) {
  var start_time = selectedDates[0].toISOString();
  var end_time = selectedDates[1].toISOString();
  fetch(API_URL + "/api/bikes?start_time=" + start_time + "&end_time=" + end_time).then(function(response) {
    return response.json();
  }).then(function(data) {
    var numbers = data.map(x => x.thishour);
    chart.load({
      columns: [
        ['Bike flow'].concat(numbers)
      ]
    });
  });
}
