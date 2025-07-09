const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    /*app.use(
        createProxyMiddleware({
            target: 'http://localhost:8080',
            changeOrigin: true,
            pathFilter: '/api',
        }),
    );*/

    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://localhost:8080',
            changeOrigin: true,
        }),
    );
    app.use(
        '/ws',
        createProxyMiddleware({
            target: 'http://localhost:8080',
            changeOrigin: true,
            ws: true,
        }),
    );
};