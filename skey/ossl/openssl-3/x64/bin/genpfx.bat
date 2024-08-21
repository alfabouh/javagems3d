openssl genpkey -algorithm RSA -out gen/private.key -aes256
openssl req -new -key gen/private.key -out gen/request.csr
openssl x509 -req -days 365 -in gen/request.csr -signkey gen/private.key -out gen/certificate.crt
openssl pkcs12 -export -out gen/certificate.pfx -inkey gen/private.key -in gen/certificate.crt
