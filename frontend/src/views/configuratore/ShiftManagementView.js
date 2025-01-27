import React from "react";
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer
} from "mdb-react-ui-kit";
import {ServiceAPI} from "../../API/ServiceAPI";
import {TurnoAPI} from "../../API/TurnoAPI";
import type {MedicalService} from "../../entity/MedicalService";
import MedicalServiceCollapse
  from "../../components/common/MedicalServiceCollapse";
import ShiftItemBox from "../../components/common/ShiftItemBox";
import DialogDeleteShift from "../../components/common/DialogDeleteShift";

export default class ShiftManagementView extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      medicalServices: [],
      shifts: []
    }
  }

  componentDidMount() {
    let serviceAPI = new ServiceAPI();
    let shiftAPI = new TurnoAPI();

    let retrievedServicesPromise: Promise<MedicalService[]> = serviceAPI.getAllServices();
    let retrievedShiftsPromise: Promise<Shift[]> = shiftAPI.getShifts();

    Promise.all([retrievedServicesPromise, retrievedShiftsPromise])
      .then(([retrievedServices:MedicalService[], retrievedShifts:Shift[]]) => {
        const sortedServices = retrievedServices.sort((a, b) => {
          if (a.name < b.name) return -1;
          if (a.name > b.name) return 1;
          return 0;
        });

        const filteredShifts = retrievedShifts.filter(shift =>
          retrievedServices.some(service => service.id === shift.medicalService.id)
        );

        this.setState({
          medicalServices: sortedServices,
          shifts: filteredShifts,
        });
      })
      .catch(error => {
        console.error("Errore durante il caricamento dei dati:", error);
      });
  }

  /**
   * Funzione per aggiornare la lista dei turni dopo l'eliminazione.
   */
  updateShiftsListAfterRemoval = (removedShift) => {
    const updatedShifts = this.state.shifts.filter(
      (shift) => shift.id !== removedShift.id
    );
    this.setState({ shifts: updatedShifts });
  };

  render() {
    return (
      <MDBContainer fluid className="main-content-container px-4 pb-4 pt-4">
        <MDBCard alignment="center">
          <MDBCardBody>
            <MDBCardTitle style={{marginBottom: '30px'}}>
              Gestione dei turni
            </MDBCardTitle>

            {this.state.medicalServices.map((medicalService: MedicalService) => (
              <MedicalServiceCollapse medicalService={medicalService}>
                {this.state.shifts
                  .filter((shift) => shift.medicalService.label === medicalService.name)
                  .map((shift: Shift) => (
                    <div
                      key={shift.id}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between"
                      }}
                    >
                      <ShiftItemBox key={shift.id} shiftData={shift}/>
                      <DialogDeleteShift
                        currentShiftInfo={shift}
                        updateShiftsList={this.updateShiftsListAfterRemoval}
                        disabled={false} // Personalizza se il pulsante deve essere disabilitato.
                      />
                    </div>
                  ))}
              </MedicalServiceCollapse>
            ))}
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>
    );
  }
}
