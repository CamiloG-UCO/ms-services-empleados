# 🏨 Hotel  - Capa de Controladores (`controller`)

Este módulo gestiona la **autenticación y autorización de usuarios** utilizando **JWT (JSON Web Tokens)** junto con **Spring Security**.  
La autorización se basa en **roles asignados a cada usuario**, controlados directamente desde los **controladores (controllers)** mediante anotaciones.

---

## 🔐 Autorización basada en Roles en los Controladores

Cada **controlador** define las restricciones de acceso a sus endpoints mediante la anotación `@PreAuthorize`,  
la cual evalúa los roles del usuario autenticado extraídos del token JWT.

### 🧩 Ejemplo de Lógica del Controlador

```java
package co.uco.hotel.userservice.controllers.user;

import co.uco.hotel.userservice.domain.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // 🔸 Solo los usuarios con rol ADMIN pueden registrar nuevos usuarios
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Lógica de registro de usuario
        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    // 🔸 Solo los usuarios con rol CUSTOMER o ADMIN pueden acceder a su perfil
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        // Lógica para obtener el perfil del usuario autenticado
        return ResponseEntity.ok("Perfil del usuario autenticado.");
    }
}
