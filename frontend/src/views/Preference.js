import React, {Component, useState} from "react"
import {
  MDBCard,
  MDBCardBody,
  MDBCardHeader,
  MDBCol,
  MDBContainer,
  MDBRow, MDBTable, MDBTableBody, MDBTableHead,
} from "mdb-react-ui-kit";
import {UtenteAPI} from "../API/UtenteAPI";
import DatePick from "../components/common/DatePick";
import IconButton from "@mui/material/IconButton";
import {DesiderateAPI} from "../API/DesiderataAPI";
import {CategoriaUtenteAPI} from "../API/CategoriaUtenteAPI";
import {toast} from "react-toastify";
import DeleteIcon from "@mui/icons-material/Delete";



export default class Preference extends React.Component {

  constructor(props){
    super(props);
    this.state = {
      desiderate:[],
    }
  }

  async componentDidMount() {
    let id = localStorage.getItem("id");
    let desiderate = await(new DesiderateAPI().getDesiderate(id));
    this.setState({
      desiderate : desiderate,
    })

  }

  async handleDeleteDesiderata(idDesiderata) {
    let id = localStorage.getItem("id");
    console.log(idDesiderata)
    let desiderata = new DesiderateAPI();
    let responseStatus;
    responseStatus = await desiderata.deleteDesiderate(idDesiderata,id);
    console.log(responseStatus)

    if (responseStatus === 200) {
      toast.success('Desiderata cancellata con successo', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
      this.componentDidMount()
    } else if (responseStatus === 400) {
      toast.error('Errore nella cancellazione', {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
    }
  }


  render() {

    return (
      <section style={{backgroundColor: '#eee'}}>
      <MDBContainer className="py-5">
        <MDBCard alignment='center'>
          <MDBCardHeader>Inserisci le tue desiderate</MDBCardHeader>
          <MDBCardBody>
          <MDBRow>
            <MDBCol>
              <DatePick onSelectdate={() => this.componentDidMount()}/>
            </MDBCol>
        </MDBRow>
            <MDBRow>
              <MDBTable align="middle" >
                <MDBTableHead>
                  <tr>
                    <th scope='col'  > Data </th>
                    <th scope='col'  >  </th>
                  </tr>
                </MDBTableHead>
                <MDBTableBody>
                  {this.state.desiderate.map((data, key) => {
                    return (
                      <tr key={key}>
                        <td>{data.giorno+"/"+data.mese+"/"+data.anno}</td>
                        <td><IconButton aria-label="delete"onClick={() => this.handleDeleteDesiderata(data.idDesiderata)}><DeleteIcon /></IconButton></td>
                      </tr>
                    )
                  })}
                </MDBTableBody>
              </MDBTable>
            </MDBRow>
        </MDBCardBody>
        </MDBCard>
      </MDBContainer>
      </section>
    )
  }


}



