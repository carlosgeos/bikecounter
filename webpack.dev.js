const webpack = require('webpack');
const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
  devtool: "source-map",
  module: {
    rules: [
      {
        test: /\.s?css$/,
        use: [
          {
            loader: "style-loader"/* needed if CSS is going to be embedded in the bundle.js */
          },
          {
            loader: "css-loader", // translates CSS into CommonJS
          },
          {
            loader: "sass-loader", // compiles Sass to CSS
            options: {/* also use "~" in prefix for node_modules stylesheets */
              includePaths: [path.resolve(__dirname, "site/styles")]
            }
          }]
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      API_URL: JSON.stringify("http://localhost:3000"),
    })
  ]
});
