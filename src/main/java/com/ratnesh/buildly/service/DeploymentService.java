package com.ratnesh.buildly.service;

import com.ratnesh.buildly.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);
}
