const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {

    // We default backend proxy to localhost. This can be changed by
    // setting the REACT_APP_PROXY environment variable.
    let backend = (process.env.REACT_APP_PROXY)? process.env.REACT_APP_PROXY : 'http://localhost:8080';
  
    app.use(
    '/api',
    createProxyMiddleware({
      target: backend,
      changeOrigin: true,
    })
  );
};