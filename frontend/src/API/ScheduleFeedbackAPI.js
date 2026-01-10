import {fetchWithAuth} from "../utils/fetchWithAuth";

export class ScheduleFeedbackAPI {

  /**
   * Per il Pianificatore tutti i feedback.
   */
  async getFeedbacks() {
    const response = await fetchWithAuth('/api/schedule-feedback');
    return await response.json();
  }

  /**
   * Per il Dottore solo i propri feedback.
   */
  async getMyFeedbacks() {
    const response = await fetchWithAuth('/api/schedule-feedback/mine');
    return await response.json();
  }

  async postFeedback(feedbackDTO) {
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(feedbackDTO)
    };

    const response = await fetchWithAuth('/api/schedule-feedback', requestOptions);
    return response;
  }

  // aggancio update non integrato. Valutarne integrazione futura con agenti AI?
  async updateFeedback(feedbackDTO) {
    const requestOptions = {
      method: 'PUT',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(feedbackDTO)
    };

    const response = await fetchWithAuth('/api/schedule-feedback', requestOptions);
    return response;
  }

  // aggancio delete non integrato. Valutarne integrazione futura con agenti AI?
  async deleteFeedback(feedbackId) {
    const requestOptions = {
      method: 'DELETE',
    };
    const response = await fetchWithAuth(`/api/schedule-feedback/${feedbackId}`, requestOptions);
    return response;
  }
}
