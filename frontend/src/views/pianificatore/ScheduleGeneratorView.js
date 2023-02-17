import React from "react";
import Button from '@mui/material/Button';
import {toast, ToastContainer} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBCol,
  MDBContainer,
  MDBRow, MDBTable, MDBTableBody, MDBTableHead,
} from "mdb-react-ui-kit";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import TemporaryDrawerSchedulo from "../../components/common/BottomViewAggiungiSchedulazione";
import {ScheduloAPI} from "../../API/ScheduloAPI";

/*
* Schermata che permette di generare un nuovo schedulo
*/
export class SchedulerGeneratorView extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            dataStart: "",
            dataEnd: "",
            schedulazioni: [{}]

        }

        this.componentDidMount = this.componentDidMount.bind(this);
    }

    async componentDidMount() {
      let schedulazioni = await(new ScheduloAPI().getSchedulazini());
      console.log(schedulazioni[0]);

      this.setState({
        schedulazioni: schedulazioni,
      })

    }

    async handleDelete(idSchedulo) {
      let scheduloAPI = new ScheduloAPI();
      let responseStatus;
      responseStatus = await scheduloAPI.deleteSchedulo(idSchedulo);

      if (responseStatus === 200) {
        this.componentDidMount()
        toast.success('Schedulazione cancellata con successo', {
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
      }else if (responseStatus === 417) {
        toast.error('Non è possibile cancellare una vecchia pianificazione!', {
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


    async handleRegeneration(idSchedulo) {
      let scheduloAPI = new ScheduloAPI();
      let responseStatus;
      responseStatus = await scheduloAPI.rigeneraSchedulo(idSchedulo);

      if (responseStatus === 202) {
        this.componentDidMount()
        toast.success('Schedulazione ricreata con successo', {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
      } else if (responseStatus === 417) {
        toast.error('Non è possibile rigenerare una vecchia pianificazione!', {
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

      else {
        toast.error('Errore nella ricreazione', {
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


    render(){
        return (


          <section>
          <TemporaryDrawerSchedulo onPostGeneration= {this.componentDidMount}></TemporaryDrawerSchedulo>

          <MDBContainer className="py-5">
            <MDBCard alignment='center'>
              <MDBCardBody style={{height: '64vh'}}>
                <MDBCardTitle>Gestione schedulazioni</MDBCardTitle>
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
                        <th scope='col' >Data inizio</th>
                        <th scope='col' >Data fine</th>
                        <th scope='col' >Stato </th>
                        <th scope='col' > </th>
                        <th scope='col' > </th>
                      </tr>
                    </MDBTableHead>
                    <MDBTableBody>
                    {this.state.schedulazioni.map((schedulo, key) => {
                    return (
                      <tr key={key}>
                        <td className="align-middle">{schedulo.dataInizio}</td>
                        <td className="align-middle">{schedulo.dataFine}</td>
                        <td className="align-middle">{schedulo.illegalita?"Incomppleta":"Completa"}</td>
                        <td className="align-middle" ><IconButton aria-label="delete" onClick={() => this.handleDelete(schedulo.id)}><DeleteIcon /></IconButton></td>
                        <td className="align-middle"><Button onClick={() => this.handleRegeneration(schedulo.id)}>Rigenera</Button></td>
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
