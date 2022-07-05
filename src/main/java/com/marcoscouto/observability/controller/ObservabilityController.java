package com.marcoscouto.observability.controller;

import org.springframework.cloud.sleuth.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/observability")
public class ObservabilityController {

    private final Tracer tracer;

    public ObservabilityController(Tracer tracer) {
        this.tracer = tracer;
    }

    @GetMapping
    public ResponseEntity response() {
        var response = new HashMap();
        response.put("trace-id", tracer.currentSpan().context().traceId()); // trace id
        response.put("span-id", tracer.currentSpan().context().spanId()); // new span id
        response.put("parent-id", tracer.currentSpan().context().parentId()); // Parent span id
        return ResponseEntity.ok(response);
    }

}
