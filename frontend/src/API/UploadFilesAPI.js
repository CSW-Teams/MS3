import {fetchWithAuth} from "../utils/fetchWithAuth";

export class UploadFilesAPI {

  constructor() {
  }

  async uploadGiustifica(file, onUploadProgress) {

    let formData = new FormData();

    formData.append("file",file)
    return await fetchWithAuth('/api/justify/uploadJustification', {
      // content-type header should not be specified!
      method: 'POST',
      body: formData,
      onUploadProgress,
    });
  }

  async uploadFileRetirement(file, onUploadProgress, idRequest) {
    let formData = new FormData();

    formData.append("attachment", file)
    return await fetchWithAuth(`/api/concrete-shifts/retirement-request/${idRequest}/uploadFile`, {
    method: 'POST',
    body: formData,
    onUploadProgress,
    });
  }


  async getFiles() {
    return [];  //To do..
  };

}
