package pe.edu.sst.backend.modules.trabajadores.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.RolRepository;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
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

        private static final String TIPO_DOCUMENTO_DNI = "DNI";
        private static final String TIPO_CONTRATO_TEMPORAL = "TEMPORAL";
        private static final String ESTADO_ACTIVO = "ACTIVO";
        private static final String ESTADO_CESADO = "CESADO";

        private final TrabajadorRepository trabajadorRepository;
        private final SedeRepository sedeRepository;
        private final CargoRepository cargoRepository;
        private final UsuarioRepository usuarioRepository;
        private final RolRepository rolRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public TrabajadorResponse crear(CrearTrabajadorRequest request) {
                String numeroDocumento = request.getNumeroDocumento().trim();
                String nombreCompleto = request.getNombreCompleto().trim();
                String telefono = normalizeOptional(request.getTelefono());
                String correoNotificaciones = normalizeOptional(request.getCorreoNotificaciones());

                validarDni(numeroDocumento);
                validarTelefonoOpcional(telefono);
                validarCorreoNotificacionesOpcional(correoNotificaciones);

                if (trabajadorRepository.existsByNumeroDocumento(numeroDocumento)) {
                        throw new BadRequestException("El DNI ya se encuentra registrado");
                }

                String emailUsuario = numeroDocumento + "@sst.com";
                if (usuarioRepository.existsByEmail(emailUsuario)) {
                        throw new BadRequestException("Ya existe un usuario asociado al DNI ingresado");
                }

                Sede sede = sedeRepository.findById(request.getSedeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Sede no encontrada con ID: " + request.getSedeId()));
                Cargo cargo = cargoRepository.findById(request.getCargoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cargo no encontrado con ID: " + request.getCargoId()));

                var rolTrabajador = rolRepository.findByNombre(RoleName.TRABAJADOR)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Rol TRABAJADOR no encontrado en el sistema"));

                Usuario usuario = Usuario.builder()
                                .email(emailUsuario)
                                .password(passwordEncoder.encode(numeroDocumento))
                                .activo(true)
                                .rol(rolTrabajador)
                                .build();
                usuario = usuarioRepository.save(usuario);

                Trabajador trabajador = Trabajador.builder()
                                .tipoDocumento(TIPO_DOCUMENTO_DNI)
                                .numeroDocumento(numeroDocumento)
                                .nombreCompleto(nombreCompleto)
                                .telefono(telefono)
                                .correoNotificaciones(correoNotificaciones)
                                .tipoContrato(TIPO_CONTRATO_TEMPORAL)
                                .sede(sede)
                                .cargo(cargo)
                                .usuarioId(usuario.getId())
                                .estado(ESTADO_ACTIVO)
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
                Cargo cargo = cargoRepository.findById(request.getCargoId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Cargo no encontrado con ID: " + request.getCargoId()));

                String nuevoEstado = request.getEstado().trim().toUpperCase();
                validarEstado(nuevoEstado);

                trabajador.setNombreCompleto(request.getNombreCompleto().trim());
                String telefono = normalizeOptional(request.getTelefono());
                String correoNotificaciones = normalizeOptional(request.getCorreoNotificaciones());
                validarTelefonoOpcional(telefono);
                validarCorreoNotificacionesOpcional(correoNotificaciones);
                trabajador.setTelefono(telefono);
                trabajador.setCorreoNotificaciones(correoNotificaciones);
                trabajador.setTipoContrato(TIPO_CONTRATO_TEMPORAL);
                trabajador.setSede(sede);
                trabajador.setCargo(cargo);
                trabajador.setEstado(nuevoEstado);

                sincronizarUsuarioActivo(trabajador, nuevoEstado);

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
        @Transactional(readOnly = true)
        public List<TrabajadorResponse> buscarPorSegmento(String segment, int limit) {
                String value = segment == null ? "" : segment.trim();
                if (value.isEmpty()) {
                        return List.of();
                }

                int safeLimit = Math.max(1, Math.min(limit, 20));
                Pageable pageable = PageRequest.of(0, safeLimit);

                return trabajadorRepository.findBySegmento(value, pageable)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Override
        @Transactional
        public void cambiarEstado(Long id, String nuevoEstado) {
                Trabajador trabajador = trabajadorRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Trabajador no encontrado con ID: " + id));

                String estadoNormalizado = nuevoEstado.trim().toUpperCase();
                validarEstado(estadoNormalizado);

                trabajador.setEstado(estadoNormalizado);
                sincronizarUsuarioActivo(trabajador, estadoNormalizado);
                trabajadorRepository.save(trabajador);
        }

        @Override
        public List<MaestraResponse> listarSedes() {
                return sedeRepository.findByEstadoTrue().stream()
                                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                                .map(s -> new MaestraResponse(s.getId(), s.getNombre())).toList();
        }

        @Override
        public List<MaestraResponse> listarCargos() {
                return cargoRepository.findByEstadoTrue().stream()
                                .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
                                .map(c -> new MaestraResponse(c.getId(), c.getNombre())).toList();
        }

        private void validarDni(String numeroDocumento) {
                if (numeroDocumento == null || !numeroDocumento.matches("^[0-9]{8}$")) {
                        throw new BadRequestException("El DNI debe contener exactamente 8 dígitos");
                }
        }

        private void validarEstado(String estado) {
                if (!ESTADO_ACTIVO.equals(estado) && !ESTADO_CESADO.equals(estado)) {
                        throw new BadRequestException("El estado debe ser ACTIVO o CESADO");
                }
        }

        private void validarTelefonoOpcional(String telefono) {
                if (telefono != null && !telefono.matches("^[0-9]{9}$")) {
                        throw new BadRequestException("El teléfono debe contener exactamente 9 dígitos");
                }
        }

        private void validarCorreoNotificacionesOpcional(String correo) {
                if (correo != null && !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                        throw new BadRequestException("El correo de notificaciones debe tener un formato válido");
                }
        }

        private void sincronizarUsuarioActivo(Trabajador trabajador, String estado) {
                if (trabajador.getUsuarioId() == null) {
                        return;
                }

                Usuario usuario = usuarioRepository.findById(trabajador.getUsuarioId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Usuario no encontrado con ID: " + trabajador.getUsuarioId()));

                usuario.setActivo(ESTADO_ACTIVO.equals(estado));
                usuarioRepository.save(usuario);
        }

        private String normalizeOptional(String value) {
                if (value == null || value.isBlank()) {
                        return null;
                }
                return value.trim();
        }

        private TrabajadorResponse mapToResponse(Trabajador t) {
                return TrabajadorResponse.builder()
                                .id(t.getId())
                                .tipoDocumento(t.getTipoDocumento())
                                .numeroDocumento(t.getNumeroDocumento())
                                .nombreCompleto(t.getNombreCompleto())
                                .telefono(t.getTelefono())
                                .correoNotificaciones(t.getCorreoNotificaciones())
                                .tipoContrato(t.getTipoContrato())
                                .estado(t.getEstado())
                                .sedeId(t.getSede().getId())
                                .sedeNombre(t.getSede().getNombre())
                                .cargoId(t.getCargo().getId())
                                .cargoNombre(t.getCargo().getNombre())
                                .usuarioId(t.getUsuarioId())
                                .createdAt(t.getCreatedAt())
                                .build();
        }
}
