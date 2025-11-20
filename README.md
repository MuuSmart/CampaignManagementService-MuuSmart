# Campaign Management Microservice

Microservicio de gestión de campañas para **MuuSmart Ganadería**, parte del sistema de gestión ganadera.

Swagger UI disponible en: `http://localhost:8081/api/v1/swagger-ui/index.html`

## Descripción

Este microservicio gestiona todas las operaciones relacionadas con campañas para establos ganaderos. Implementa principios de arquitectura limpia, SOLID y patrones de diseño empresariales.

## Características

- ✅ Crear, leer, actualizar y eliminar campañas
- ✅ Gestión de objetivos (Goals) asociados a campañas
- ✅ Gestión de canales de distribución (Channels)
- ✅ Autenticación JWT
- ✅ Autorización basada en roles
- ✅ Documentación Swagger/OpenAPI
- ✅ Validación de datos
- ✅ Manejo centralizado de excepciones

## Requisitos Previos

- Java 17 o superior (Usar JDK 17)
- Maven 3.8.0 o superior
- MySQL 8.0 o superior
- IntelliJ IDEA (recomendado)
- Postman o cualquier cliente REST para pruebas

## Configurar las variables de entorno

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campaign_management_db
spring.datasource.username=root
spring.datasource.password=your_password
//jwt.secret=your-secret-key-change-this-in-production
