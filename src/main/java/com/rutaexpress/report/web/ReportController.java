package com.rutaexpress.report.web;

import com.rutaexpress.contracts.ApiPaths;
import com.rutaexpress.contracts.dto.KpiReportDto;
import com.rutaexpress.report.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.REPORTS)
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/kpis")
    public KpiReportDto kpis() {
        return service.kpis();
    }
}
