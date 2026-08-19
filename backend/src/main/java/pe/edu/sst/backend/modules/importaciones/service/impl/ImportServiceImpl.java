package pe.edu.sst.backend.modules.importaciones.service.impl;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.importaciones.dto.ImportResultDTO;
import pe.edu.sst.backend.modules.importaciones.dto.InvalidRowDetail;
import pe.edu.sst.backend.modules.importaciones.entity.ImportBatch;
import pe.edu.sst.backend.modules.importaciones.entity.TrabajadorMesEstado;
import pe.edu.sst.backend.modules.importaciones.repository.ImportAuditLogRepository;
import pe.edu.sst.backend.modules.importaciones.repository.ImportBatchRepository;
import pe.edu.sst.backend.modules.importaciones.repository.ImportErrorRepository;
import pe.edu.sst.backend.modules.importaciones.repository.TrabajadorMesEstadoRepository;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import pe.edu.sst.backend.modules.trabajadores.repository.CargoRepository;
import pe.edu.sst.backend.modules.trabajadores.repository.SedeRepository;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.Rol;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.entity.repository.RolRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import pe.edu.sst.backend.modules.importaciones.service.ImportService;
import pe.edu.sst.backend.modules.importaciones.entity.ImportAuditLog;
import pe.edu.sst.backend.modules.trabajadores.entity.Cargo;
import pe.edu.sst.backend.modules.trabajadores.entity.Sede;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.InputStream;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ImportBatchRepository importBatchRepository;
    private final TrabajadorMesEstadoRepository trabajadorMesEstadoRepository;
    private final ImportAuditLogRepository importAuditLogRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final SedeRepository sedeRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ImportErrorRepository importErrorRepository;
    private final PasswordEncoder passwordEncoder;
    

    @Override
    @Transactional
    public ImportResultDTO processAndImplementImport(MultipartFile file, String monthOption) throws Exception {
        YearMonth target = resolveYearMonth(monthOption);

        ImportBatch batch = ImportBatch.builder()
                .filename(file.getOriginalFilename())
                .month(target.getMonthValue())
                .year(target.getYear())
                .status("PROCESSING")
                .summaryCreated(0)
                .summaryReactivated(0)
                .summaryDeactivated(0)
                .summaryUpdated(0)
                .summaryErrors(0)
                .build();
        batch = importBatchRepository.save(batch);

        List<Map<String, String>> rows = parseExcel(file.getInputStream());
        // ObjectMapper compartido con soporte para java.time.* para evitar InvalidDefinitionException al serializar entidades con LocalDateTime
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Map<String, Map<String, String>> validRowsByDni = new LinkedHashMap<>();
        Set<String> duplicateDnis = new HashSet<>();
        List<InvalidRowDetail> errors = new ArrayList<>();
        // guardar errores en tabla temporal para que la UI los muestre siempre hasta que se corrijan o se descarten
        List<pe.edu.sst.backend.modules.importaciones.entity.ImportError> persistedErrors = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> row = rows.get(i);
            String dni = row.getOrDefault("dni", "").trim();

            if (dni.isEmpty() || !dni.matches("^[0-9]{6,20}$")) {
                errors.add(buildInvalidRow(row, "DNI inválido"));
                continue;
            }

            if (validRowsByDni.containsKey(dni)) {
                duplicateDnis.add(dni);
                errors.add(buildInvalidRow(row, "DNI repetido en archivo"));
                continue;
            }

            String sedeNombre = row.getOrDefault("sede", "").trim();
            String cargoNombre = row.getOrDefault("cargo", "").trim();

            if (!sedeNombre.isEmpty() && sedeRepository.findByEstadoTrue().stream()
                    .noneMatch(s -> s.getNombre().equalsIgnoreCase(sedeNombre))) {
                errors.add(buildInvalidRow(row, "Sede no encontrada: " + sedeNombre));
                continue;
            }

            if (!cargoNombre.isEmpty() && cargoRepository.findByEstadoTrue().stream()
                    .noneMatch(c -> c.getNombre().equalsIgnoreCase(cargoNombre))) {
                errors.add(buildInvalidRow(row, "Cargo no encontrado: " + cargoNombre));
                continue;
            }

            validRowsByDni.put(dni, row);
        }

        // Persistir errores encontrados en tabla temporal para que la UI los consulte en la página de importaciones
        for (InvalidRowDetail ir : errors) {
            String errMsg = ir.getErrorMessage();
            String dni = ir.getDni();
            // evitar duplicados exactos
            boolean exists = importErrorRepository.existsByImportBatchIdAndDniAndErrorMessage(batch.getId(), dni, errMsg);
            if (!exists) {
                pe.edu.sst.backend.modules.importaciones.entity.ImportError ie = pe.edu.sst.backend.modules.importaciones.entity.ImportError.builder()
                        .importBatchId(batch.getId())
                        .dni(ir.getDni())
                        .trabajador(ir.getTrabajador())
                        .telefono(ir.getTelefono())
                        .sede(ir.getSede())
                        .cargo(ir.getCargo())
                        .errorMessage(ir.getErrorMessage())
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
                persistedErrors.add(importErrorRepository.save(ie));
            }
        }

        List<Trabajador> allTrabajadores = trabajadorRepository.findAll();
        Map<String, Trabajador> dbByDni = allTrabajadores.stream()
                .filter(t -> t.getNumeroDocumento() != null)
                .collect(Collectors.toMap(Trabajador::getNumeroDocumento, t -> t, (a, b) -> a));

        int deactivated = 0;
        for (Trabajador trabajador : allTrabajadores.stream()
                .filter(t -> t.getEstado() != null && t.getEstado().equalsIgnoreCase("ACTIVO"))
                .collect(Collectors.toList())) {
            if (!validRowsByDni.containsKey(trabajador.getNumeroDocumento())) {
                String before = om.writeValueAsString(trabajador);
                trabajador.setEstado("INACTIVO");
                trabajadorRepository.save(trabajador);
                trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                        .trabajadorId(trabajador.getId())
                        .importBatchId(batch.getId())
                        .month(batch.getMonth())
                        .year(batch.getYear())
                        .estado("INACTIVO")
                        .build());
                importAuditLogRepository.save(ImportAuditLog.builder()
                        .importBatchId(batch.getId())
                        .trabajadorId(trabajador.getId())
                        .action("DEACTIVATE")
                        .beforeJson(before)
                        .afterJson(om.writeValueAsString(trabajador))
                        .build());
                deactivated++;
            }
        }

        int created = 0;
        int reactivated = 0;
        int updated = 0;
        // asegurarse de que exista el rol TRABAJADOR; si no existe, crearlo automáticamente para evitar excepciones y 500s
        Rol rolTrabajador = rolRepository.findByNombre(RoleName.TRABAJADOR)
                .orElseGet(() -> rolRepository.save(Rol.builder().nombre(RoleName.TRABAJADOR).descripcion("Creado automáticamente").build()));

        for (Map.Entry<String, Map<String, String>> entry : validRowsByDni.entrySet()) {
            Map<String, String> row = entry.getValue();
            String dni = entry.getKey();
            Trabajador existing = dbByDni.get(dni);

            String nombre = row.getOrDefault("nombrecompleto", row.getOrDefault("nombre", "")).trim();
            String telefono = row.getOrDefault("telefono", "").trim();
            String correo = row.getOrDefault("correo", "").trim();
            String sedeNombre = row.getOrDefault("sede", "").trim();
            String cargoNombre = row.getOrDefault("cargo", "").trim();

            Sede sedeEnt = null;
            if (!sedeNombre.isEmpty()) {
                sedeEnt = sedeRepository.findByEstadoTrue().stream()
                        .filter(s -> s.getNombre().equalsIgnoreCase(sedeNombre))
                        .findFirst()
                        .orElse(null);
            }

            Cargo cargoEnt = null;
            if (!cargoNombre.isEmpty()) {
                cargoEnt = cargoRepository.findByEstadoTrue().stream()
                        .filter(c -> c.getNombre().equalsIgnoreCase(cargoNombre))
                        .findFirst()
                        .orElse(null);
            }

            if (existing == null) {
                Usuario user = Usuario.builder()
                        .email(dni + "@sst.com")
                        .password(passwordEncoder.encode(dni))
                        .activo(true)
                        .rol(rolTrabajador)
                        .build();
                user = usuarioRepository.save(user);

                Trabajador nuevo = Trabajador.builder()
                        .tipoDocumento("DNI")
                        .numeroDocumento(dni)
                        .nombreCompleto(nombre)
                        .telefono(telefono.isEmpty() ? null : telefono)
                        .correoNotificaciones(correo.isEmpty() ? null : correo)
                        .tipoContrato("TEMPORAL")
                        .sede(sedeEnt != null ? sedeEnt : sedeRepository.findByEstadoTrue().stream().findFirst().orElse(null))
                        .cargo(cargoEnt != null ? cargoEnt : cargoRepository.findByEstadoTrue().stream().findFirst().orElse(null))
                        .usuarioId(user.getId())
                        .estado("ACTIVO")
                        .build();
                trabajadorRepository.save(nuevo);
                trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                        .trabajadorId(nuevo.getId())
                        .importBatchId(batch.getId())
                        .month(batch.getMonth())
                        .year(batch.getYear())
                        .estado("ACTIVO")
                        .build());
                importAuditLogRepository.save(ImportAuditLog.builder()
                        .importBatchId(batch.getId())
                        .trabajadorId(nuevo.getId())
                        .action("CREATE")
                        .beforeJson(null)
                        .afterJson(om.writeValueAsString(nuevo))
                        .build());
                created++;
                continue;
            }

            if (existing.getEstado() != null && existing.getEstado().equalsIgnoreCase("INACTIVO")) {
                existing.setEstado("ACTIVO");
                Usuario usr = existing.getUsuarioId() != null ? usuarioRepository.findById(existing.getUsuarioId()).orElse(null) : null;
                if (usr == null) {
                    Usuario user = Usuario.builder()
                            .email(dni + "@sst.com")
                            .password(passwordEncoder.encode(dni))
                            .activo(true)
                            .rol(rolTrabajador)
                            .build();
                    user = usuarioRepository.save(user);
                    existing.setUsuarioId(user.getId());
                } else {
                    usr.setEmail(dni + "@sst.com");
                    usr.setPassword(passwordEncoder.encode(dni));
                    usr.setActivo(true);
                    usuarioRepository.save(usr);
                }

                String before = om.writeValueAsString(existing);
                existing.setNombreCompleto(nombre.isEmpty() ? existing.getNombreCompleto() : nombre);
                existing.setTelefono(telefono.isEmpty() ? existing.getTelefono() : telefono);
                existing.setCorreoNotificaciones(correo.isEmpty() ? existing.getCorreoNotificaciones() : correo);
                if (sedeEnt != null) existing.setSede(sedeEnt);
                if (cargoEnt != null) existing.setCargo(cargoEnt);
                trabajadorRepository.save(existing);
                trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                        .trabajadorId(existing.getId())
                        .importBatchId(batch.getId())
                        .month(batch.getMonth())
                        .year(batch.getYear())
                        .estado("ACTIVO")
                        .build());
                importAuditLogRepository.save(ImportAuditLog.builder()
                        .importBatchId(batch.getId())
                        .trabajadorId(existing.getId())
                        .action("REACTIVATE_OR_UPDATE")
                        .beforeJson(before)
                        .afterJson(om.writeValueAsString(existing))
                        .build());
                reactivated++;
                continue;
            }

            String before = om.writeValueAsString(existing);
            existing.setNombreCompleto(nombre.isEmpty() ? existing.getNombreCompleto() : nombre);
            existing.setTelefono(telefono.isEmpty() ? existing.getTelefono() : telefono);
            existing.setCorreoNotificaciones(correo.isEmpty() ? existing.getCorreoNotificaciones() : correo);
            if (sedeEnt != null) existing.setSede(sedeEnt);
            if (cargoEnt != null) existing.setCargo(cargoEnt);
            trabajadorRepository.save(existing);
            trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                    .trabajadorId(existing.getId())
                    .importBatchId(batch.getId())
                    .month(batch.getMonth())
                    .year(batch.getYear())
                    .estado("ACTIVO")
                    .build());
            importAuditLogRepository.save(ImportAuditLog.builder()
                    .importBatchId(batch.getId())
                    .trabajadorId(existing.getId())
                    .action("UPDATE")
                    .beforeJson(before)
                    .afterJson(om.writeValueAsString(existing))
                    .build());
            updated++;
        }

        batch.setSummaryCreated(created);
        batch.setSummaryReactivated(reactivated);
        batch.setSummaryDeactivated(deactivated);
        batch.setSummaryUpdated(updated);
        batch.setSummaryErrors(errors.size() + duplicateDnis.size());
        batch.setStatus("COMPLETED");
        importBatchRepository.save(batch);

        return ImportResultDTO.builder()
                .totalRows(rows.size())
                .correctRows(created + reactivated + updated)
                .errorsCount(errors.size())
                .duplicates(duplicateDnis.size())
                .wouldDeactivateCount(deactivated)
                .errors(errors)
                .build();
    }    @Override
    @Transactional
    public void processSingleRow(InvalidRowDetail rowDetail) throws Exception {
        if (rowDetail == null || rowDetail.getDni() == null || rowDetail.getDni().isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }

        Map<String, String> row = new HashMap<>();
        row.put("dni", rowDetail.getDni().trim());
        row.put("nombrecompleto", rowDetail.getTrabajador() != null ? rowDetail.getTrabajador().trim() : "");
        row.put("telefono", rowDetail.getTelefono() != null ? rowDetail.getTelefono().trim() : "");
        row.put("sede", rowDetail.getSede() != null ? rowDetail.getSede().trim() : "");
        row.put("cargo", rowDetail.getCargo() != null ? rowDetail.getCargo().trim() : "");

        String dni = row.get("dni");
        if (dni.isEmpty() || !dni.matches("^[0-9]{6,20}$")) {
            throw new IllegalArgumentException("DNI inválido (debe tener entre 6 y 20 dígitos numéricos)");
        }

        String nombre = row.getOrDefault("nombrecompleto", "").trim();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del trabajador no puede estar vacío");
        }

        String sedeNombre = row.getOrDefault("sede", "").trim();
        if (sedeNombre.isEmpty()) {
            throw new IllegalArgumentException("La sede no puede estar vacía");
        }
        Sede sedeEnt = sedeRepository.findByEstadoTrue().stream()
                .filter(s -> s.getNombre().equalsIgnoreCase(sedeNombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Sede no encontrada: " + sedeNombre));

        String cargoNombre = row.getOrDefault("cargo", "").trim();
        if (cargoNombre.isEmpty()) {
            throw new IllegalArgumentException("El cargo no puede estar vacío");
        }
        Cargo cargoEnt = cargoRepository.findByEstadoTrue().stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(cargoNombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cargo no encontrado: " + cargoNombre));

        String telefono = row.getOrDefault("telefono", "").trim();
        String correo = row.getOrDefault("correo", "").trim();

        Trabajador existing = trabajadorRepository.findByNumeroDocumento(dni).orElse(null);

        if (existing == null) {
            Usuario user = Usuario.builder()
                    .email(dni + "@sst.com")
                    .password(passwordEncoder.encode(dni))
                    .activo(true)
                    .rol(rolRepository.findByNombre(RoleName.TRABAJADOR).orElseGet(() -> rolRepository.save(Rol.builder().nombre(RoleName.TRABAJADOR).descripcion("Creado automáticamente").build())))
                    .build();
            user = usuarioRepository.save(user);

            Trabajador nuevo = Trabajador.builder()
                    .tipoDocumento("DNI")
                    .numeroDocumento(dni)
                    .nombreCompleto(nombre)
                    .telefono(telefono.isEmpty() ? null : telefono)
                    .correoNotificaciones(correo.isEmpty() ? null : correo)
                    .tipoContrato("TEMPORAL")
                    .sede(sedeEnt)
                    .cargo(cargoEnt)
                    .usuarioId(user.getId())
                    .estado("ACTIVO")
                    .build();
            trabajadorRepository.save(nuevo);

            // si procesamos correctamente la fila, borrar cualquier error pendiente para este dni
            java.util.List<pe.edu.sst.backend.modules.importaciones.entity.ImportError> existingErrors = importErrorRepository.findByDni(dni);
            if (existingErrors != null && !existingErrors.isEmpty()) {
                importErrorRepository.deleteAll(existingErrors);
            }
            return;
        }

        existing.setNombreCompleto(nombre);
        existing.setTelefono(telefono.isEmpty() ? null : telefono);
        existing.setCorreoNotificaciones(correo.isEmpty() ? null : correo);
        existing.setSede(sedeEnt);
        existing.setCargo(cargoEnt);
        existing.setEstado("ACTIVO");
        trabajadorRepository.save(existing);

        // si se procesó correctamente, borrar errores pendientes para este dni
        java.util.List<pe.edu.sst.backend.modules.importaciones.entity.ImportError> existingErrors = importErrorRepository.findByDni(dni);
        if (existingErrors != null && !existingErrors.isEmpty()) {
            importErrorRepository.deleteAll(existingErrors);
        }
    }

    private InvalidRowDetail buildInvalidRow(Map<String, String> row, String message) {
        return InvalidRowDetail.builder()
                .dni(row.getOrDefault("dni", "").trim())
                .trabajador(row.getOrDefault("nombrecompleto", row.getOrDefault("nombre", "")).trim())
                .telefono(row.getOrDefault("telefono", "").trim())
                .sede(row.getOrDefault("sede", "").trim())
                .cargo(row.getOrDefault("cargo", "").trim())
                .errorMessage(message)
                .build();
    }

    @Override
    public java.util.List<pe.edu.sst.backend.modules.importaciones.entity.ImportError> getPendingErrors() {
        return importErrorRepository.findByOrderByCreatedAtDesc();
    }

    @Override
    public void deletePendingError(Long id) {
        if (id == null) return;
        importErrorRepository.deleteById(id);
    }

    @Override
    public void retryPendingError(Long id) throws Exception {
        var opt = importErrorRepository.findById(id);
        if (opt.isEmpty()) return;
        var ie = opt.get();
        InvalidRowDetail row = InvalidRowDetail.builder()
                .dni(ie.getDni())
                .trabajador(ie.getTrabajador())
                .telefono(ie.getTelefono())
                .sede(ie.getSede())
                .cargo(ie.getCargo())
                .build();
        // intentar procesar la fila
        processSingleRow(row);
        // si no lanza excepción, borrar el error pendiente
        importErrorRepository.deleteById(id);
    }


    private YearMonth resolveYearMonth(String monthOption) {
        YearMonth now = YearMonth.now();
        if (monthOption == null)
            return now;
        switch (monthOption.toUpperCase()) {
            case "THIS":
                return now;
            case "NEXT":
                return now.plusMonths(1);
            case "PREV":
                return now.minusMonths(1);
            default:
                // try parse YYYY-MM or MM-YYYY
                try {
                    String[] parts = monthOption.split("-");
                    if (parts.length == 2) {
                        int m = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        return YearMonth.of(y, m);
                    }
                } catch (Exception ex) {
                }
                return now;
        }
    }

    private String normalizeHeader(String header) {
        if (header == null) return "";
        // Remove accents/diacritics
        String normalized = java.text.Normalizer.normalize(header, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        
        // Lowercase and trim
        normalized = normalized.trim().toLowerCase();
        
        // Remove non-alphanumeric characters
        normalized = normalized.replaceAll("[^a-z0-9]", "");
        
        // Map common aliases
        if (normalized.equals("nombres") || normalized.equals("nombre") || normalized.equals("nombrecompleto") 
                || normalized.equals("nombresyapellidos") || normalized.equals("apellidosynombres") 
                || normalized.equals("trabajador")) {
            return "nombrecompleto";
        }
        if (normalized.equals("dni") || normalized.equals("documento") || normalized.equals("nrodocumento") 
                || normalized.equals("numerodocumento") || normalized.equals("nrodoc")) {
            return "dni";
        }
        if (normalized.equals("telefono") || normalized.equals("celular") || normalized.equals("telf")) {
            return "telefono";
        }
        if (normalized.equals("sede") || normalized.equals("oficina") || normalized.equals("local")) {
            return "sede";
        }
        if (normalized.equals("cargo") || normalized.equals("puesto") || normalized.equals("funcion") || normalized.equals("rol")) {
            return "cargo";
        }
        if (normalized.equals("correo") || normalized.equals("email") || normalized.equals("mail") 
                || normalized.equals("correoelectronico") || normalized.equals("correonotificaciones")) {
            return "correo";
        }
        
        return normalized;
    }

    private List<Map<String, String>> parseExcel(InputStream is) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        Workbook wb = WorkbookFactory.create(is);
        Sheet sheet = wb.getSheetAt(0);

        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> it = sheet.rowIterator();
        if (!it.hasNext())
            return rows;

        Row header = it.next();
        List<String> headers = new ArrayList<>();
        for (Cell c : header) {
            headers.add(normalizeHeader(dataFormatter.formatCellValue(c)));
        }

        while (it.hasNext()) {
            Row r = it.next();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                Cell c = r.getCell(i);
                String val = "";
                if (c != null) {
                    val = dataFormatter.formatCellValue(c).trim();
                }
                map.put(headers.get(i), val);
            }
            rows.add(map);
        }
        wb.close();
        return rows;
    }
}




