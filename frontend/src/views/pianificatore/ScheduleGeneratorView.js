import React from "react";
import Button from '@mui/material/Button';
import {toast} from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import {
  MDBCard,
  MDBCardBody,
  MDBCardTitle,
  MDBContainer,
  MDBRow, MDBTable, MDBTableBody, MDBTableHead,
} from "mdb-react-ui-kit";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import TemporaryDrawerSchedule from "../../components/common/BottomViewAggiungiSchedulazione";
import {ScheduleAPI} from "../../API/ScheduleAPI";
import {AssegnazioneTurnoAPI} from "../../API/AssegnazioneTurnoAPI";
import { t } from "i18next";
import {panic} from "../../components/common/Panic";
import { Container } from "shards-react";
import GenerationLoadingModal from "../../components/common/GenerationLoadingModal";
import GenerationStatusFeedback from "../../components/common/GenerationStatusFeedback";
import AiScheduleComparisonModal from "../../components/common/AiScheduleComparisonModal";
import AiScheduleSelectionConfirmationModal from "../../components/common/AiScheduleSelectionConfirmationModal";

const resolveCandidateLabel = (metadata) => {
  if (!metadata?.type) {
    return 'Schedule / Schedulazione';
  }
  switch (metadata.type.toLowerCase()) {
    case 'standard':
      return 'Standard / Standard';
    case 'empathetic':
      return 'Empathetic / Empatica';
    case 'efficient':
      return 'Efficient / Efficiente';
    case 'balanced':
      return 'Balanced / Bilanciata';
    default:
      return `${metadata.type} / ${metadata.type}`;
  }
};

/**
 * @see docs/scheduling_flow/README.md
 * This is the main UI component for the Planner to manage schedules.
 * It displays the list of existing schedules and provides the entry point for creating a new one
 * via the TemporaryDrawerSchedule component. It handles schedule deletion and regeneration,
 * reflecting a domain rule that only the latest schedule can be regenerated.
 */
