package com.ratnesh.buildly.service;

import com.ratnesh.buildly.dto.project.FileContentResponse;
import com.ratnesh.buildly.dto.project.FileNode;
import com.ratnesh.buildly.dto.project.FileTreeResponse;

import java.util.List;

public interface ProjectFileService {
    FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
