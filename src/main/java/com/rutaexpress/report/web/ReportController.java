package com.rutaexpress.report.web;

import com.rutaexpress.contracts.ApiPaths;
import com.rutaexpress.contracts.dto.HourlyBucketDto;
import com.rutaexpress.contracts.dto.KpiReportDto;
import com.rutaexpress.contracts.dto.LeadTimeDto;
import com.rutaexpress.contracts.dto.TopShipmentDto;
import com.rutaexpress.report.service.ReportService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.REPORTS)
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @GetMapping("/kpis")
    public KpiReportDto kpis(@RequestParam(required = false) Integer range) {
        return range == null ? service.kpis() : service.kpis(range);
    }

    @GetMapping("/hourly")
    public List<HourlyBucketDto> hourly(@RequestParam(defaultValue = "24") int range) {
        return service.hourly(range);
    }

    @GetMapping("/leadtime")
    public LeadTimeDto leadtime(@RequestParam(defaultValue = "24") int range) {
        return service.leadtime(range);
    }

    @GetMapping("/top-services")
    public List<TopShipmentDto> topServices(@RequestParam(defaultValue = "24") int range,
            @RequestParam(defaultValue = "10") int limit) {
        return service.topServices(range, limit);
    }
}
