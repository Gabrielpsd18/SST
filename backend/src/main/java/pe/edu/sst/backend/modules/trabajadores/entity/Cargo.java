package pe.edu.sst.backend.modules.trabajadores.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cargos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Builder.Default
    @Column(nullable = false)
    private Boolean estado = true;
}