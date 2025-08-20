import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/app-user">
        <Translate contentKey="global.menu.entities.appUser" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/category">
        <Translate contentKey="global.menu.entities.category" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/course">
        <Translate contentKey="global.menu.entities.course" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/lesson">
        <Translate contentKey="global.menu.entities.lesson" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/quiz">
        <Translate contentKey="global.menu.entities.quiz" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/assignment">
        <Translate contentKey="global.menu.entities.assignment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/enrollment">
        <Translate contentKey="global.menu.entities.enrollment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/assignment-submission">
        <Translate contentKey="global.menu.entities.assignmentSubmission" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/quiz-attempt">
        <Translate contentKey="global.menu.entities.quizAttempt" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/certificate">
        <Translate contentKey="global.menu.entities.certificate" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/review">
        <Translate contentKey="global.menu.entities.review" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/comment">
        <Translate contentKey="global.menu.entities.comment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/blog-post">
        <Translate contentKey="global.menu.entities.blogPost" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
