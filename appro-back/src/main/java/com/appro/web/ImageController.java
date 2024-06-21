package com.appro.web;

import com.appro.dto.ImageInfo;
import com.appro.service.ImageService;
import com.appro.web.handler.TooManyItemsException;
import com.appro.web.request.AddImagesRequest;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class ImageController {

    @Value("${images.size.filter}")
    private int imagesSizeFilter;

    private final ImageService imageService;

    @Operation(summary = "Upload list of images")
    @PostMapping("/images") // +
    public List<ImageInfo> uploadImages(@ModelAttribute AddImagesRequest imagesRequest) {
        validateImagesSize(imagesRequest.images().size());
        return imageService.saveImages(imagesRequest.images(), imagesRequest.type());
    }

    private void validateImagesSize(int size) {
        if (size > imagesSizeFilter) {
            throw new TooManyItemsException(size);
        }
    }
}