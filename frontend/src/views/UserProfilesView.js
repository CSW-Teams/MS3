import React from "react";
import {UtenteAPI} from "../API/UtenteAPI";
import {CategoriaUtenteAPI} from "../API/CategoriaUtenteAPI";
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBTable, MDBTableBody,
  MDBTableHead
} from "mdb-react-ui-kit";
import {Button} from "@material-ui/core";


export default class UserProfilesView extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      utenti: []
    }
  }

    async componentDidMount() {
    let utenti = await(new UtenteAPI().getAllUsersInfo());
    this.setState({
      utenti : utenti,
    })

  }
  render() {
    return(
      <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>Informazioni Utenti</MDBCardTitle>
          <MDBTable align="middle" >
            <MDBTableHead>
              <tr>
                <th scope='col'>Nome</th>
                <th scope='col'>Cognome</th>
                <th scope='col'>Data Nascita</th>
                <th scope='col'>Ruolo</th>
                <th scope='col'>Modifica</th>
              </tr>
            </MDBTableHead>
            <MDBTableBody>
              {this.state.utenti.map((data, key) => {
                return (
                  <tr key={key}>
                    <td>{data.nome}</td>
                    <td>{data.cognome}</td>
                    <td>{data.dataNascita}</td>
                    <td>{data.ruoloEnum}</td>
                    <td><Button href=""> <i className="far fa-edit mb-5"></i></Button></td>
                  </tr>
                )
              })}
            </MDBTableBody>
          </MDBTable>
        </MDBCardBody>
      </MDBCard>
    );
  }
  }
