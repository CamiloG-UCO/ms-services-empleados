# 🏨 Hotel  - Capa de Repositorios (`repositories`)

La capa **repository** se encarga de la **interacción directa con la base de datos**.  
Aquí se definen las interfaces que permiten realizar operaciones CRUD y consultas personalizadas sobre las entidades del dominio.

---

## 📦 Librerías Utilizadas

- **Spring Data JPA (`org.springframework.data.jpa.repository`)** → Simplifica el acceso a la base de datos y genera automáticamente las operaciones CRUD.
- **Java Util (`java.util.Optional`, `java.util.UUID`)** → Manejo de tipos seguros para evitar `NullPointerException` y uso de identificadores únicos.

---

## 🧩 Ejemplo de Repositorio: `IUserRepository`

```java
package co.uco.hotel.userservice.repositories.user;

import co.uco.hotel.userservice.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
