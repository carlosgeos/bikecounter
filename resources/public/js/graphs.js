/* var data = d3.range(1000).map(d3.randomBates(10));*/

var data = [0.5, 0.6, 0.4, 0.5];
var formatCount = d3.format(",.0f");

var svg = d3.select("svg"),
    margin = {top: 10, right: 30, bottom: 30, left: 30},
    width = +svg.attr("width") - margin.left - margin.right,
    height = +svg.attr("height") - margin.top - margin.bottom,
    g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");
var x = d3.scaleLinear()
          .rangeRound([0, width]);
var bins = d3.histogram()
             .domain(x.domain())
             .thresholds(x.ticks(20))
(data);

var y = d3.scaleLinear()
          .domain([0, d3.max(bins, function(d) { return d.length; })])
          .range([height, 0]);

var bar = g.selectAll(".bar")
           .data(bins)
           .enter().append("g")
           .attr("class", "bar")
           .attr("transform", function(d) { return "translate(" + x(d.x0) + "," + y(d.length) + ")"; });

bar.append("rect")
   .attr("x", 1)
   .attr("width", x(bins[0].x1) - x(bins[0].x0) - 1)
   .attr("height", function(d) { return height - y(d.length); });/* Esto añade graficamente las barras */

bar.append("text")/* anchor = text */
   .attr("dy", ".75em")
   .attr("y", 6)
   .attr("x", (x(bins[0].x1) - x(bins[0].x0)) / 2)
   .attr("text-anchor", "middle")
   .text(function(d) { return formatCount(d.length); });/* Esto añade muchos ceros abajo que no hacen nada */

g.append("g")
 .attr("class", "axis axis--x")
 .attr("transform", "translate(0," + height + ")")
 .call(d3.axisBottom(x)); // Esto es el 0.1, 0.2, 0.3... y la linea de abajo

/*
 *
 * var parseDate = d3.timeParse("%m/%d/%Y %H:%M:%S %p"),
 *     formatCount = d3.format(",.0f");
 *
 * var margin = {top: 10, right: 30, bottom: 30, left: 30},
 *     width = 960 - margin.left - margin.right,
 *     height = 500 - margin.top - margin.bottom;
 *
 * var x = d3.scaleTime()
 *           .domain([new Date(2015, 0, 1), new Date(2016, 0, 1)])
 *           .rangeRound([0, width]);
 *
 * var y = d3.scaleLinear()
 *           .range([height, 0]);
 *
 * var histogram = d3.histogram()
 *                   .value(function(d) { return d.date; })
 *                   .domain(x.domain())
 *                   .thresholds(x.ticks(d3.timeWeek));
 *
 * var svg = d3.select("#histo").append("svg")
 *             .attr("width", width + margin.left + margin.right)
 *             .attr("height", height + margin.top + margin.bottom)
 *             .append("g")
 *             .attr("transform", "translate(" + margin.left + "," + margin.top + ")");
 *
 * svg.append("g")
 *    .attr("class", "axis axis--x")
 *    .attr("transform", "translate(0," + height + ")")
 *    .call(d3.axisBottom(x));*/
