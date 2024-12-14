import React from "react";
import PropTypes from "prop-types";
import { Container, Row, Nav, NavItem, NavLink } from "shards-react";
import { Link } from "react-router-dom";

const MainFooter = ({ contained, copyright }) => (
  <footer className="main-footer d-flex p-2 px-3 bg-white border-top" >
    <style>
      {`
        .main-footer {
          position: sticky;
          bottom: 0;
          z-index: 9998;
          width: 100%;
        }
          `}
    </style>
    <Container fluid={contained}>
      <Row>
        <span className="copyright ml-auto my-auto mr-2">{copyright}</span>
      </Row>
    </Container>
  </footer>

);

MainFooter.propTypes = {
  /**
   * Whether the content is contained, or not.
   */
  contained: PropTypes.bool,
  /**
   * The menu items array.
   */
  menuItems: PropTypes.array,
  /**
   * The copyright info.
   */
  copyright: PropTypes.string
};

MainFooter.defaultProps = {
  contained: false,
  copyright: "Copyright © 2022 SprintFloyd - © 2023 Scrumtastic - © 2024 Shifts happen ",
};

export default MainFooter;
