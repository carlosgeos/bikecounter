const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');


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
        loader: "babel-loader",
        options: {
          presets: ["es2015"]
          /* newer option -> use babel-preset-env */
        }
        /* include babel-loader and preset options for ES2015 if needed ! */
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
        loader: 'file-loader',
      },
    ]
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
    new CleanWebpackPlugin([path.resolve(__dirname, 'resources/public')]),
    new HtmlWebpackPlugin({
      title: 'Brussels Bikecounter',
      template: 'site/index.html',
      filename: 'index.html'
    }),
  ]
};
