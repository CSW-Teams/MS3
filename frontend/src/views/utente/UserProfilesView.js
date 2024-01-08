import React from "react";
import {UserAPI} from "../../API/UserAPI";
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
      orderBy: "name",
      comparator: defaultComparator,
      attore : localStorage.getItem("actor"),
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
    let utenti = await(new UserAPI().getAllUsersInfo());

    this.setState({
      utenti : utenti,
    })

  }


  render() {

    // Ordina gli utenti in base alla proprietà specificata.
    // È possibile specificare la proprietà cliccando sulla colonna corrispondente.
    this.state.utenti.sort((u1, u2) => {

      let p1 = u1[this.state.orderBy];
      let p2 = u2[this.state.orderBy];


      return this.state.comparator(p1, p2);

    })

    // Needed to show system actors in italian on all user view
    for(var i = 0;i<this.state.utenti.length;i++){
      for(var j = 0;j<this.state.utenti[i].systemActors.length;j++){
        this.state.utenti[i].systemActors[j] = (
          (this.state.utenti[i].systemActors[j] === "PLANNER") ? "Pianificatore" :
            (this.state.utenti[i].systemActors[j] === "CONFIGURATOR") ? "Configuratore" :
              "Dottore"
        );
      }
    }

    return(
      <MDBCard>
        <MDBCardBody className="text-center">
          <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                      <MDBCardTitle style={{ marginLeft: "auto", marginBottom: 10 }}>Informazioni Utenti</MDBCardTitle>
                      {this.state.attore === "CONFIGURATOR" && (
                        <Button variant="contained" color="primary" href="/nuovo-utente" style={{ marginLeft: 450, marginBottom: 10 }}>
                          Registra nuovo utente
                        </Button>
                      )}
                    </div>
          <MDBTable align="middle"
                    bordered
                    small
                    hover >
            <MDBTableHead color='tempting-azure-gradient' textwhite>
              <tr>
                <th scope='col' onClick={() => this.setOrderBy("name")} > Nome </th>
                <th scope='col' onClick={() => this.setOrderBy("lastname")} >Cognome</th>
                <th scope='col' onClick={() => this.setOrderBy("birthday")} >Data Nascita</th>
                <th scope='col' onClick={() => this.setOrderBy("systemActors")} >Attore</th>
                {this.state.attore!=="PLANNER" && <th scope='col'>Info</th>}
                {this.state.attore==="PLANNER" && <th scope='col'>Modifica</th>}

              </tr>
            </MDBTableHead>
            <MDBTableBody>
              {this.state.utenti.map((data, key) => {
                return (
                  <tr key={key}>
                    <td>{data.name}</td>
                    <td>{data.lastname}</td>
                    <td>{data.birthday}</td>
                    <td>{data.systemActors.join(", ")}</td>
                    {this.state.attore==="UTENTE" && <td><Button className="overlay" variant="primary" href={`/profilo-utente/${data.id}`}><i className="fa fa-id-card"> </i></Button></td>}
                    {this.state.attore!=="UTENTE" && <td><Button className="overlay" variant="primary" href={`/profilo-utente/${data.id}`}><i className="fas fa-edit fa-lg"> </i></Button></td>}
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
