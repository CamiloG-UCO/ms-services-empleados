package co.edu.hotel.empleadoservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "empleados")
public class Empleados {

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
    private String roles;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String contactNumber;

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

    // ---- Getters & Setters ----
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getIdentificationNumber() { return identificationNumber; }
    public void setIdentificationNumber(int identificationNumber) { this.identificationNumber = identificationNumber; }

    public TipoIdentificacion getIdentificationType() { return identificationType; }
    public void setIdentificationType(TipoIdentificacion identificationType) { this.identificationType = identificationType; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getHotel() { return hotel; }
    public void setHotel(String hotel) { this.hotel = hotel; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getRegisteredBy() { return registeredBy; }
    public void setRegisteredBy(String registeredBy) { this.registeredBy = registeredBy; }
}
