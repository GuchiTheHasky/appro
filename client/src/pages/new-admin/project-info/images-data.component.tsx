import React, {FC, useEffect} from "react";
import {ProjectProps} from "@/pages/new-admin/project-info/model";
import {Divider, Grid, IconButton, ImageList, ImageListItem, ImageListItemBar, Typography} from "@mui/material";
import FileProperty from "@/pages/Admin/ViewAddEditProject/FileProperty";
import delete_icon from "@/assets/img/admin/delete.svg";

import {useSaveImages} from "@/api/useSaveImages";
import {ImageInfo} from "@/api/model";

export const ImageData: FC<ProjectProps> = ({mode, state, dispatch}) => {
    const view = mode === 'view';
    const addNew = false;

    const {mutate: saveImages, data: imagesPreview} = useSaveImages();

    useEffect(() => {
        if (imagesPreview) {
            const {images} = state;
            dispatch({type: 'images', payload: images ? [...images, ...imagesPreview] : imagesPreview});
        }
    }, [imagesPreview]);

    const handleImagesChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        saveImages({images: event.target.files})
    }

    const handlePhotosChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        dispatch({type: 'photosToAdd', payload: event.target.files});
    }

    const handleImageRemove = (imageSrc: string) => {
        const images = state.images;

        if (images) {
            const newImages = images.filter((i: ImageInfo) => i.path !== imageSrc);
            dispatch({type: 'images', payload: newImages});
        }
    }

    const handlePhotoRemove = (photoSrc: string) => {
        const photos = state.photos;
        const photosToDelete = state.photosToDelete || [];
        if (photos) {
            const newPhotos = photos.filter((i: ImageInfo) => i.path !== photoSrc);
            photosToDelete.push(photoSrc);
            dispatch({type: 'photos', payload: newPhotos});
            dispatch({type: 'photosToDelete', payload: photosToDelete});
        }
    }

    const handleMainImageChange = (event: React.ChangeEvent<any>) => {

        dispatch({type: 'mainImage', payload: event.target.files[0]});
    }

    const handleMainImageRemove = () => {
        const mainImage = state.mainImage;
        const imagesToDelete = state.imagesToDelete || [];
        // @ts-ignore
        imagesToDelete.push(mainImage);
        dispatch({type: 'mainImage', payload: null});
        dispatch({type: 'imagesToDelete', payload: imagesToDelete});
    }


    return (
        <Grid container>
            <Grid item xs={12}>
                <Typography>Основне зображення</Typography>
                <ProjectImage
                    images={state.mainImage ? [state.mainImage.path] : null}
                    title={'Загрузить основное изображения проекта'}
                    isMain={true}
                    required={true}
                    disabled={view}
                    handleAddImage={handleMainImageChange}
                    handleRemoveImage={handleMainImageRemove}
                />
            </Grid>
            <Grid item xs={12}>
                <Typography>Зображення проекту</Typography>
                {/*{ListImage(state.images ? state.images.map((i: any) => i.path) : [], view, handleImageRemove)}*/}
            </Grid>
            <Divider/>
            <Grid item xs={12}>
                <ProjectImage
                    images={addNew ? null : state.images.map((i: any) => i.path)}
                    title={'Загрузить изображения проекта'}
                    required={true}
                    multiple={true}
                    disabled={view}
                    handleAddImage={handleImagesChange}
                    handleRemoveImage={handleImageRemove}
                />
            </Grid>
            {/*{*/}
            {/*    state.projectConfig?.isFinished && (*/}
            {/*        <Grid item xs={12}>*/}
            {/*            <ProjectImage*/}
            {/*                images={addNew ? null : state.photos.map((i: any) => i.path)}*/}
            {/*                title={'Загрузить фото готового проекта'}*/}
            {/*                required={false}*/}
            {/*                multiple={true}*/}
            {/*                disabled={view}*/}
            {/*                handleAddImage={handlePhotosChange}*/}
            {/*                handleRemoveImage={handlePhotoRemove}*/}
            {/*            />*/}
            {/*        </Grid>*/}
            {/*    )*/}
            {/*}*/}
        </Grid>)
}


interface Props {
    images: string[] | null | undefined;
    title: string;
    required?: boolean;
    multiple?: boolean;
    disabled?: boolean;
    isMain?: boolean;

    handleAddImage(event: React.ChangeEvent<any>): void;

    handleRemoveImage(id: string | number): void;
}

const ProjectImage = ({
                          images,
                          title,
                          required,
                          multiple,
                          disabled,
                          isMain,
                          handleAddImage,
                          handleRemoveImage
                      }: Props) => {
    if (images && isMain) {
        return ListImage(images, disabled, handleRemoveImage)
    }
    return (
        <>
            <FileProperty
                title={title}
                required={required}
                disabled={disabled}
                multiple={multiple}
                handleProperty={handleAddImage}
            />
            {images && ListImage(images, disabled, handleRemoveImage)}

        </>
    )
}


export const ListImage = (
    images: string[],
    disabled: boolean | undefined,
    handleRemoveImage: (id: string | number) => void
) => {
    return (
        <ImageList cols={3}>
            {images.map((item, index) => (
                <ImageListItem key={item + index}>
                    <img src={item} alt={item} loading='lazy'/>
                    {!disabled && (
                        <ImageListItemBar
                            position='top'
                            actionIcon={
                                <IconButton
                                    style={{width: 40, height: 40}}
                                    disabled={disabled}
                                    onClick={() => handleRemoveImage(item)}
                                >
                                    <img src={delete_icon}/>
                                </IconButton>
                            }
                            actionPosition='left'
                        />
                    )}
                </ImageListItem>
            ))}
        </ImageList>
    )
}