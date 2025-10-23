package co.edu.hotel.empleadoservice.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "empleados")
@Component
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private int identificationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_identificacion_id", nullable = false)
    private TipoIdentificacion identificationType;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int contactNumber;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private String hotel;

    @CreationTimestamp
    @Column(name = "registrar_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "registrar_by", nullable = false)
    private String registeredBy;
}

