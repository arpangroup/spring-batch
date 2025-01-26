package com.arpan.rest_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrichmentController {
    private int count = 0;

    @GetMapping
    public String enrich() {
        if (Math.random() > .2) {
            throw new RuntimeException("I screwed up");
        } else {
            this.count ++;
            return  String.format("Enriched %s", this.count);
        }
    }
}
