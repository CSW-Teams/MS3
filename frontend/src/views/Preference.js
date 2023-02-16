import React, {Component, useState} from "react"
import {
  MDBCard,
  MDBCardBody,
  MDBCardHeader, MDBCardText, MDBCardTitle,
  MDBCol,
  MDBContainer,
  MDBRow, MDBTable, MDBTableBody, MDBTableHead,
} from "mdb-react-ui-kit";
import DatePick from "../components/common/DatePick";
import IconButton from "@mui/material/IconButton";
import {DesiderateAPI} from "../API/DesiderataAPI";
import {toast, ToastContainer} from "react-toastify";
import DeleteIcon from "@mui/icons-material/Delete";

function defaultComparator(prop1, prop2){
  if (prop1 < prop2)
    return -1;
  if (prop1 > prop2)
    return 1;
  return 0;
}

export default class Preference extends React.Component {

  constructor(props){
    super(props);
    this.state = {
      desiderate:[],
      orderBy: "data",
      comparator: defaultComparator
    }
    this.setOrderBy = this.setOrderBy.bind(this);

  }

  setOrderBy(userProp){
    this.setState({
      orderBy: userProp,
      comparator: defaultComparator
    })
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
    let desiderata = new DesiderateAPI();
    let responseStatus;
    responseStatus = await desiderata.deleteDesiderate(idDesiderata,id);
    console.log(responseStatus)

    if (responseStatus === 200) {
      //window.location.reload()
      this.componentDidMount()
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

    this.state.desiderate.sort((u1, u2) => {

      let p1 = u1[this.state.orderBy];
      let p2 = u2[this.state.orderBy];

      return this.state.comparator(p1, p2);

    })

    return (
      <section style={{backgroundColor: '#eee'}}>
      <MDBContainer className="py-5" style={{height: '85vh',}}>
        <MDBCard alignment='center'>
          <MDBCardBody>
            <MDBCardTitle>Inserisci le tue desiderate    <DatePick onSelectdate={() => this.componentDidMount()} desiderate={this.state.desiderate}/></MDBCardTitle>
            <MDBRow>
            <MDBCol>
            </MDBCol>
        </MDBRow>
            <MDBRow>
              <MDBTable align="middle"
                        bordered
                        small
                        hover
                        >
                <MDBTableHead color='tempting-azure-gradient' textWhite>
                <tr>
                    <th scope='col' onClick={() => this.setOrderBy("data")} >Data</th>
                    <th scope='col'  >  </th>
                  </tr>
                </MDBTableHead>
                <MDBTableBody>
                  {this.state.desiderate.map((data, key) => {
                    return (
                      <tr key={key}>
                        <td className="align-middle">{data.data}</td>
                        <td className="align-middle" ><IconButton aria-label="delete"onClick={() => this.handleDeleteDesiderata(data.idDesiderata)}><DeleteIcon /></IconButton></td>
                      </tr>
                    )
                  })}
                </MDBTableBody>
              </MDBTable>
            </MDBRow>
        </MDBCardBody>
        </MDBCard>
      </MDBContainer>
        <ToastContainer
          position="top-center"
          autoClose={5000}
          hideProgressBar={true}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
        />
      </section>
    )
  }


}



