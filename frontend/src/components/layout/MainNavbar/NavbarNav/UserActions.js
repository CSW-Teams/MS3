import React from "react";
import {Link} from "react-router-dom";
import {
  Collapse,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownToggle,
  NavItem,
  NavLink
} from "shards-react";
import {useTranslation} from 'react-i18next';
import {LogoutAPI} from "../../../../API/LogoutAPI";

export default function UserActions() {
  const {t} = useTranslation();

  const [state, setState] = React.useState({
    visible: false,
    nome: localStorage.getItem("name"),
    cognome: localStorage.getItem("lastname"),
  });

  React.useEffect(() => {
    setState({
      ...state,
      nome: localStorage.getItem("name"),
      cognome: localStorage.getItem("lastname")
    });
  }, []);

  const toggleUserActions = () => {
    setState({
      ...state, visible: !state.visible
    });
  };

  const handleLogout = async () => {
    const token = localStorage.getItem("jwt");

    if (token) {
      const logoutApi = new LogoutAPI();
      try {
        await logoutApi.postLogout(token);
      } catch (e) {
        console.error("Logout failed", e);
      }
    }
    localStorage.removeItem("id");
    localStorage.removeItem("name");
    localStorage.removeItem("lastname");
    localStorage.removeItem("actor");
    localStorage.removeItem("tenant");
    localStorage.removeItem("jwt");
  };

  return (<NavItem tag={Dropdown} caret toggle={toggleUserActions}>
      <DropdownToggle caret tag={NavLink} className="text-nowrap px-3"
                      style={{cursor: "pointer"}}>
        <span
          className="d-none d-md-inline-block">{state.nome + " " + state.cognome}</span>
      </DropdownToggle>
      <Collapse tag={DropdownMenu} right small open={state.visible}>
        <DropdownItem tag={Link} to='/personal-single-user-profile/'>
          <i className="material-icons">&#xE7FD;</i> {t('Profile')}
        </DropdownItem>
        <DropdownItem tag={Link} to="/cambia-password/">
          <i className="material-icons">password</i> {t('Change Password')}
        </DropdownItem>
        <DropdownItem tag={Link} to="/preference">
          <i className="material-icons">&#xE8B8;</i> {t('Manage Preferences')}
        </DropdownItem>
        <DropdownItem tag={Link} to="/" className="text-danger"
                      onClick={handleLogout}>
          <i className="material-icons text-danger">&#xE879;</i> {t('Logout')}
        </DropdownItem>
      </Collapse>
    </NavItem>);
}
