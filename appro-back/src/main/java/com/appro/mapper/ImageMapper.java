package com.appro.mapper;

import com.appro.dto.ImageDto;
import com.appro.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    Image toImage(ImageDto imageDto);

    ImageDto toDto(Image image);

    List<ImageDto> toDtoList(List<Image> images);
}