export class SchedulerGeneratorView extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            dataStart: "",
            dataEnd: "",
            schedulazioni: [{}],
            loading: false, // Per delete e vecchi caricatori
            isGenerationLoading: false, // Nuovo stato per la modale di caricamento della generazione
            generationStatus: null, // 'success', 'partial', 'error', null
            generationMessage: '',
            generationDetails: '',
            isComparisonOpen: false,
            comparisonCandidates: [],
            selectionLocked: false,
            selectedCandidateKey: null,
            pendingCandidate: null,
            isSelectionConfirmationOpen: false,
            isSelectionSubmitting: false,
            selectedScheduleId: null, // Hook for future success messaging (Story 4.4).
        }

        this.componentDidMount = this.componentDidMount.bind(this);
        this.handleGenerateSchedule = this.handleGenerateSchedule.bind(this);
        this.handleCloseGenerationFeedback = this.handleCloseGenerationFeedback.bind(this);
        this.handleCloseComparisonModal = this.handleCloseComparisonModal.bind(this);
        this.handleOpenSelectionConfirmation = this.handleOpenSelectionConfirmation.bind(this);
        this.handleCancelSelectionConfirmation = this.handleCancelSelectionConfirmation.bind(this);
        this.handleConfirmSelection = this.handleConfirmSelection.bind(this);
    }

    async componentDidMount() {
      let schedulazioni
      try {
        schedulazioni = await(new ScheduleAPI().getSchedulazini());
      } catch (err) {

        panic()
        return
      }

      this.setState({
        schedulazioni: schedulazioni,
      })

    }

    async handleDelete(idSchedule) {

      this.setState({loading: true});

      let scheduleAPI = new ScheduleAPI();
      let responseStatus;
      try {
        responseStatus = await scheduleAPI.deleteSchedule(idSchedule);
      } catch (err) {

        panic()
        return
      }

      if (responseStatus === 200) {
        this.componentDidMount()
        toast.success(t("Schedule successfully deleted"), {
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
        toast.error(t("Error during removal"), {
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
        toast.error(t("Deleting an old Schedule is not allowed"), {
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

      this.setState({loading: false});
    }

    async handleRegeneration(idSchedule) {
      this.setState({isGenerationLoading: true}); // Inizio caricamento generazione

      let scheduleAPI = new ScheduleAPI();
      let responseStatus;
      try {
        responseStatus = await scheduleAPI.rigeneraSchedule(idSchedule);
        // La logica di gestione della risposta
        if (responseStatus === 202) {
            await this.componentDidMount() // Attendere il ricaricamento degli schedule
            this.setState({
                generationStatus: 'success',
                generationMessage: t("Schedule successfully recreated"),
                generationDetails: '',
                isComparisonOpen: false,
                comparisonCandidates: [],
                selectionLocked: false,
                selectedCandidateKey: null,
                pendingCandidate: null,
                isSelectionConfirmationOpen: false,
                isSelectionSubmitting: false,
                selectedScheduleId: null,
            });
          } else if (responseStatus === 417) {
            this.setState({
                generationStatus: 'error',
                generationMessage: t("Old Schedules cannot be regenerated"),
                generationDetails: '',
                isComparisonOpen: false,
                comparisonCandidates: [],
                selectionLocked: false,
                selectedCandidateKey: null,
                pendingCandidate: null,
                isSelectionConfirmationOpen: false,
                isSelectionSubmitting: false,
                selectedScheduleId: null,
            });
          } else {
            this.setState({
                generationStatus: 'error',
                generationMessage: t("Regeneration Error"),
                generationDetails: '',
                isComparisonOpen: false,
                comparisonCandidates: [],
                selectionLocked: false,
                selectedCandidateKey: null,
                pendingCandidate: null,
                isSelectionConfirmationOpen: false,
                isSelectionSubmitting: false,
                selectedScheduleId: null,
            });
          }
      } catch (err) {
        panic();
        // In caso di errore non gestito (panic), il loading viene comunque terminato dal finally
        this.setState({
            generationStatus: 'error',
            generationMessage: t("An unexpected error occurred during regeneration."),
            generationDetails: err.message || t("Please try again later."),
            isComparisonOpen: false,
            comparisonCandidates: [],
            selectionLocked: false,
            selectedCandidateKey: null,
            pendingCandidate: null,
            isSelectionConfirmationOpen: false,
            isSelectionSubmitting: false,
            selectedScheduleId: null,
        });
      } finally {
        this.setState({isGenerationLoading: false}); // Fine caricamento generazione
      }
    }

    async handleGenerateSchedule(dataInizio, dataFine) {
      this.setState({
        isGenerationLoading: true,
        generationStatus: null,
        generationMessage: '',
        generationDetails: '',
        comparisonCandidates: [],
        isComparisonOpen: false,
        selectionLocked: false,
        selectedCandidateKey: null,
        pendingCandidate: null,
        isSelectionConfirmationOpen: false,
        isSelectionSubmitting: false,
        selectedScheduleId: null,
      });

      let assegnazioneTurnoAPI = new AssegnazioneTurnoAPI();
      let response;
      try {
        response = await assegnazioneTurnoAPI.postGenerationScheduleAi(dataInizio, dataFine);
        await this.componentDidMount(); // Ricarica gli schedule dopo la generazione

        switch (response.status) {
          case 200:
          case 202: {
            const comparisonCandidates = response.body?.candidates ?? [];
            this.setState({
              generationStatus: 'success',
              generationMessage: t("Schedule successfully created."),
              comparisonCandidates: comparisonCandidates,
              isComparisonOpen: comparisonCandidates.length > 0,
            });
            break;
          }
          case 206:
            this.setState({
              generationStatus: 'partial',
              generationMessage: t("Schedule generated with warnings."),
              generationDetails: t("Some constraints were violated, resulting in a partial schedule."),
              isComparisonOpen: false,
              comparisonCandidates: [],
            });
            break;
          case 406: // NOT_ACCEPTABLE HTTP ERROR
            this.setState({
              generationStatus: 'error',
              generationMessage: t("Error: Schedule already exists or cannot be generated."),
              generationDetails: t("Please check dates and existing schedules."),
              isComparisonOpen: false,
              comparisonCandidates: [],
            });
            break;
          default:
            this.setState({
              generationStatus: 'error',
              generationMessage: t("Schedule Generation Error."),
              generationDetails: t("An unexpected error occurred."),
              isComparisonOpen: false,
              comparisonCandidates: [],
            });
            break;
        }

      } catch (err) {
        panic();
        this.setState({
          generationStatus: 'error',
          generationMessage: t("An unexpected error occurred during schedule generation."),
          generationDetails: err.message || t("Please try again later."),
          isComparisonOpen: false,
          comparisonCandidates: [],
        });
      } finally {
        this.setState({ isGenerationLoading: false });
      }
    }

    handleCloseGenerationFeedback() {
        this.setState({
            generationStatus: null,
            generationMessage: '',
            generationDetails: '',
        });
    }

    handleCloseComparisonModal() {
      this.setState({ isComparisonOpen: false, comparisonCandidates: [] });
    }

    handleOpenSelectionConfirmation(candidate) {
      if (this.state.selectionLocked || !candidate) {
        return;
      }
      this.setState({
        pendingCandidate: candidate,
        isSelectionConfirmationOpen: true,
      });
    }

    handleCancelSelectionConfirmation() {
      if (this.state.isSelectionSubmitting) {
        return;
      }
      this.setState({
        pendingCandidate: null,
        isSelectionConfirmationOpen: false,
      });
    }

    async handleConfirmSelection() {
      const { pendingCandidate, selectionLocked, isSelectionSubmitting } = this.state;
      if (!pendingCandidate || selectionLocked || isSelectionSubmitting) {
        return;
      }
      const candidateKey =
        pendingCandidate.metadata?.candidateId ?? pendingCandidate.metadata?.type;
      if (!candidateKey) {
        toast.error("Selection data missing / Dati selezione mancanti", {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
        return;
      }

      this.setState({ isSelectionSubmitting: true });
      let response;
      try {
        response = await new ScheduleAPI().selectScheduleCandidate(candidateKey);
      } catch (error) {
        toast.error("Selection failed / Selezione non riuscita", {
          position: "top-center",
          autoClose: 5000,
          hideProgressBar: true,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: "colored",
        });
        this.setState({ isSelectionSubmitting: false });
        return;
      }

      if (response.status === 202) {
        this.setState({
          selectionLocked: true,
          selectedCandidateKey: candidateKey,
          pendingCandidate: null,
          isSelectionConfirmationOpen: false,
          isSelectionSubmitting: false,
          selectedScheduleId: response.body?.scheduleId ?? null,
        });
        return;
      }

      toast.error("Selection could not be saved / Impossibile salvare la selezione", {
        position: "top-center",
        autoClose: 5000,
        hideProgressBar: true,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: "colored",
      });
      this.setState({ isSelectionSubmitting: false });
    }

  render() {
    const {
      loading,
      isGenerationLoading,
      generationStatus,
      generationMessage,
      generationDetails,
      schedulazioni,
      isComparisonOpen,
      comparisonCandidates,
      selectionLocked,
      selectedCandidateKey,
      pendingCandidate,
      isSelectionConfirmationOpen,
      isSelectionSubmitting,
    } = this.state;

    return (
      <Container fluid className="main-content-container px-4 pb-4">
        <MDBContainer className="py-5">
          <MDBCard alignment='center'>
            <MDBCardBody >
              <MDBCardTitle>{t("Schedule management")}</MDBCardTitle>
              {/* Componente per il feedback sulla generazione dello schedule */}
              <GenerationStatusFeedback
                status={generationStatus}
                message={generationMessage}
                details={generationDetails}
                onClose={this.handleCloseGenerationFeedback}
              />
              <MDBRow className="mt-3 mb-3 mx-3">
                <TemporaryDrawerSchedule onGenerateSchedule={this.handleGenerateSchedule}></TemporaryDrawerSchedule>
              </MDBRow>
              <MDBRow>
                <MDBTable align="middle" bordered small hover>
                  <MDBTableHead color='tempting-azure-gradient' textwhite>
                    <tr>
                      <th scope='col'>{t("Start Date")}</th>
                      <th scope='col'>{t("End Date")}</th>
                      <th scope='col'>{t("Status")}</th>
                      <th scope='col'> </th>
                      <th scope='col'> </th>
                    </tr>
                  </MDBTableHead>
                  <MDBTableBody>
                    {schedulazioni.map((schedule, key) => {
                      const millisecondsInDay = 86400000; // 24 * 60 * 60 * 1000
                      const initialDayMillis = schedule.initialDate * millisecondsInDay;
                      const finalDayMillis = schedule.finalDate * millisecondsInDay;

                      const options = {
                        weekday: 'long',
                        day: "numeric",
                        month: 'long',
                        year: 'numeric',
                      };

                      const startDate = new Date(initialDayMillis);
                      const endDate = new Date(finalDayMillis);

                      return (
                        <tr key={key}>
                          <td className="align-middle">{startDate.toLocaleString(navigator.language, options)}</td>
                          <td className="align-middle">{endDate.toLocaleString(navigator.language, options)}</td>
                          <td className="align-middle">{schedule.isIllegal ? "Incompleta" : "Completa"}</td>
                          <td className="align-middle">
                            <IconButton
                              aria-label="delete"
                              onClick={() => this.handleDelete(schedule.id)}>
                              <DeleteIcon />
                            </IconButton>
                          </td>
                          <td className="align-middle">
                            {schedule === schedulazioni[schedulazioni.length - 1] &&
                            <Button onClick={() => this.handleRegeneration(schedule.id)}>Rigenera</Button>
                            }
                          </td>
                        </tr>
                      )
                    })}
                  </MDBTableBody>
                </MDBTable>
              </MDBRow>

              {loading && (
                <div className="loading-overlay">
                  <div className="loading-spinner"></div>
                </div>
              )}

            </MDBCardBody>
          </MDBCard>
        </MDBContainer>
        {/* Modale di caricamento per la generazione */}
        <GenerationLoadingModal isOpen={isGenerationLoading} />
        <AiScheduleComparisonModal
          isOpen={isComparisonOpen}
          onClose={this.handleCloseComparisonModal}
          candidates={comparisonCandidates}
          onSelectCandidate={this.handleOpenSelectionConfirmation}
          selectionLocked={selectionLocked}
          selectedCandidateKey={selectedCandidateKey}
        />
        <AiScheduleSelectionConfirmationModal
          isOpen={isSelectionConfirmationOpen}
          onConfirm={this.handleConfirmSelection}
          onCancel={this.handleCancelSelectionConfirmation}
          candidateLabel={resolveCandidateLabel(pendingCandidate?.metadata)}
          scheduleId={pendingCandidate?.metadata?.scheduleId}
          isSubmitting={isSelectionSubmitting}
        />
      </Container>
    )
  }


}
