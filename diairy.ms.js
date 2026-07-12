const http = require('http');
const fs = require('fs');

http.createServer((req, res) => {
  if (req.method === 'POST' && req.url === '/pages') {
    let body = '';
    req.on('data', chunk => body += chunk);
    req.on('end', () => {
      fs.writeFileSync('./livre-pages.json', body);
      res.end('OK');
    });
  }
  else if (req.method === 'GET' && req.url === '/pages') {
    const data = fs.existsSync('./livre-pages.json')
      ? fs.readFileSync('./livre-pages.json', 'utf8')
      : '[]';
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(data);
  }
  else {
    res.statusCode = 404;
    res.end();
  }
}).listen(3000, () => console.log('API sur http://localhost:3000'));