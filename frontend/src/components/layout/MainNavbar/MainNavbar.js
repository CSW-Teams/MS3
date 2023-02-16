import React from "react";
import PropTypes from "prop-types";
import classNames from "classnames";
import {Container, Nav, Navbar, NavbarBrand} from "shards-react";

import NavbarSearch from "./NavbarSearch";
import NavbarNav from "./NavbarNav/NavbarNav";
import NavbarToggle from "./NavbarToggle";
import {Link} from "@mui/icons-material";
import {MDBNavbarBrand} from "mdb-react-ui-kit";
import {FormatItalic} from "@material-ui/icons";

const MainNavbar = ({ layout, stickyTop }) => {
  const classes = classNames(
    "main-navbar",
    "bg-white",
    stickyTop && "sticky-top"
  );

  return (
    <div className={classes}>
      <Container className="p-0">
        <Navbar type="light" className="align-items-stretch flex-md-nowrap p-0" >
          <Container>
            <NavbarBrand className="mr-auto" href="/pianificazione-globale">
              <img src="https://s3-us-west-1.amazonaws.com/fraymework/multimedia/images/icons/apps/npicheck.png" style={{ height: 25 }} alt='logo' />
              {' '}
              <font style={{ fontWeight: 800 , fontstyle: FormatItalic } } >
                M3S : MEDICAL STAFF SHIFT SCHEDULER
              </font>
            </NavbarBrand>
          </Container>
          <NavbarSearch />
          <NavbarNav />
          <NavbarToggle />
        </Navbar>
      </Container>
    </div>
  );
};

MainNavbar.propTypes = {
  /**
   * The layout type where the MainNavbar is used.
   */
  layout: PropTypes.string,
  /**
   * Whether the main navbar is sticky to the top, or not.
   */
  stickyTop: PropTypes.bool
};

MainNavbar.defaultProps = {
  stickyTop: true
};

export default MainNavbar;
