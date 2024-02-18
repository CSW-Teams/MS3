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
import { t } from "i18next";
import {toast} from "react-toastify";
import {panic} from "../../components/common/Panic";

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
    try {

      let utenti = await(new UserAPI().getAllUsersInfo());

      this.setState({
        utenti : utenti,
      })
    } catch (err) {

      panic()
    }

  }


  render() {

    // Ordina gli utenti in base alla proprietà specificata.
    // È possibile specificare la proprietà cliccando sulla colonna corrispondente.
    this.state.utenti.sort((u1, u2) => {

      let p1 = u1[this.state.orderBy];
      let p2 = u2[this.state.orderBy];


      return this.state.comparator(p1, p2);

    })

    return(
      <MDBCard>
        <MDBCardBody className="text-center">
          <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                      <MDBCardTitle style={{ marginLeft: "auto", marginBottom: 10 }}>{t("User Information")}</MDBCardTitle>
                      {this.state.attore === "CONFIGURATOR" && (
                        <Button variant="contained" color="primary" href="/nuovo-utente" style={{ marginLeft: 450, marginBottom: 10 }}>
                          {t("Register New User")}
                        </Button>
                      )}
                    </div>
          <MDBTable align="middle"
                    bordered
                    small
                    hover >
            <MDBTableHead color='tempting-azure-gradient' textwhite>
              <tr>
                <th scope='col' onClick={() => this.setOrderBy("name")} >{t("Name")}</th>
                <th scope='col' onClick={() => this.setOrderBy("lastname")} >{t("Surname")}</th>
                <th scope='col' onClick={() => this.setOrderBy("birthday")} >{t("Birthdate")}</th>
                <th scope='col' onClick={() => this.setOrderBy("systemActors")} >{t("Actor")}</th>
                {this.state.attore!=="PLANNER" && <th scope='col'>{t("Info")}</th>}
                {this.state.attore==="PLANNER" && <th scope='col'>{t("Modify")}</th>}

              </tr>
            </MDBTableHead>
            <MDBTableBody>
              {this.state.utenti.map((data, key) => {
                return (
                  <tr key={key}>
                    <td>{data.name}</td>
                    <td>{data.lastname}</td>
                    <td>{data.birthday}</td>
                    <td>{data.systemActors.map(actor => t(actor)).join(", ")}</td>
                    {this.state.attore === "UTENTE" &&
                      <td><Button className="overlay" variant="primary" href={`/single-user-profile/${data.id}`}><i className="fa fa-id-card"> </i></Button></td>}
                    {this.state.attore!=="UTENTE" && <td><Button className="overlay" variant="primary" href={`/single-user-profile/${data.id}`}><i className="fas fa-edit fa-lg"> </i></Button></td>}
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
