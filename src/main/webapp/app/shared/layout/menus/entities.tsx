import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';

import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown icon="th-list" name="Entities" id="entity-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
    <MenuItem icon="asterisk" to="/unit">
      Unit
    </MenuItem>
    <MenuItem icon="asterisk" to="/framework">
      Framework
    </MenuItem>
    <MenuItem icon="asterisk" to="/action-comment">
      Action Comment
    </MenuItem>
    <MenuItem icon="asterisk" to="/status">
      Status
    </MenuItem>
    <MenuItem icon="asterisk" to="/action-attachment">
      Action Attachment
    </MenuItem>
    <MenuItem icon="asterisk" to="/audit-report">
      Audit Report
    </MenuItem>
    <MenuItem icon="asterisk" to="/audit-recomm">
      Audit Recomm
    </MenuItem>
    <MenuItem icon="asterisk" to="/ics-recomm">
      ICS Recomm
    </MenuItem>
    <MenuItem icon="asterisk" to="/audit-sub-recomm">
      Audit Sub Recomm
    </MenuItem>
    <MenuItem icon="asterisk" to="/action">
      Action
    </MenuItem>
    <MenuItem icon="asterisk" to="/action-change-mail">
      Action Change Mail
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
