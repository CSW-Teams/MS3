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
import { useTranslation } from 'react-i18next';

export default function UserActions() {
  const { t } = useTranslation();

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
      ...state,
      visible: !state.visible
    });
  };

  const handleLogout = () => {
    localStorage.removeItem("id");
    localStorage.removeItem("name");
    localStorage.removeItem("lastname");
    localStorage.removeItem("actor");
    localStorage.removeItem("tenant");
    localStorage.removeItem("jwt");
  };

  return (
    <NavItem tag={Dropdown} caret toggle={toggleUserActions}>
      <DropdownToggle caret tag={NavLink} className="text-nowrap px-3" style={{ cursor: "pointer" }}>
        <span className="d-none d-md-inline-block">{state.nome + " " + state.cognome}</span>
      </DropdownToggle>
      <Collapse tag={DropdownMenu} right small open={state.visible}>
        <DropdownItem tag={Link} to="/" className="text-danger" onClick={handleLogout}>
          <i className="material-icons text-danger">&#xE879;</i> {t('Logout')}
        </DropdownItem>
      </Collapse>
    </NavItem>
  );
}
