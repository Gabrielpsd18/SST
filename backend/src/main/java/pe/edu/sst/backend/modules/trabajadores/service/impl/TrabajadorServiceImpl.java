package pe.edu.sst.backend.modules.trabajadores.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.trabajadores.dto.request.ActualizarTrabajadorRequest;
import pe.edu.sst.backend.modules.trabajadores.dto.request.CrearTrabajadorRequest;
import pe.edu.sst.backend.modules.trabajadores.dto.response.MaestraResponse;
import pe.edu.sst.backend.modules.trabajadores.dto.response.TrabajadorResponse;
import pe.edu.sst.backend.modules.trabajadores.entity.*;
import pe.edu.sst.backend.modules.trabajadores.repository.*;
import pe.edu.sst.backend.modules.trabajadores.service.TrabajadorService;
import pe.edu.sst.backend.shared.exception.BadRequestException;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrabajadorServiceImpl implements TrabajadorService {

        private final TrabajadorRepository trabajadorRepository;
        private final SedeRepository sedeRepository;
        private final AreaRepository areaRepository;
        private final CargoRepository cargoRepository;

        @Override
        @Transactional
        public TrabajadorResponse crear(CrearTrabajadorRequest request) {
                if (trabajadorRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
                        throw new BadRequestException("El número de documento ya se encuentra registrado");
                }

                Sede sede = sedeRepository.findById(request.getSedeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Sede no encontrada con ID: " + request.getSedeId()));
                Area area = areaRepository.findById(request.getAreaId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Área no encontrada con ID: " + request.getAreaId()));
                Cargo cargo = cargoRepository.findById(request.getCargoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cargo no encontrado con ID: " + request.getCargoId()));

                Trabajador trabajador = Trabajador.builder()
                                .tipoDocumento(request.getTipoDocumento())
                                .numeroDocumento(request.getNumeroDocumento())
                                .nombres(request.getNombres())
                                .apellidos(request.getApellidos())
                                .telefono(request.getTelefono())
                                .tipoContrato(request.getTipoContrato())
                                .sede(sede)
                                .area(area)
                                .cargo(cargo)
                                .usuarioId(request.getUsuarioId())
                                .estado("ACTIVO")
                                .build();

                return mapToResponse(trabajadorRepository.save(trabajador));
        }

        @Override
        @Transactional
        public TrabajadorResponse actualizar(Long id, ActualizarTrabajadorRequest request) {
                Trabajador trabajador = trabajadorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trabajador no encontrado con ID: " + id));

                Sede sede = sedeRepository.findById(request.getSedeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Sede no encontrada con ID: " + request.getSedeId()));
                Area area = areaRepository.findById(request.getAreaId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Área no encontrada con ID: " + request.getAreaId()));
                Cargo cargo = cargoRepository.findById(request.getCargoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cargo no encontrado con ID: " + request.getCargoId()));

                trabajador.setNombres(request.getNombres());
                trabajador.setApellidos(request.getApellidos());
                trabajador.setTelefono(request.getTelefono());
                trabajador.setTipoContrato(request.getTipoContrato());
                trabajador.setSede(sede);
                trabajador.setArea(area);
                trabajador.setCargo(cargo);
                trabajador.setEstado(request.getEstado());

                return mapToResponse(trabajadorRepository.save(trabajador));
        }

        @Override
        @Transactional(readOnly = true)
        public TrabajadorResponse obtenerPorId(Long id) {
                return trabajadorRepository.findById(id)
                                .map(this::mapToResponse)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trabajador no encontrado con ID: " + id));
        }

        @Override
        @Transactional(readOnly = true)
        public Page<TrabajadorResponse> listarPaginado(Pageable pageable) {
                return trabajadorRepository.findAll(pageable).map(this::mapToResponse);
        }

        @Override
        @Transactional
        public void cambiarEstado(Long id, String nuevoEstado) {
                Trabajador trabajador = trabajadorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trabajador no encontrado con ID: " + id));
                trabajador.setEstado(nuevoEstado);
                trabajadorRepository.save(trabajador);
        }

        @Override
        public List<MaestraResponse> listarSedes() {
                return sedeRepository.findByEstadoTrue().stream()
                                .map(s -> new MaestraResponse(s.getId(), s.getNombre())).toList();
        }

        @Override
        public List<MaestraResponse> listarAreas() {
                return areaRepository.findByEstadoTrue().stream()
                                .map(a -> new MaestraResponse(a.getId(), a.getNombre())).toList();
        }

        @Override
        public List<MaestraResponse> listarCargos() {
                return cargoRepository.findByEstadoTrue().stream()
                                .map(c -> new MaestraResponse(c.getId(), c.getNombre())).toList();
        }

        private TrabajadorResponse mapToResponse(Trabajador t) {
                return TrabajadorResponse.builder()
                                .id(t.getId())
                                .tipoDocumento(t.getTipoDocumento())
                                .numeroDocumento(t.getNumeroDocumento())
                                .nombres(t.getNombres())
                                .apellidos(t.getApellidos())
                                .telefono(t.getTelefono())
                                .tipoContrato(t.getTipoContrato())
                                .estado(t.getEstado())
                                .sedeId(t.getSede().getId())
                                .sedeNombre(t.getSede().getNombre())
                                .areaId(t.getArea().getId())
                                .areaNombre(t.getArea().getNombre())
                                .cargoId(t.getCargo().getId())
                                .cargoNombre(t.getCargo().getNombre())
                                .usuarioId(t.getUsuarioId())
                                .createdAt(t.getCreatedAt())
                                .build();
        }
}