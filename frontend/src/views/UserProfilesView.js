import React from "react";
import {UtenteAPI} from "../API/UtenteAPI";
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBTable, MDBTableBody,
  MDBTableHead
} from "mdb-react-ui-kit";
import {Button, Link} from "@material-ui/core";

function defaultComparator(prop1, prop2){
  if (prop1 < prop2)
    return -1;
  if (prop1 > prop2)
    return 1;
  return 0;
}

export default class UserProfilesView extends React.Component{
  constructor(props) {
    super(props);
    this.state = {
      utenti: [],
      orderBy: "nome",
      comparator: defaultComparator
    }
    this.setOrderBy = this.setOrderBy.bind(this);
  }

  /**
   * Cambia la proprietà degli utenti per cui si vuole ordinare,
   * usando un comparatore di default
   */
  setOrderBy(userProp){
    this.setState({
      orderBy: userProp,
      comparator: defaultComparator
    })
  }

    /**
   * Cambia la proprietà degli utenti per cui si vuole ordinare,
   * specificando un comparatore custom
   */
    setOrderByAndComparator(userProp, comparator){
      this.setState({
        orderBy: userProp,
        comparator: comparator
      })
    }


  async componentDidMount() {
    let utenti = await(new UtenteAPI().getAllUsersInfo());

    this.setState({
      utenti : utenti,
    })

  }
  render() {

    // Ordina gli utenti in base alla proprietà specificata.
    // È possibile specificare la proprietà cliccando sulla colonna corrispondente.
    console.log(this.state)
    this.state.utenti.sort((u1, u2) => {

      let p1 = u1[this.state.orderBy];
      let p2 = u2[this.state.orderBy];

      return this.state.comparator(p1, p2);

    })

    return(
      <MDBCard>
        <MDBCardBody className="text-center">
          <MDBCardTitle>Informazioni Utenti</MDBCardTitle>
          <MDBTable align="middle"
                    striped
                    bordered
                    small >
            <MDBTableHead>
              <tr>
                <th scope='col' onClick={() => this.setOrderBy("nome")} > Nome </th>
                <th scope='col' onClick={() => this.setOrderBy("cognome")} >Cognome</th>
                <th scope='col' onClick={() => this.setOrderBy("dataNascita")} >Data Nascita</th>
                <th scope='col' onClick={() => this.setOrderBy("ruoloEnum")} >Ruolo</th>
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
                    <td>
                      <Button className="overlay" variant="primary" href={`/profilo-utente/${data.id}`}>
                       <i className="fas fa-edit fa-lg"> </i>
                        </Button></td>
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
