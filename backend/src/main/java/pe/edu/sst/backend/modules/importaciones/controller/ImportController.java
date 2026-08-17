package pe.edu.sst.backend.modules.importaciones.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.sst.backend.modules.importaciones.dto.ImportPreviewResult;
import pe.edu.sst.backend.modules.importaciones.service.ImportService;

@RestController
@RequestMapping("/api/v1/importaciones")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping(path = "/trabajadores")
    public ResponseEntity<ImportPreviewResult> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam(value = "month", required = false, defaultValue = "THIS") String month) throws Exception {
        ImportPreviewResult res = importService.previewImport(file, month);
        return ResponseEntity.ok(res);
    }

    @PostMapping(path = "/{id}/apply")
    public ResponseEntity<ImportPreviewResult> apply(@PathVariable("id") Long id) throws Exception{
        ImportPreviewResult res = importService.applyImport(id);
        return ResponseEntity.ok(res);
    }

    @GetMapping(path = "/{id}/issues")
    public ResponseEntity<java.util.List<pe.edu.sst.backend.modules.importaciones.entity.ImportRowIssue>> listIssues(@PathVariable("id") Long id){
        java.util.List<pe.edu.sst.backend.modules.importaciones.entity.ImportRowIssue> issues = importService.listIssues(id);
        return ResponseEntity.ok(issues);
    }

    @PostMapping(path = "/{id}/issues/{issueId}/resolve")
    public ResponseEntity<Void> resolveIssue(@PathVariable("id") Long id, @PathVariable("issueId") Long issueId, @RequestBody java.util.Map<String, String> body){
        String action = body.getOrDefault("action", "MANUAL_FIX");
        importService.resolveIssue(id, issueId, action);
        return ResponseEntity.noContent().build();
    }
}

