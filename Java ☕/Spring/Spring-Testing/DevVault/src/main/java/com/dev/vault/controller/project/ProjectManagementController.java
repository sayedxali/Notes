package com.dev.vault.controller.project;

import com.dev.vault.helper.payload.group.ProjectDto;
import com.dev.vault.service.interfaces.project.ProjectManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing projects and groups.
 */
@RestController
@RequestMapping("/api/v1/proj_leader")
@RequiredArgsConstructor
public class ProjectManagementController {

    private final ProjectManagementService projectManagementService;

    /**
     * Endpoint for creating a new project.
     *
     * @param projectDto the project details to create
     * @return a ResponseEntity containing the created project and an HTTP status code of 201 (CREATED)
     */
    @PreAuthorize("hasAnyRole('PROJECT_LEADER', 'TEAM_MEMBER', 'PROJECT_ADMIN')")
    @PostMapping({"/create-project", "/create-group"})
    public ResponseEntity<?> createProject(@RequestBody ProjectDto projectDto) {
        return new ResponseEntity<>(projectManagementService.createProject(projectDto), HttpStatus.CREATED);
    }
}
