## Blog API Spring boot

This is a basic blog app api created using Spring boot. Idea of this was to experiment Spring framework and gain overall knowledge of the ecosystem and creating web apps using the variety of Beans. 

Main things used in this project:

- Spring Boot as framework on top of Java
- Spring Data JPA (Jakarta Persistence) as ORM for different types of data and using CRUD actions with built-in implementations.
- JWT token authentication
- Layered architecture (Controller, Service, Repository, Entity)

This app also has proper service and integration tests implemented with JUnit. Goal was to write the app with TDD principles.

### Starting

```
./mvnw spring-boot:run
```

### Run Tests

```
./mvnw test
```

### TODO:

- Add better support for adding/using tags in other entities.
- Add a separate entity for an organization which could have many authors as member of that entity.
- Add public routes for a practical example in where the web app's frontend might have public information regarding blogs which does not require authentication (more like a thing to consider if this would be a part of a real full stack project).
- Add a separate frontend to demonstrate application functionality in practice.
- Make a proper documentation of all REST-endpoints with Swagger, OpenAPI, etc.


Author: Joni Niemelä
