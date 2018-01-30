const path = require('path');
const webpack = require('webpack');
const merge = require('webpack-merge');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const common = require('./webpack.common.js');

module.exports = merge(common, {
  module: {
    rules: [
      {
        test: /\.s?css$/,
        use: ExtractTextPlugin.extract({
          use: [
            {
              loader: "css-loader", // translates CSS into CommonJS
            },
            {
              loader: "sass-loader", // compiles Sass to CSS
              options: {/* also use "~" in prefix for node_modules stylesheets */
                includePaths: [path.resolve(__dirname, "site/styles")]
              }
            }]
        })
      }]
  },
  plugins: [
    new ExtractTextPlugin({
      filename: "[contenthash]-main.css",
    }),
    new UglifyJSPlugin({
      parallel: 4
    }),
    new webpack.DefinePlugin({
      /* Important ! In prod, there is no need for a specific
      port. This value should be http://localhost:3000 in dev to take
      advantage of the auto-reloading webpack server */
      API_URL: JSON.stringify(""),
    })

  ]
});
