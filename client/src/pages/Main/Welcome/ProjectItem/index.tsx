import classes from './ProjectItem.module.scss';
import Overlay from '../../../../components/UI/Overlay';
import ProjectDetails from '../ProjectDetails';
import React from 'react';
import { Project } from '../../../../entity/Project';

interface PropsType {
  project: Project;
}

const ProjectItem = ({ project }: PropsType) => {
  return (
    <div>
      <div className={classes['welcome__project-image']}>
        <img src={project.mainImage} alt="slide 1" />
        <Overlay />
      </div>
      <div className={classes['welcome__project-details-wrapper']}>
        <ProjectDetails project={project} />
      </div>
    </div>
  );
};

export default ProjectItem;
