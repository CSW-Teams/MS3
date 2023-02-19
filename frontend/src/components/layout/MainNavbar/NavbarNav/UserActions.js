import React from "react";
import { Link } from "react-router-dom";
import {
  Dropdown,
  DropdownToggle,
  DropdownMenu,
  DropdownItem,
  Collapse,
  NavItem,
  NavLink
} from "shards-react";

export default class UserActions extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      visible: false,
      nome : localStorage.getItem("nome"),
      cognome: localStorage.getItem("cognome"),
    };

    this.toggleUserActions = this.toggleUserActions.bind(this);
  }

  async componentDidMount() {
    this.setState({
      nome: localStorage.getItem("nome"),
      cognome : localStorage.getItem("cognome")
    })
  }

    toggleUserActions() {
    this.setState({
      visible: !this.state.visible
    });
  }

  render() {
    return (
      <NavItem tag={Dropdown} caret toggle={this.toggleUserActions}>
        <DropdownToggle caret tag={NavLink} className="text-nowrap px-3">

          <span className="d-none d-md-inline-block">{this.state.nome+" "+this.state.cognome}</span>
        </DropdownToggle>
        <Collapse tag={DropdownMenu} right small open={this.state.visible}>
          <DropdownItem tag={Link} to="/user-profile/1">
            <i className="material-icons">&#xE7FD;</i> Profilo
          </DropdownItem>
          <DropdownItem tag={Link} to="/cambia-password/">
            <i className="material-icons">password</i> Modifica Password
          </DropdownItem>
          <DropdownItem tag={Link} to="/preference">
            <i className="material-icons">&#xE8B8;</i> Modifica Preferenze
          </DropdownItem>
          <DropdownItem tag={Link} to="/" className="text-danger">
            <i className="material-icons text-danger">&#xE879;</i> Logout
          </DropdownItem>
        </Collapse>
      </NavItem>
    );
  }
}
