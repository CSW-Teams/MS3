import React from "react";
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer,
  MDBTable,
  MDBTableBody,
  MDBTableHead
} from "mdb-react-ui-kit";
import {ServiceAPI} from "../../API/ServiceAPI";
import Typography from "@mui/material/Typography";
import MedicalServiceUpdateDrawer from "../../components/common/BottomViewModificaServizio";
import DialogDeleteService from '../../components/common/DialogDeleteService';
import DialogCreateNewService from "../../components/common/DialogCreateNewService";
import {panic} from "../../components/common/Panic";
import {t} from "i18next";

function defaultComparator(prop1, prop2) {
  if (prop1 < prop2) return -1;
  if (prop1 > prop2) return 1;
  return 0;
}

export default class MedicalServicesView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      services: [],
      availableTaskTypes: [],
      orderBy: "name",
      comparator: defaultComparator
    }
    this.setOrderBy = this.setOrderBy.bind(this);
  }

  /**
   * Cambia la proprietà degli utenti per cui si vuole ordinare,
   * usando un comparatore di default
   */
  setOrderBy(userProp) {
    this.setState({
      orderBy: userProp,
      comparator: defaultComparator
    })
  }

  /**
   * Cambia la proprietà degli utenti per cui si vuole ordinare,
   * specificando un comparatore custom
   */
  setOrderByAndComparator(userProp, comparator) {
    this.setState({
      orderBy: userProp,
      comparator: comparator
    })
  }

  componentDidMount() {
    this.getServiceLists();
  }

  async getServiceLists() {
    let serviceAPI = new ServiceAPI();
    let retrievedServices
    let retrievedAvailableTaskTypes
    try {
      retrievedServices = await serviceAPI.getAllServices();
      retrievedAvailableTaskTypes = await serviceAPI.getAvailableTaskTypes();
    } catch (err) {

      panic()
      return
    }
    this.setState({
      services: retrievedServices,
      availableTaskTypes: retrievedAvailableTaskTypes
    })
  }

  updateServicesListAfterCreation = (createdService) => {
    var newServiceList = []
    newServiceList.push(...this.state.services);
    newServiceList.push(...[createdService]);
    this.setState({services: newServiceList});
  };

  updateServicesListAfterUpdate = (updatedService) => {
    var newServiceList = []
    newServiceList.push(...this.state.services);
    var objIndex = newServiceList.findIndex((obj => obj.id == updatedService.id));
    newServiceList[objIndex] = updatedService;
    this.setState({services: newServiceList});
  };

  updateServicesListAfterRemoval = (removedService) => {
    var newServiceList = []
    newServiceList.push(...this.state.services);
    var objIndex = newServiceList.findIndex((obj => obj.id == removedService.id));
    newServiceList.splice(objIndex, 1);
    this.setState({services: newServiceList});
  };

  render() {
    // Ordina gli utenti in base alla proprietà specificata.
    // È possibile specificare la proprietà cliccando sulla colonna corrispondente.
    this.state.services.sort((u1, u2) => {
      let p1 = u1[this.state.orderBy];
      let p2 = u2[this.state.orderBy];
      return this.state.comparator(p1, p2);
    })

    return (
      <MDBContainer fluid className="main-content-container px-4 pb-4 pt-4">

        <DialogCreateNewService tasks={this.state.availableTaskTypes}
                                services={this.state.services}
                                updateServicesList={this.updateServicesListAfterCreation}/>

        <MDBCard>
          <MDBCardBody className="text-center">
            <div style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center"
            }}>
              <MDBCardTitle style={{
                marginLeft: "auto",
                marginBottom: 10
              }}>{t('Services Informations')}</MDBCardTitle>
            </div>
            <MDBTable align="middle"
                      bordered
                      small
                      hover>
              <MDBTableHead color='tempting-azure-gradient' textWhite>
                <tr>
                  <th scope='col'>
                    {t('Service')}
                  </th>
                  <th scope='col'>
                    {t('Task')}
                  </th>
                  <th scope='col'>
                    {t('Operations')}
                  </th>
                </tr>
              </MDBTableHead>
              <MDBTableBody>
                {
                  this.state.services.map((service, key) => {
                    return (
                      <tr key={key}>
                        <td>
                          <Typography variant="p">
                            {service.name.toUpperCase()}
                          </Typography>
                        </td>
                        <td>
                          {service.getTasksAsString()}
                        </td>
                        <td>
                          <div style={{
                            display: 'inline-flex',
                            align: 'center',
                            flexDirection: 'row'
                          }}>
                            <MedicalServiceUpdateDrawer
                              availableTasks={this.state.availableTaskTypes}
                              services={this.state.services}
                              updateServicesList={this.updateServicesListAfterUpdate}
                              currentServiceInfo={service}/>
                            <DialogDeleteService
                              updateServicesList={this.updateServicesListAfterRemoval}
                              currentServiceInfo={service}
                              disabled={service.assigned}/>
                          </div>
                        </td>
                      </tr>
                    )
                  })
                }
              </MDBTableBody>
            </MDBTable>
          </MDBCardBody>
        </MDBCard>
      </MDBContainer>
    );
  }
}
