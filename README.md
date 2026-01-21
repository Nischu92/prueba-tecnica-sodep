# Guia de Uso

     _____ ____  _____  ______ _____  
    / ____/ __ \|  __ \|  ____|  __ \ 
   | (___| |  | | |  | | |__  | |__) |
    \___ \| |  | | |  | |  __| |  ___/ 
    ____) | |__| | |__| | |____| |     
   |_____/ \____/|_____/|______|_|     
                                       
    ║█║█║█║█║█║█║█║█║█║█║█║█║█║█║█║
    ║█║█║█║█║  BIENVENIDO  █║█║█║█║
    ║█║█║█║█║█║█║█║█║█║█║█║█║█║█║█║
    ║█║May the Force be with you║█║
    ║█║█║ Sodep Software Team ║█║█║
    ╠═════════════════════════════╣
    ║ ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ║
    ║ ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ║
    ║ ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ▓▓  ║
    ╠═════════════════════════════╣
    ║  Nicolai Alexander Schueler ║
    ╚═════════════════════════════╝


# Pasos para ejecutar el microservicio

# 1. Iniciar PostgreSQL con Docker

docker-compose up -d

Esto iniciará PostgreSQL en el puerto 5433 con las credenciales:
- Database: `prueba_tecnica`
- User: `postgres`
- Password: `postgres`

Para este paso es necesario tener tanto docker como docker-compose instalado.
Asegurate de elegir mappear el puerto externo (en el docker-compose.yml file) a un puerto libre localmente.

### 2. Verificar que el archivo .env existe

El archivo `.env` ya está creado con valores de ejemplo.

### 3. Compilar el proyecto

mvn clean install

### 4. Ejecutar la aplicación

mvn spring-boot:run

La aplicación se iniciará en: http://localhost:8080

### 5. Probar los endpoints

#### a) Sincronizar clientes desde la API externa (JSONPlaceholder)

curl -X POST http://localhost:8080/api/clientes/sync

Esto traerá 10 usuarios de JSONPlaceholder y los guardará en la base de datos

#### b) Listar todos los clientes

curl http://localhost:8080/api/clientes

#### c) Obtener un cliente específico

curl http://localhost:8080/api/clientes/1

#### d) Crear un nuevo cliente manualmente

curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Perez",
    "email": "juan.perez@example.com",
    "telefono": "595-21-123456",
    "direccion": "Asuncion, Paraguay"
  }'

#### e) Actualizar un cliente

curl -X PUT http://localhost:8080/api/clientes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Perez Actualizado",
    "email": "juan.perez.nuevo@example.com",
    "telefono": "595-21-654321",
    "direccion": "Lambare, Paraguay"
  }'

#### f) Eliminar un cliente

curl -X DELETE http://localhost:8080/api/clientes/1

## Troubleshooting

### Error: "Connection refused" a PostgreSQL

Verificar que Docker esté ejecutándose:
docker ps

Deberías ver el contenedor `postgres-sodep` corriendo.

### Error: "Port 5432 is already allocated"

Ya tienes PostgreSQL corriendo localmente. Opciones:
1. Detener tu PostgreSQL local
2. Cambiar el puerto en `docker-compose.yml` y `application.properties` o `.env`

### Ver logs de PostgreSQL

docker logs postgres-sodep

### Verificar la base de datos

Podemos revisar la base de datos directamente desde el contenedor
docker exec -it postgres-sodep psql -U postgres -d prueba_tecnica

Podemos revisar la base de datos desde dbeaver u otro IDE


## Seguridad del Token

# Diccionario
El microservicio utiliza 2 tokens:
  - EXTERNAL_API_TOKEN: Token utilizado para llamar la api externa. Actualmente el endpoint de la API
  consumida no necesita token, por lo tanto se hace un bypass pero se argega al header de la peticion http.
  - INTERNAL_API_TOKEN: Token utilizado para las llamadas internas hacia el microservicio. Actualmente se utiliza
  un JWT generado de data de ejemplo de un usuario ('nschuler') con algoritmo HS256 y se hace una comparacion directa
  con el mismo, sin embargo, se recomienda aplicar un directo JWT parser y manejo de sesiones, idealmente con una
  base de datos de registro como lo es redis.

## Flujo Base
- El usuario envía una petición HTTP con el header `Authorization: Bearer <token>`.
- El `SecurityFilter` intercepta todas las peticiones (excepto `/api/clientes/sync`) -> Endpoint utilizado para sincronizar los registros existentes desde la api.
- Si el token coincide con el valor configurado en `.env`, la petición pasa al controlador (Aqui se podria agregar unos controles de permisos).
- Si el token es inválido o falta, se responde con `401 Unauthorized`.
- Se genera una autenticación interna en el contexto de Spring Security permitiendo el acceso a los endpoints protegidos.
- El controlador accede a los servicios y la base de datos normalmente.

