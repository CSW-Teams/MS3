import React from "react";
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBCol,
  MDBContainer,
  MDBRow, MDBTable, MDBTableBody, MDBTableHead,
} from "mdb-react-ui-kit";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import {panic} from "../../components/common/Panic";
import {t} from "i18next";
import { ScheduleFeedbackAPI } from "../../API/ScheduleFeedbackAPI";

export default class FeedbackManagementView extends React.Component{

  constructor(props){
    super(props);
    this.state = {
      feedbacks: [],
      orderBy: "lastname",
      orderDirection: "asc",
      attore: localStorage.getItem("actor"),
      isTextModalOpen: false,
      selectedFeedback: null,
    };
  }


  async componentDidMount() {
    try {
      let api = new ScheduleFeedbackAPI();
      const rawData = await api.getFeedbacks(); // Chiamata al Backend

      const formattedFeedbacks = rawData.map(item => ({
        name: item.doctorName,
        lastname: item.doctorLastname,
        feedback_rating: item.score,
        feedback_text: item.comment
      }));

      this.setState({ feedbacks: formattedFeedbacks });

    } catch(err) {
      panic()
      return
    }
  }

  getSortIcon(column) {
      let direction = "fas fa-sort"
      if (this.state.orderBy === column) direction = this.state.orderDirection === "asc" ? "fas fa-sort-up" : "fas fa-sort-down"

      return <i
        className={direction}
        style={{
          color: this.state.orderBy === column ? "#1a1a1a" : "#e0e0e0",
          marginLeft: "5px",
        }}
      ></i>;
    }

  setOrderBy = (column) => {
    this.setState((prevState) => {
      if (prevState.orderBy === column) {
        return {orderDirection: prevState.orderDirection === "asc" ? "desc" : "asc"};
      }
      return {orderBy: column, orderDirection: "asc"};
    });
  }

  openTextModal = (feedback) => {
    this.setState({ isTextModalOpen: true, selectedFeedback: feedback });
  }

  closeTextModal = () => {
    this.setState({ isTextModalOpen: false, selectedFeedback: null });
  }

  render(){
    const sortedData = [...this.state.feedbacks].sort((a, b) => {
      const {orderBy, orderDirection} = this.state;
      const left = a?.[orderBy];
      const right = b?.[orderBy];
      if (left == null && right == null) return 0;
      if (left == null) return orderDirection === "asc" ? -1 : 1;
      if (right == null) return orderDirection === "asc" ? 1 : -1;
      if (typeof left === "string" || typeof right === "string") {
        return orderDirection === "asc"
          ? String(left).localeCompare(String(right))
          : String(right).localeCompare(String(left));
      }
      return orderDirection === "asc" ? left - right : right - left;
    });
    const previewStyle = {
      maxWidth: 420,
      whiteSpace: "nowrap",
      overflow: "hidden",
      textOverflow: "ellipsis",
      display: "block",
      minWidth: 0,
    };

    return (
          <MDBContainer fluid className="main-content-container px-4 pb-4 pt-4">
            <MDBCard alignment="center">
              <MDBCardBody className="text-center">
                <MDBCardTitle
                  style={{marginBottom: 10}}>{t("Feedbacks provided")}</MDBCardTitle>
                <MDBTable align="middle"
                          bordered
                          small
                          hover>
                  <MDBTableHead color='tempting-azure-gradient' textwhite>
                    <tr>
                      <th scope='col'
                          onClick={() => this.setOrderBy("name")}>{t("Name")} {this.getSortIcon("name")}</th>
                      <th scope='col'
                          onClick={() => this.setOrderBy("lastname")}>{t("Surname")} {this.getSortIcon("lastname")}</th>
                      <th scope='col'
                          onClick={() => this.setOrderBy("feedback rating")}>{t("Feedback rating")} {this.getSortIcon("Feedback rating")}</th>
                      <th scope='col'
                          onClick={() => this.setOrderBy("feedback text")}>{t("Text")} {this.getSortIcon("Text")}</th>
                    </tr>
                  </MDBTableHead>
                  <MDBTableBody>
                    {sortedData.map((data, key) => (
                      <tr key={key}>
                        <td>{data.name}</td>
                        <td>{data.lastname}</td>
                        <td>{data.feedback_rating}</td>
                        <td>
                          <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                            <Button
                              variant="text"
                              align="center"
                              color="primary"
                              onClick={() => this.openTextModal(data)}
                            >
                              {t("Show")}
                            </Button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </MDBTableBody>
                </MDBTable>
                <Dialog
                  open={this.state.isTextModalOpen}
                  onClose={this.closeTextModal}
                  maxWidth="sm"
                  fullWidth
                >
                  <DialogTitle>
                    {t("Text")} {this.state.selectedFeedback
                    ? `- ${this.state.selectedFeedback.name} ${this.state.selectedFeedback.lastname}`
                    : ""}
                  </DialogTitle>
                  <DialogContent>
                    <div style={{ whiteSpace: "pre-wrap" }}>
                      {this.state.selectedFeedback?.feedback_text || ""}
                    </div>
                  </DialogContent>
                  <DialogActions>
                    <Button onClick={this.closeTextModal}>{t("Close")}</Button>
                  </DialogActions>
                </Dialog>
              </MDBCardBody>
            </MDBCard>
          </MDBContainer>
        );
  }
}
