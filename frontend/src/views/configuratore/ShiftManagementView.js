import React from "react";
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer
} from "mdb-react-ui-kit";
import DialogDeleteShift from '../../components/common/DialogDeleteShift';
import {ServiceAPI} from "../../API/ServiceAPI";
import {TurnoAPI} from "../../API/TurnoAPI";
import type {MedicalService} from "../../entity/MedicalService";
import MedicalServiceCollapse
  from "../../components/common/MedicalServiceCollapse";
import ShiftItemBox from "../../components/common/ShiftItemBox";

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

    let retrievedServices: Promise<MedicalService[]> = serviceAPI.getAllServices();
    retrievedServices.then((retrievedServices) => {
      const sortedServices = retrievedServices.sort((a, b) => {
        if (a.name < b.name) return -1;
        if (a.name > b.name) return 1;
        return 0;
      });

      this.setState({medicalServices: sortedServices});
    })

    let retrievedShifts = shiftAPI.getShifts();
    retrievedShifts.then((retrievedShifts) => {
      this.setState({shifts: retrievedShifts});
    })
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
    )
      ;
  }
}
