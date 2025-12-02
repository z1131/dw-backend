package com.deepwrite.api.service;

import com.deepwrite.api.dto.ProjectDTO;
import com.deepwrite.common.model.Response;

public interface ProjectAppService {
    Response<ProjectDTO> createProject(Long userId);
    Response<ProjectDTO> getProject(Long id);
}
