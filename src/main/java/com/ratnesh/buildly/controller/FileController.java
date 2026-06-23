package com.ratnesh.buildly.controller;

import com.ratnesh.buildly.dto.project.FileContentResponse;
import com.ratnesh.buildly.dto.project.FileNode;
import com.ratnesh.buildly.dto.project.FileTreeResponse;
import com.ratnesh.buildly.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final ProjectFileService fileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId){
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }


    @GetMapping("/content")
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @RequestParam String path) {
        return ResponseEntity.ok(fileService.getFileContent(projectId, path));
    }
}
