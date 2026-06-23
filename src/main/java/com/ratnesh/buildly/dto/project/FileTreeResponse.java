package com.ratnesh.buildly.dto.project;

import java.util.List;

public record FileTreeResponse(
        List<FileNode> files
) {
}
