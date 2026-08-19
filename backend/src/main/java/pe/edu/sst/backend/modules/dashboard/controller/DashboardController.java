package pe.edu.sst.backend.modules.dashboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.sst.backend.config.constants.ApiPaths;
import pe.edu.sst.backend.modules.dashboard.dto.DashboardStatsResponse;
import pe.edu.sst.backend.modules.dashboard.service.DashboardService;

@RestController
@RequestMapping(ApiPaths.DASHBOARD )
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardStatsResponse> obtenerEstadisticas() {

        DashboardStatsResponse estadisticas =
                dashboardService.obtenerEstadisticas();

        return ResponseEntity.ok(estadisticas);
    }
}