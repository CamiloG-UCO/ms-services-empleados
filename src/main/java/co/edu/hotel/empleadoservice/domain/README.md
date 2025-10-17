# 🏨 Hotel  - Capa de Dominio (`domain`)

La capa **domain** contiene las **entidades principales** del sistema, que representan las tablas de la base de datos y su estructura de relaciones.  
Aquí se definen las clases que modelan el negocio, aqui un ejemplo de guia como `User` y `Role`.

---

## 📦 Librerías Utilizadas

- **Jakarta Persistence (`jakarta.persistence`)** → Para mapear las entidades con la base de datos (JPA).
- **Lombok (`lombok`)** → Para generar automáticamente getters, setters y otros métodos sin escribir código repetitivo.

---

## 🧩 Ejemplo de Entidad: `User`

```java
package co.uco.hotel.userservice.domain.user;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
