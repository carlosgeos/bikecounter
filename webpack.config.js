var path = require('path');
var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");

module.exports = {
  entry: './site/scripts/index.js',
  output: {
    path: path.resolve(__dirname, 'resources/public'),
    filename: '[hash]-bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        include: [path.resolve(__dirname, "site/scripts")],
        /* include babel-loader and preset options for ES2015 if needed ! */
      },
      /* {
         test: /\.css$/,
         use: [ 'style-loader', 'css-loader' ]
       * },*/
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
      },
      {
        test: /\.(png|jpg|jpeg|gif|svg)$/,
        loader: 'url-loader',
        options: {
          limit: 8192,
          name: 'images/[hash]-[name].[ext]',
        }
      },
      {
        test: /\.(eot|otf|ttf|woff|woff2)$/,
        loader: 'url-loader',
        options: {
          limit: 4096,
          name: 'fonts/[hash]-[name].[ext]',
        }
      }]
  },
  /* resolve makes it easier for JS files to look for style files:
     import css from "Styles/filename.scss" and for css files to find
     images, fonts etc */
  resolve: {
    alias: {
      Styles: path.resolve(__dirname, 'site/styles/'),
      Images: path.resolve(__dirname, 'site/img/'),
      Fonts: path.resolve(__dirname, 'site/fonts/')
    }
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: 'Brussels Bikecounter',
      template: 'site/index.html',
      filename: 'index.html'
    }),
    new ExtractTextPlugin({
      filename: "[contenthash]-main.css",
      /* disable: process.env.NODE_ENV === "development"*/
    }),
    new webpack.optimize.OccurrenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin(),
  ]
};
