## Blog API Spring boot

This is a basic blog app api created using Spring boot. Idea of this was to experiment Spring framework and gain overall knowledge of the ecossytem and creating web apps using the variety of Beans. 

Main things used in this project:

- Spring Boot as framework on top of Java
- Spring Data JPA (Jakarta Presence) as ORM for different types of data and using CRUD actions with built-in implementations.
- JWT token authentication
- Layered architecture (Controller, Service, Repository, Entity)

This app also has proper service and integration tests implemented with JUnit. Goal was to write the app with TDD principles.

### Starting

```
./mvnw spring-boot:run
```

### Run Tests
./mvnw test

TODO:

- Add better support for adding tags
- Add public routes for a practical example where the web app's frontend might have public information regarding blogs which does not require authentication (more like a thing to consider if this would be a part of a real full stack project).
- Add a separate frontend to demonstrate application functionality in practice.


Author: Joni Niemelä
