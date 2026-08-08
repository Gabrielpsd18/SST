package pe.edu.sst.backend.modules.capacitaciones.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorResponse;
import pe.edu.sst.backend.modules.capacitaciones.entity.Capacitador;
import pe.edu.sst.backend.modules.capacitaciones.repository.CapacitadorRepository;
import pe.edu.sst.backend.modules.capacitaciones.service.CapacitadorService;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacitadorServiceImpl implements CapacitadorService {

    private final CapacitadorRepository capacitadorRepository;

    @Override
    @Transactional
    public CapacitadorResponse crear(CapacitadorRequest request) {
        Capacitador capacitador = Capacitador.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .empresa(request.getEmpresa())
                .telefono(request.getTelefono())
                .correo(request.getCorreo())
                .especialidad(request.getEspecialidad())
                .estado(true)
                .build();

        return mapToResponse(capacitadorRepository.save(capacitador));
    }

    @Override
    @Transactional
    public CapacitadorResponse actualizar(Long id, CapacitadorRequest request) {
        Capacitador capacitador = capacitadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Capacitador no encontrado con ID: " + id));

        capacitador.setNombres(request.getNombres());
        capacitador.setApellidos(request.getApellidos());
        capacitador.setEmpresa(request.getEmpresa());
        capacitador.setTelefono(request.getTelefono());
        capacitador.setCorreo(request.getCorreo());
        capacitador.setEspecialidad(request.getEspecialidad());

        return mapToResponse(capacitadorRepository.save(capacitador));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CapacitadorResponse> listarActivos() {
        return capacitadorRepository.findByEstadoTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CapacitadorResponse obtenerPorId(Long id) {
        return capacitadorRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Capacitador no encontrado con ID: " + id));
    }

    private CapacitadorResponse mapToResponse(Capacitador c) {
        return CapacitadorResponse.builder()
                .id(c.getId())
                .nombres(c.getNombres())
                .apellidos(c.getApellidos())
                .empresa(c.getEmpresa())
                .telefono(c.getTelefono())
                .correo(c.getCorreo())
                .especialidad(c.getEspecialidad())
                .estado(c.getEstado())
                .build();
    }
}
