import React from "react";
import PropTypes from "prop-types";
import {Container, Navbar, NavbarBrand} from "shards-react";

import { Dispatcher, Constants } from "../../../flux";
import {FormatItalic} from "@material-ui/icons";

class SidebarMainNavbar extends React.Component {
  constructor(props) {
    super(props);

    this.handleToggleSidebar = this.handleToggleSidebar.bind(this);
  }

  handleToggleSidebar() {
    Dispatcher.dispatch({
      actionType: Constants.TOGGLE_SIDEBAR
    });
  }

  render() {
    return (
      <div className="main-navbar">
        <Navbar
          className="align-items-stretch bg-white flex-md-nowrap border-bottom p-0"
          type="light"
        >
          <Container>
            <NavbarBrand className="mr-auto" href="/pianificazione-globale">
              <img src="https://s3-us-west-1.amazonaws.com/fraymework/multimedia/images/icons/apps/npicheck.png" style={{ height: 25 }} alt='logo' />
              {' '}
              <font style={{ fontWeight: 800 , fontstyle: FormatItalic } } >
                Menu
              </font>
            </NavbarBrand>
          </Container>

          {/* eslint-disable-next-line */}
          <a
            className="toggle-sidebar d-sm-inline d-md-none d-lg-none"
            onClick={this.handleToggleSidebar}
          >
            <i className="material-icons">&#xE5C4;</i>
          </a>
        </Navbar>
      </div>
    );
  }
}

SidebarMainNavbar.propTypes = {
  /**
   * Whether to hide the logo text, or not.
   */
  hideLogoText: PropTypes.bool
};

SidebarMainNavbar.defaultProps = {
  hideLogoText: false
};

export default SidebarMainNavbar;
