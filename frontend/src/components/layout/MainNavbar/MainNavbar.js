import React from "react";
import PropTypes from "prop-types";
import classNames from "classnames";
import {Container, Nav, Navbar, NavbarBrand} from "shards-react";

import NavbarSearch from "./NavbarSearch";
import NavbarNav from "./NavbarNav/NavbarNav";
import NavbarToggle from "./NavbarToggle";
import {CenterFocusStrong, FormatItalic} from "@material-ui/icons";
import {
  AlignHorizontalCenter,
  AlignHorizontalCenterRounded
} from "@mui/icons-material";
import {MDBCard, MDBContainer} from "mdb-react-ui-kit";
import {ContainerClasses} from "@mui/material";

const MainNavbar = ({ layout, stickyTop }) => {
  const classes = classNames(
    "main-navbar",
    "bg-white",
    stickyTop && "sticky-top"
  );

  // Usa useState per memorizzare i dati estratti da localStorage
  const [hospitalName, setHospitalName] = useState(null);

  // Usa useEffect per leggere i dati dal localStorage al caricamento del componente
  useEffect(() => {
    const storedHospitalName = localStorage.getItem("tenant"); // Supponiamo di aver memorizzato questo valore prima
    if (storedHospitalName) {
      setHospitalName(storedHospitalName); // Imposta il valore letto nel componente
    }
  }, []); // L'array vuoto assicura che questo venga eseguito solo una volta, al caricamento del componente.

  return (
    <div className={classes}>
      <Container className="p-0">
        <Navbar type="light" className="align-items-stretch flex-md-nowrap p-0" >
          <MDBContainer  className="flex-center" style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }} >
            <NavbarBrand className="mr-auto" href="/pianificazione-globale">
              <font style={{ fontWeight: 800 , fontstyle: FormatItalic ,textAlign: AlignHorizontalCenterRounded} } >
                MEDICAL STAFF SHIFT SCHEDULER - {hospitalName}
              </font>
            </NavbarBrand>
          </MDBContainer>
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
