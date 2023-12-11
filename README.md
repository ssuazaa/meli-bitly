# Documentación

### 1. Ejecución de la api:

Seguir los siguientes pasos:

* Clonar repositorio:
  * git clone https://github.com/ssuazaa/meli-bitly.git
* Contar con instalación del motor NoSQL MongoDB, si no se cuenta con una se puede iniciar un contenedor en Docker con este comando:
  * docker run -dti --name mongodb -e MONGO_INITDB_ROOT_USERNAME=short_user -e MONGO_INITDB_ROOT_PASSWORD=1234 -p 27017:27017 mongo:7.0.4-jammy
* Crear base de datos dentro del contenedor:
  * docker exec -ti mongodb /bin/bash
  * mongosh --host localhost --port 27017 -u short_user -p 1234 --authenticationDatabase admin
  * use short_db;
  * db.createCollection('shorten_urls');
  * exit
* Crear archivo .env y enlazar con el ide de preferencia para crear estas variables de entorno, los valores pueden ser:
  * spring.data.mongodb.uri = *mongodb://short_user:1234@localhost:27017/short_db?appName=SHORT_URL_APP&uuidRepresentation=standard* 

  o colocar los valores para las variables de entorno:
  * MONGO_PROTOCOL = Protocolo a usar: [mongodb, mongodb+srv], valor por defecto: *mongodb*.
  * MONGO_USER = Usuario de conexión a la base de datos, valor por defecto: *short_user*.
  * MONGO_PASS = Contraseña de conexión a la base de datos, no tiene valor por defecto y es mandatorio. 
  * MONGO_HOST = Dirección ip o dns de la base de datos, valor por defecto: *localhost:27017*.
  * MONGO_DB = Nombre de la base de datos, valor por defecto: *short_db*
* Con los pasos anteriores la aplicación ya podrá ejecutarse.

### 2. Documentación de la api

Al iniciar la aplicación en local la documentación se puede encontrar en esta url:
* http://localhost:8080/webjars/swagger-ui/index.html#/

También se puede ver la documentación de la api en esta url publica:
* https://meli-bitly-api-nfx63qhysa-uc.a.run.app/webjars/swagger-ui/index.html#/

### 3. Coverage:

Se puede visitar el siguiente enlace para verificar el coverage y otra información de la aplicación:

https://sonarcloud.io/summary/overall?id=ssuazaa_meli-bitly

### 4. Estructura de la base de datos:

La base de datos por defecto tiene el nombre de *short_db* y tiene una colección llamada *shorten_urls*, un documento tiene la siguiente estructura:

```json
{
  "_id": "71166ea7-5972-4d79-8b3a-e53aaaaa52ab",
  "originalUrl": "https://www.google.com.co/es",
  "hash": "14de3a08c71597a5bb6c1",
  "statistics": {
    "creationAt": "2023-12-07T20:42:30.317+00:00",
    "updatedAt": "2023-12-07T20:42:30.317+00:00",
    "amountInteractions": 2
  }
}
```

### 5. Documentación flujos

* 5.1 Crear un nuevo ShortUrl:

Se debe ejecutar el serivicio y enviar el body:

POST -> https://meli-bitly-api-nfx63qhysa-uc.a.run.app/api/v1/shortener

BODY ->
```json
{
  "url": "https://www.google.com.co/es"
}
```

este servicio retorna un estado 201 y un header LOCATION con la url con la que se puede consultar información del ShortUrl, por ejemplo un valor puede ser:

https://meli-bitly-api-nfx63qhysa-uc.a.run.app/api/v1/shortener/hash/14de3a08c71597a5bb6c1

* 5.2 Consultar el ShortUrl:

Teniendo en cuenta la anterior url del header LOCATION, se debe ejecutar el serivicio:

GET -> https://meli-bitly-api-nfx63qhysa-uc.a.run.app/api/v1/shortener/hash/14de3a08c71597a5bb6c1

la respuesta puede tener 2 formas:

200 OK:

RESPONSE ->
```json
{
  "id": "71166ea7-5972-4d79-8b3a-e53aaaaa52ab",
  "originalUrl": "https://www.google.com.co/es",
  "hash": "14de3a08c71597a5bb6c1",
  "statistics": null
}
```

404 NOT_FOUND:

RESPONSE ->
```json
{
  "key": "SHORT_URL_NOT_FOUND",
  "message": "Short with hash '14de3a08c71597a5bb6c1' was not found",
  "dateTime": "2023-12-11T16:47:34.053768437"
}
```

Al encontrarse el hash en base de datos incrementara la cantidad de visitas y una aplicación front puede ejecutar alguna logica para re-dirigir a la "originalUrl" por ejemplo como bitly que espera 5 segundos.

* 5.3 Consultar todos los ShortUrl con sus estadísticas:

Se debe ejecutar el serivicio:

GET -> https://meli-bitly-api-nfx63qhysa-uc.a.run.app/api/v1/shortener

RESPONSE ->
```json
[
  {
    "id": "71166ea7-5972-4d79-8b3a-e53aaaaa52ab",
    "originalUrl": "https://www.google.com.co/es",
    "hash": "14de3a08c71597a5bb6c1",
    "statistics": {
      "creationAt": "2023-12-07T20:42:30.317",
      "updatedAt": "2023-12-11T16:46:38.475", 
      "amountInteractions": 3
    }
  }
]
```