import React from "react"
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBCol,
  MDBContainer,
  MDBRow,
} from "mdb-react-ui-kit";
import PreferencesDatePick from "../../components/common/PreferencesDatePick";
import {DesiderateAPI} from "../../API/DesiderataAPI";
import {toast} from "react-toastify";
import {t} from "i18next";
import {panic} from "../../components/common/Panic";

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
      toDeletePreferences: [],
      orderBy: "data",
      comparator: defaultComparator
    }
    this.setOrderBy = this.setOrderBy.bind(this);
    this.updatePreferences = this.updatePreferences.bind(this) ;
  }

  setOrderBy(userProp){
    this.setState({
      orderBy: userProp,
      comparator: defaultComparator
    })
  }

  updatePreferences(prefs, toDelPrefs) {
    this.setState({
      desiderate : prefs,
      toDeletePreferences : toDelPrefs,
    })
  }

  async componentDidMount() {
    let id = localStorage.getItem("id");
    let desiderate
    try {
      desiderate = await(new DesiderateAPI().getDesiderate(id));
    } catch (err) {

      panic()
      return
    }
    this.setState({
      desiderate : desiderate,
    })

  }

  async handleDeleteDesiderata(idDesiderata) {
    let id = localStorage.getItem("id");
    let desiderata = new DesiderateAPI();
    let responseStatus;
    try {
      responseStatus = await desiderata.deleteDesiderate(idDesiderata,id);
    } catch (err) {

      panic()
      return
    }

    if (responseStatus === 200) {
      //window.location.reload()
      this.componentDidMount()
      toast.success(t('Preference deleted successfully'), {
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
      toast.error(t('Error during removal'), {
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
              <MDBCardTitle>{t('Add your preferences')}</MDBCardTitle>
              <MDBRow>
                <MDBCol>
                  <PreferencesDatePick onSelectdate={() => this.componentDidMount()} desiderate={this.state.desiderate} toDelPrefs = {this.state.toDeletePreferences} setDesiderate={this.updatePreferences}/>
                </MDBCol>
              </MDBRow>
            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
      </section>
    )
  }


}


