package pe.edu.sst.backend.modules.importaciones.service.impl;

import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.importaciones.dto.ImportPreviewResult;
import pe.edu.sst.backend.modules.importaciones.entity.ImportBatch;
import pe.edu.sst.backend.modules.importaciones.entity.ImportRowIssue;
import pe.edu.sst.backend.modules.importaciones.entity.TrabajadorMesEstado;
import pe.edu.sst.backend.modules.importaciones.repository.ImportAuditLogRepository;
import pe.edu.sst.backend.modules.importaciones.repository.ImportBatchRepository;
import pe.edu.sst.backend.modules.importaciones.repository.ImportRowIssueRepository;
import pe.edu.sst.backend.modules.importaciones.repository.TrabajadorMesEstadoRepository;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import pe.edu.sst.backend.modules.trabajadores.repository.CargoRepository;
import pe.edu.sst.backend.modules.trabajadores.repository.SedeRepository;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.entity.repository.RolRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;
import pe.edu.sst.backend.modules.importaciones.service.ImportService;
import pe.edu.sst.backend.modules.importaciones.entity.ImportAuditLog;
import pe.edu.sst.backend.modules.trabajadores.entity.Cargo;
import pe.edu.sst.backend.modules.trabajadores.entity.Sede;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ImportBatchRepository importBatchRepository;
    private final ImportRowIssueRepository importRowIssueRepository;
    private final TrabajadorMesEstadoRepository trabajadorMesEstadoRepository;
    private final ImportAuditLogRepository importAuditLogRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final SedeRepository sedeRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ImportPreviewResult previewImport(MultipartFile file, String monthOption) throws Exception {
        // 1. Determinar periodo
        YearMonth target = resolveYearMonth(monthOption);

        // 2. Crear lote inicial
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

        // 3. Parsear Excel a estructura plana
        List<Map<String, String>> rows = parseExcel(file.getInputStream());

        Map<String, Map<String, String>> excelByDni = new HashMap<>();
        Set<String> duplicateDnis = new HashSet<>();
        int invalidRows = 0;

        for (int i = 0; i < rows.size(); i++) {
            Map<String, String> r = rows.get(i);
            String dni = r.getOrDefault("dni", "").trim();

            if (dni.isEmpty() || !dni.matches("^[0-9]{6,20}$")) {
                // Justo antes de validar el DNI, añade esto:
                
                ImportRowIssue issue = ImportRowIssue.builder()
                        .importBatchId(batch.getId())
                        .rowNumber(i + 1)
                        .rawRowJson(r.toString())
                        .issueType("INVALID_DNI")
                        .issueDetails("DNI inválido")
                        .resolved(false)
                        .build();
                importRowIssueRepository.save(issue);
                continue;
            }
            if (excelByDni.containsKey(dni)) {
                duplicateDnis.add(dni);
            }
            excelByDni.putIfAbsent(dni, r);
        }

        // 4. Buscar trabajadores activos en BD
        List<Trabajador> activos = trabajadorRepository.findAll().stream()
                .filter(t -> t.getEstado() != null && t.getEstado().equalsIgnoreCase("ACTIVO"))
                .collect(Collectors.toList());

        List<Trabajador> toDeactivate = activos.stream()
                .filter(t -> !excelByDni.containsKey(t.getNumeroDocumento()))
                .collect(Collectors.toList());

        int missingSede = 0, missingCargo = 0;
        List<Map<String, String>> sample = rows.stream().limit(20).collect(Collectors.toList());

        // 5. Validar existencia de Sede y Cargo (Modo Estricto sin auto-creación)
        for (Map.Entry<String, Map<String, String>> e : excelByDni.entrySet()) {
            Map<String, String> r = e.getValue();
            String sedeNombre = r.getOrDefault("sede", "").trim();
            String cargoNombre = r.getOrDefault("cargo", "").trim();

            if (!sedeNombre.isEmpty()) {
                boolean exists = sedeRepository.findByEstadoTrue().stream()
                        .anyMatch(s -> s.getNombre().equalsIgnoreCase(sedeNombre));
                if (!exists) {
                    missingSede++;
                    importRowIssueRepository.save(ImportRowIssue.builder()
                            .importBatchId(batch.getId())
                            .rawRowJson(r.toString())
                            .issueType("MISSING_SEDE")
                            .issueDetails("Sede no encontrada: " + sedeNombre)
                            .resolved(false)
                            .build());
                }
            }

            if (!cargoNombre.isEmpty()) {
                boolean exists = cargoRepository.findAll().stream()
                        .anyMatch(c -> c.getNombre().equalsIgnoreCase(cargoNombre));
                if (!exists) {
                    missingCargo++;
                    importRowIssueRepository.save(ImportRowIssue.builder()
                            .importBatchId(batch.getId())
                            .rawRowJson(r.toString())
                            .issueType("MISSING_CARGO")
                            .issueDetails("Cargo no encontrado: " + cargoNombre)
                            .resolved(false)
                            .build());
                }
            }
        }

        // 6. Registrar duplicados como incidencias
        for (String d : duplicateDnis) {
            importRowIssueRepository.save(ImportRowIssue.builder()
                    .importBatchId(batch.getId())
                    .rawRowJson("DNI duplicate: " + d)
                    .issueType("DUPLICATE")
                    .issueDetails("DNI repetido en archivo")
                    .resolved(false)
                    .build());
        }

        // 7. Persistir filas válidas como ROW_DATA para que applyImport las procese
        // luego
        ObjectMapper om = new ObjectMapper();
        for (Map.Entry<String, Map<String, String>> e : excelByDni.entrySet()) {
            Map<String, String> r = e.getValue();
            importRowIssueRepository.save(ImportRowIssue.builder()
                    .importBatchId(batch.getId())
                    .rawRowJson(om.writeValueAsString(r))
                    .issueType("ROW_DATA")
                    .resolved(false)
                    .build());
        }

        // 8. Construir respuesta de previsualización
        ImportPreviewResult result = ImportPreviewResult.builder()
                .batchId(batch.getId())
                .totalRows(rows.size())
                .duplicates(duplicateDnis.size())
                .invalidRows(invalidRows)
                .missingSede(missingSede)
                .missingCargo(missingCargo)
                .newCount(0)
                .reactivatedCount(0)
                .wouldDeactivateCount(toDeactivate.size())
                .errors(invalidRows + duplicateDnis.size() + missingSede + missingCargo)
                .sampleRows(sample)
                .build();

        // 9. Dejar el lote en estado PENDIENTE esperando el "Aplicar cambios"
        batch.setStatus("PENDING");
        importBatchRepository.save(batch);

        return result;
    }

    @Override
    @Transactional
    public ImportPreviewResult applyImport(Long batchId) throws Exception {
        ImportBatch batch = importBatchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        batch.setStatus("PROCESSING");
        importBatchRepository.save(batch);

        // Load all ROW_DATA entries for this batch
        List<ImportRowIssue> rowDataIssues = importRowIssueRepository.findByImportBatchId(batchId).stream()
                .filter(i -> "ROW_DATA".equals(i.getIssueType()))
                .toList();

        ObjectMapper om = new ObjectMapper();

        Map<String, Map<String, String>> excelByDni = new HashMap<>();
        for (ImportRowIssue r : rowDataIssues) {
            try {
                Map<String, String> map = om.readValue(r.getRawRowJson(), Map.class);
                String dni = (map.getOrDefault("dni", "")).trim();
                if (dni.isEmpty())
                    continue;
                excelByDni.put(dni, map);
            } catch (Exception ex) {
                // skip malformed
            }

        }

        // Fetch all trabajadores from DB mapped by DNI
        List<Trabajador> allTrabajadores = trabajadorRepository.findAll();
        Map<String, Trabajador> dbByDni = allTrabajadores.stream().filter(t -> t.getNumeroDocumento() != null)
                .collect(Collectors.toMap(Trabajador::getNumeroDocumento, t -> t, (a, b) -> a));

        // Deactivate actuales activos not in excel
        List<Trabajador> activos = allTrabajadores.stream()
                .filter(t -> t.getEstado() != null && t.getEstado().equalsIgnoreCase("ACTIVO"))
                .collect(Collectors.toList());
        int deactivated = 0, created = 0, reactivated = 0, updated = 0, errors = 0;
        for (Trabajador t : activos) {
            if (!excelByDni.containsKey(t.getNumeroDocumento())) {
                try {
                    String before = om.writeValueAsString(t);
                    t.setEstado("INACTIVO");
                    trabajadorRepository.save(t);
                    trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                            .trabajadorId(t.getId())
                            .importBatchId(batch.getId())
                            .month(batch.getMonth())
                            .year(batch.getYear())
                            .estado("INACTIVO")
                            .build());
                    // audit
                    ImportAuditLog log = ImportAuditLog
                            .builder()
                            .importBatchId(batch.getId())
                            .trabajadorId(t.getId())
                            .action("DEACTIVATE")
                            .beforeJson(before)
                            .afterJson(om.writeValueAsString(t))
                            .build();
                    importAuditLogRepository.save(log);
                    deactivated++;
                } catch (Exception ex) {
                    deactivated++;
                }
            }
        }

        // Process excel rows
        for (Map.Entry<String, Map<String, String>> e : excelByDni.entrySet()) {
            String dni = e.getKey();
            Map<String, String> row = e.getValue();
            try {
                Trabajador existing = dbByDni.get(dni);
                String nombre = row.getOrDefault("nombrecompleto", row.getOrDefault("nombre", "")).trim();
                String telefono = row.getOrDefault("telefono", "").trim();
                String correo = row.getOrDefault("correo", "").trim();
                String sedeNombre = row.getOrDefault("sede", "").trim();
                String cargoNombre = row.getOrDefault("cargo", "").trim();

                // find sede and cargo if exists
                Sede sedeEnt = null;
                if (!sedeNombre.isEmpty()) {
                    var sede = sedeRepository.findByEstadoTrue().stream()
                            .filter(s -> s.getNombre().equalsIgnoreCase(sedeNombre)).findFirst();
                    if (sede.isPresent())
                        sedeEnt = sede.get();
                }
                Cargo cargoEnt = null;
                if (!cargoNombre.isEmpty()) {
                    var cargo = cargoRepository.findByEstadoTrue().stream()
                            .filter(c -> c.getNombre().equalsIgnoreCase(cargoNombre)).findFirst();
                    if (cargo.isPresent())
                        cargoEnt = cargo.get();
                }

                if (existing == null) {
                    // create user and trabajador
                    Usuario user = Usuario.builder()
                            .email(dni + "@sst.com")
                            .password(passwordEncoder.encode(dni))
                            .activo(true)
                            .rol(rolRepository.findByNombre(RoleName.TRABAJADOR).orElseThrow())
                            .build();
                    user = usuarioRepository.save(user);

                    Trabajador nuevo = Trabajador.builder()
                            .tipoDocumento("DNI")
                            .numeroDocumento(dni)
                            .nombreCompleto(nombre)
                            .telefono(telefono.isEmpty() ? null : telefono)
                            .correoNotificaciones(correo.isEmpty() ? null : correo)
                            .tipoContrato("TEMPORAL")
                            .sede(sedeEnt != null ? sedeEnt
                                    : sedeRepository.findByEstadoTrue().stream().findFirst().orElse(null))
                            .cargo(cargoEnt != null ? cargoEnt
                                    : cargoRepository.findByEstadoTrue().stream().findFirst().orElse(null))
                            .usuarioId(user.getId())
                            .estado("ACTIVO")
                            .build();
                    // Save trabajador
                    trabajadorRepository.save(nuevo);
                    trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                            .trabajadorId(nuevo.getId())
                            .importBatchId(batch.getId())
                            .month(batch.getMonth())
                            .year(batch.getYear())
                            .estado("ACTIVO")
                            .build());
                    // audit create
                    try {
                        String after = om.writeValueAsString(nuevo);
                        ImportAuditLog log = ImportAuditLog
                                .builder()
                                .importBatchId(batch.getId())
                                .trabajadorId(nuevo.getId())
                                .action("CREATE")
                                .beforeJson(null)
                                .afterJson(after)
                                .build();
                        importAuditLogRepository.save(log);
                    } catch (Exception ex) {
                    }
                    created++;
                } else {
                    // exists
                    if (existing.getEstado() != null && existing.getEstado().equalsIgnoreCase("INACTIVO")) {
                        existing.setEstado("ACTIVO");
                        // reset or create user
                        Usuario usr = null;
                        if (existing.getUsuarioId() != null) {
                            usr = usuarioRepository.findById(existing.getUsuarioId()).orElse(null);
                        }
                        if (usr == null) {
                            Usuario user = Usuario.builder()
                                    .email(dni + "@sst.com")
                                    .password(passwordEncoder.encode(dni))
                                    .activo(true)
                                    .rol(rolRepository.findByNombre(RoleName.TRABAJADOR).orElseThrow())
                                    .build();
                            user = usuarioRepository.save(user);
                            existing.setUsuarioId(user.getId());
                        } else {
                            usr.setEmail(dni + "@sst.com");
                            usr.setPassword(passwordEncoder.encode(dni));
                            usr.setActivo(true);
                            usuarioRepository.save(usr);
                        }

                        // update fields
                        try {
                            String before = om.writeValueAsString(existing);
                            existing.setNombreCompleto(nombre.isEmpty() ? existing.getNombreCompleto() : nombre);
                            existing.setTelefono(telefono.isEmpty() ? existing.getTelefono() : telefono);
                            existing.setCorreoNotificaciones(
                                    correo.isEmpty() ? existing.getCorreoNotificaciones() : correo);
                            if (sedeEnt != null)
                                existing.setSede(sedeEnt);
                            if (cargoEnt != null)
                                existing.setCargo(cargoEnt);
                            trabajadorRepository.save(existing);
                            trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                                    .trabajadorId(existing.getId())
                                    .importBatchId(batch.getId())
                                    .month(batch.getMonth())
                                    .year(batch.getYear())
                                    .estado("ACTIVO")
                                    .build());
                            // audit
                            ImportAuditLog log = ImportAuditLog
                                    .builder()
                                    .importBatchId(batch.getId())
                                    .trabajadorId(existing.getId())
                                    .action("REACTIVATE_OR_UPDATE")
                                    .beforeJson(before)
                                    .afterJson(om.writeValueAsString(existing))
                                    .build();
                            importAuditLogRepository.save(log);
                        } catch (Exception ex) {
                        }
                        reactivated++;
                    } else {
                        // active: update selected fields
                        try {
                            String before = om.writeValueAsString(existing);
                            existing.setNombreCompleto(nombre.isEmpty() ? existing.getNombreCompleto() : nombre);
                            existing.setTelefono(telefono.isEmpty() ? existing.getTelefono() : telefono);
                            existing.setCorreoNotificaciones(
                                    correo.isEmpty() ? existing.getCorreoNotificaciones() : correo);
                            if (sedeEnt != null)
                                existing.setSede(sedeEnt);
                            if (cargoEnt != null)
                                existing.setCargo(cargoEnt);
                            trabajadorRepository.save(existing);
                            trabajadorMesEstadoRepository.save(TrabajadorMesEstado.builder()
                                    .trabajadorId(existing.getId())
                                    .importBatchId(batch.getId())
                                    .month(batch.getMonth())
                                    .year(batch.getYear())
                                    .estado("ACTIVO")
                                    .build());
                            ImportAuditLog log = ImportAuditLog
                                    .builder()
                                    .importBatchId(batch.getId())
                                    .trabajadorId(existing.getId())
                                    .action("UPDATE")
                                    .beforeJson(before)
                                    .afterJson(om.writeValueAsString(existing))
                                    .build();
                            importAuditLogRepository.save(log);
                        } catch (Exception ex) {
                        }
                        updated++;
                    }
                }
            } catch (Exception ex) {
                errors++;
            }
        }

        batch.setSummaryCreated(created);
        batch.setSummaryReactivated(reactivated);
        batch.setSummaryDeactivated(deactivated);
        batch.setSummaryUpdated(updated);
        batch.setSummaryErrors(errors);
        batch.setStatus("COMPLETED");
        importBatchRepository.save(batch);

        return ImportPreviewResult.builder()
                .batchId(batch.getId())
                .totalRows(rowDataIssues.size())
                .newCount(created)
                .reactivatedCount(reactivated)
                .wouldDeactivateCount(deactivated)
                .errors(errors)
                .build();
    }

    @Override
    public List<ImportRowIssue> listIssues(Long batchId) {
        return importRowIssueRepository.findByImportBatchId(batchId);
    }

    @Override
    @Transactional
    public void resolveIssue(Long batchId, Long issueId, String action) {
        ImportRowIssue issue = importRowIssueRepository
                .findById(issueId).orElseThrow();
        // support some automatic actions
        if ("CREATE_MISSING".equalsIgnoreCase(action) || action != null && action.startsWith("CREATE_MISSING")) {
            // attempt to parse rawRowJson to extract sede/cargo names
            try {
                ObjectMapper om = new ObjectMapper();
                Map<String, String> row = om.readValue(issue.getRawRowJson(), Map.class);
                String sedeNombre = row.getOrDefault("sede", "").trim();
                String cargoNombre = row.getOrDefault("cargo", "").trim();
                if (!sedeNombre.isEmpty()) {
                    boolean exists = sedeRepository.findByEstadoTrue().stream()
                            .anyMatch(s -> s.getNombre().equalsIgnoreCase(sedeNombre));
                    if (!exists) {
                        Sede newSede = Sede
                                .builder()
                                .nombre(sedeNombre)
                                .estado(true)
                                .build();
                        sedeRepository.save(newSede);
                    }
                }
                if (!cargoNombre.isEmpty()) {
                    boolean exists = cargoRepository.findAll().stream()
                            .anyMatch(c -> c.getNombre().equalsIgnoreCase(cargoNombre));
                    if (!exists) {
                        Cargo newCargo = Cargo
                                .builder()
                                .nombre(cargoNombre)
                                .estado(true)
                                .build();
                        cargoRepository.save(newCargo);
                    }
                }
            } catch (Exception ex) {
                // ignore parse errors
            }
        }
        issue.setResolved(true);
        issue.setActionTaken(action);
        issue.setResolvedAt(java.time.LocalDateTime.now());
        importRowIssueRepository.save(issue);
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

    private List<Map<String, String>> parseExcel(InputStream is) throws Exception {
        List<Map<String, String>> rows = new ArrayList<>();
        Workbook wb = WorkbookFactory.create(is);
        Sheet sheet = wb.getSheetAt(0);

        // DataFormatter lee el valor de la celda tal como lo muestra Excel (evita .0 en
        // números y respeta formatos)
        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> it = sheet.rowIterator();
        if (!it.hasNext())
            return rows;

        Row header = it.next();
        List<String> headers = new ArrayList<>();
        for (Cell c : header) {
            headers.add(dataFormatter.formatCellValue(c).trim().toLowerCase());
        }

        while (it.hasNext()) {
            Row r = it.next();
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                Cell c = r.getCell(i);
                String val = "";
                if (c != null) {
                    // Formatea la celda a texto plano de manera segura y moderna
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
