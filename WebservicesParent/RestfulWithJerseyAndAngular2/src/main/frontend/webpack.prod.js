var webpack = require('webpack');
var webpackMerge = require('webpack-merge');
var ExtractTextPlugin = require('extract-text-webpack-plugin');
var commonConfig = require('./webpack.common.js');
var helpers = require('./helpers');
var UglifyJSPlugin = require('uglifyjs-webpack-plugin');

const ENV = process.env.NODE_ENV = process.env.ENV = 'production';

module.exports = webpackMerge(commonConfig, {

	output: {
    path: 'dist',
    publicPath: './',
    filename: '[name].js',
    chunkFilename: '[id].chunk.js'
  },
  
  plugins: [
	  new UglifyJSPlugin({
		  output: {
			comments: false,
			beautify: false
		  }
	  })
  ]
});