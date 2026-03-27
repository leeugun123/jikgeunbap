package com.jikgeunbap.workplace.controller;

import com.jikgeunbap.workplace.dto.WorkplaceRequest;
import com.jikgeunbap.workplace.dto.WorkplaceResponse;
import com.jikgeunbap.workplace.service.WorkplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workplace")
@RequiredArgsConstructor
public class WorkplaceController {

    private final WorkplaceService service;

    @GetMapping
    public WorkplaceResponse get() {
        return service.get();
    }

    @PutMapping
    public WorkplaceResponse set(@RequestBody WorkplaceRequest request) {
        return service.set(request);
    }
}

