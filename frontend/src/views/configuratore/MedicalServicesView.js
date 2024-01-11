import React from "react";
import {
    MDBCard,
    MDBCardBody,
    MDBCardTitle,
    MDBTable,
    MDBTableBody,
    MDBTableHead
    } from "mdb-react-ui-kit";
import {ServizioAPI} from "../../API/ServizioAPI";
import Button from '@mui/material/Button';
import MedicalServiceCreationDrawer from "../../components/common/BottomViewCreaServizio";
import MedicalServiceUpdateDrawer from "../../components/common/BottomViewModificaServizio";

function defaultComparator(prop1, prop2) {
    if (prop1 < prop2) return -1;
    if (prop1 > prop2) return 1;
    return 0;
}

export default class MedicalServicesView extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            services            : [],
            availableTaskTypes  : [],
            orderBy             : "name",
            comparator          : defaultComparator
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
        let servizioAPI = new ServizioAPI();
        const retrievedServices = await servizioAPI.getAllServices();
        const retrievedAvailableTaskTypes = await servizioAPI.getAvailableTaskTypes();
        this.setState({
            services : retrievedServices,
            availableTaskTypes : retrievedAvailableTaskTypes
            })
    }

    updateServicesList = (updatedList) => {
        var newServiceList=[]
        newServiceList.push(...this.state.services);
        newServiceList.push(...[updatedList]);
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

        return(
            <React.Fragment>
                <MedicalServiceCreationDrawer tasks={this.state.availableTaskTypes} services={this.state.services} updateServicesList={this.updateServicesList}/>
                <MDBCard>
                    <MDBCardBody className="text-center">
                        <div style={{ display: "flex", justifyContent: "center", alignItems: "center" }}>
                            <MDBCardTitle style={{ marginLeft: "auto", marginBottom: 10 }}>Informazioni Servizi</MDBCardTitle>
                        </div>
                        <MDBTable align="middle"
                                  bordered
                                  small
                                  hover>
                            <MDBTableHead color='tempting-azure-gradient' textWhite>
                                <tr>
                                    <th scope='col'>
                                        Servizio
                                    </th>
                                    <th scope='col'>
                                        Mansioni
                                    </th>
                                    <th scope='col'>
                                        Operazioni
                                    </th>
                                </tr>
                            </MDBTableHead>
                            <MDBTableBody>
                                {
                                    this.state.services.map((data, key) => {
                                    return (
                                        <tr key={key}>
                                            <td>
                                                {data.name}
                                            </td>
                                            <td>
                                                {data.taskTypesList}
                                            </td>
                                            <td>
                                                <MedicalServiceUpdateDrawer tasks={this.state.availableTaskTypes} services={this.state.services} updateServicesList={this.updateServicesList} currentServiceInfo={data}/>
                                            </td>
                                        </tr>
                                        )
                                    })
                                }
                            </MDBTableBody>
                        </MDBTable>
                    </MDBCardBody>
                </MDBCard>
            </React.Fragment>
        );
    }
}
