package com.appro.service;

import com.appro.dto.ImageDto;
import com.appro.dto.ProjectConfigDto;
import com.appro.dto.ProjectDto;
import com.appro.dto.ProjectDtoFullInfo;
import com.appro.entity.Project;
import com.appro.web.request.AddProjectRequest;

import java.util.List;

/**
 * Service interface for managing projects.
 */
public interface ProjectService {

    /**
     * Retrieves all projects, optionally sorted by a specified field and direction.
     *
     * @param sortBy        The field by which the results should be sorted.
     * @param sortDirection The direction of sorting (asc for ascending, desc for descending).
     * @return A list of ProjectDto objects representing the projects.
     */
    List<ProjectDto> findAll(String sortBy, String sortDirection);

//    /**
//     * Retrieves the project with the specified ID, returning a projection of the project.
//     *
//     * @param projectId The ID of the project to be retrieved.
//     * @return A ProjectDtoFullInfo object representing the project.
//     */
//    //ProjectDtoFullInfo findProjectById(Integer projectId);

    /**
     * Creates a new project based on the provided data transfer object.
     *
     * @param projectDto The data transfer object containing the details of the project to be created.
     * @return A ProjectDto object representing the created project.
     */
    ProjectDto create(ProjectDto projectDto);

    /**
     * Updates an existing project with the specified ID based on the provided data transfer object.
     *
     * @param id         The ID of the project to be updated.
     * @param projectDto The data transfer object containing the updated details of the project.
     * @return A ProjectDto object representing the updated project.
     */
    ProjectDto updateProject(int id, ProjectDto projectDto);

    /**
     * Marks the project with the specified ID as deleted.
     *
     * @param id The ID of the project to be marked as deleted.
     */
    void delete(int id);



    Project findProjectById(int id);

    ProjectDto findProjectFullInfo(int id);

}
