package com.appro.service.impl;

import com.appro.dto.ImageInfo;
import com.appro.dto.ProjectDto;
import com.appro.entity.Floor;
import com.appro.entity.Image;
import com.appro.entity.Project;
import com.appro.exception.ProjectNotFoundException;
import com.appro.mapper.FloorMapper;
import com.appro.mapper.ImageMapper;
import com.appro.mapper.ProjectMapper;
import com.appro.repository.FloorRepository;
import com.appro.repository.ProjectRepository;
import com.appro.service.ImageService;
import com.appro.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultProjectService implements ProjectService {

    private static final List<String> SORTABLE_FIELDS = List.of("id", "popularity", "generalArea", "projectPrice");

    private static final String CREATED_AT = "createdAt";

    private final ImageService imageService;

    private final ProjectMapper projectMapper;
    private final FloorMapper floorMapper;
    private final ImageMapper imageMapper;

    private final ProjectRepository projectRepository;
    private final FloorRepository floorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAll(String sortBy, String sortDirection) {
        Direction direction = Direction.fromString(sortDirection);

        if (isSortableField(sortBy)) {
            return projectMapper.toProjectsDto(projectRepository.findAll(Sort.by(direction, sortBy)));
        }
        return projectMapper.toProjectsDto(projectRepository.findAll(Sort.by(direction, CREATED_AT)));
    }

    @Override
    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        Project currentProject;
        Project project = projectMapper.toProjectWithOutDetachedEntities(projectDto); // is deleted = null
        currentProject = projectRepository.save(project);

        updateImagesAndFloors(currentProject, projectDto);

        Project projectToSave = projectRepository.save(currentProject);
        log.info("Created project with id: {}", projectToSave.getId());
        return projectMapper.toProjectDto(projectToSave);

    }

    @Override
    @Transactional
    public ProjectDto updateProject(int id, ProjectDto projectDto) {
        Project project = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new ProjectNotFoundException(projectDto.getId()));

        project = projectMapper.update(project, projectDto);

        updateImagesAndFloors(project, projectDto);

        Project projectToSave = projectRepository.save(project);
        log.info("Updated project with id: {}", projectToSave.getId());
        return projectMapper.toProjectDto(projectToSave);
    }

    @Override
    @Transactional
    public void delete(int id) {
        Project project = findProjectById(id);

        project.setIsDeleted(true);
        log.info("Deleted project with id: {}", id);
        projectRepository.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findProjectFullInfo(int id) {
        Project project = findProjectById(id);
        return projectMapper.toProjectDto(project);
    }

    Project findProjectById(int id) {
        Optional<Project> optionalProject = projectRepository.findById(id);
        return optionalProject.orElseThrow(() -> new ProjectNotFoundException(id));
    }

    boolean isSortableField(String sortBy) {
        return sortBy != null && SORTABLE_FIELDS.contains(sortBy);
    }

    private Project updateImagesAndFloors(Project currentProject, ProjectDto projectDto) {
        log.info("Updating images and floors for project with ID: {}", currentProject.getId());
        updateImages(currentProject, projectDto);
        return updateFloors(currentProject, projectDto);
    }

    private void updateImages(Project currentProject, ProjectDto projectDto) {
        log.info("Updating images for project with ID: {}", currentProject.getId());

        Image mainImage = updateMainImage(currentProject, projectDto);

        List<ImageInfo> newImages = projectDto.getImages();
        if (newImages != null) {
            if (currentProject.getImages() == null) {
                currentProject.setImages(new ArrayList<>());
            }
            List<Image> currentImages = currentProject.getImages().stream()
                    .filter(image -> !"main".equals(image.getType()))
                    .collect(Collectors.toList());

            List<Image> imagesToAdd = imageService.processNewAndOldImages(newImages, currentImages);
            imagesToAdd.forEach(image -> {
                image.setProject(currentProject);
                imageService.save(image);
            });

            // Add main image if it was updated
            if (mainImage != null) {
                imagesToAdd.add(mainImage);
            }

            // Update project's images
            currentProject.setImages(imagesToAdd);
            log.info("Updated images for project with ID: {}", currentProject.getId());
        }
    }

    private Image updateMainImage(Project currentProject, ProjectDto projectDto) {
        log.info("Updating main image for project with ID: {}", currentProject.getId());
        // update main image
        ImageInfo newMainImageInfo = projectDto.getMainImage();
        Image mainImage = null;
        Image currentMainImage = imageService.findMainImage(currentProject.getId());

        if (newMainImageInfo != null) {
            if (currentMainImage != null) {
                currentMainImage.setType("image");
                imageService.save(currentMainImage);
            }
            mainImage = imageMapper.toImage(newMainImageInfo);
            mainImage.setProject(currentProject);
            mainImage.setType("main");
            imageService.save(mainImage);
            log.info("Main image updated for project with ID: {}", currentProject.getId());
        }
        return mainImage;
    }

    private Project updateFloors(Project currentProject, ProjectDto projectDto) {
        log.info("Updating floors for project with ID: {}", currentProject.getId());

        List<Floor> newFloors = projectDto.getFloors().stream()
                .map(floorDto -> {
                    Floor floor = floorMapper.toFloor(floorDto);
                    floor.setProject(currentProject);

                    if (floorDto.getPlanningImage() != null) {
                        Image planningImage = imageService.findById(floorDto.getPlanningImage().getId());
                        floor.setPlanningImage(planningImage);
                    }
                    return floor;
                })
                .collect(Collectors.toList());

        if (currentProject.getFloors() != null) {
            currentProject.getFloors().clear();
        }

        for (Floor floor : newFloors) {
            if (floor.getPlanningImage() != null) {
                Image planningImage = imageService.findById(floor.getPlanningImage().getId());
                floor.setPlanningImage(planningImage);
            }
            floorRepository.save(floor);
        }

        if (currentProject.getFloors() != null) {
            currentProject.getFloors().addAll(newFloors);
        } else {
            currentProject.setFloors(new ArrayList<>());
            currentProject.getFloors().addAll(newFloors);
        }

        log.info("Updated floors for project with ID: {}", currentProject.getId());
        return currentProject;
    }

}
