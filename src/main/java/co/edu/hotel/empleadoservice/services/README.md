# 🏨 Hotel  - Capa de Servicios (`services`)

La capa **services** contiene la **lógica de negocio** de la aplicación.  
Aquí se gestionan las operaciones que combinan el acceso a datos (repositorios), la validación de reglas y el procesamiento de la información antes de llegar al controlador.

---

## 📦 Librerías Utilizadas

- **Spring Framework (`org.springframework.stereotype.Service`, `org.springframework.beans.factory.annotation.Autowired`)** → Para declarar servicios e inyectar dependencias.
- **Spring Data JPA (`JpaRepository`)** → Para comunicarse con la capa de repositorios.
- **Spring Security (`PasswordEncoder`)** → Para encriptar contraseñas y manejar autenticación.

---

## 🧩 Ejemplo de Servicio: `UserServices`

```java
package co.uco.hotel.userservice.services.user;

import co.uco.hotel.userservice.domain.user.User;
import co.uco.hotel.userservice.domain.user.Role;
import co.uco.hotel.userservice.repositories.user.IUserRepository;
import co.uco.hotel.userservice.repositories.user.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServices(IUserRepository userRepository, IRoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔸 Registrar un nuevo usuario con su rol
    public User registerUser(User user) {
        // Encriptar contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Buscar el rol asignado o usar uno por defecto
        Role role = roleRepository.findById(user.getRole().getId())
                .orElseGet(() -> roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        user.setRole(role);

        return userRepository.save(user);
    }

    // 🔸 Buscar usuario por email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
