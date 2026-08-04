package pe.edu.sst.backend.modules.identity.entity;

import pe.edu.sst.backend.modules.identity.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rol{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique= true, length=50)
    @Enumerated(EnumType.STRING)
    private RoleName nombre;
    @Column(nullable=false, length = 50 )
    private String descripcion;
}