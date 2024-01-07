
export class UploadFilesAPI {

  constructor() {
  }

  async uploadGiustifica(file, onUploadProgress) {

    let formData = new FormData();

    formData.append("file",file)
    return await fetch('/api/giustifica/caricaFile', {
      // content-type header should not be specified!
      method: 'POST',
      body: formData,
      onUploadProgress,
    });
  }

  async uploadFileRetirement(file, onUploadProgress, idRequest) {
    let formData = new FormData();

    formData.append("attachment", file)
    return await fetch(`/api/concrete-shifts/retirement-request/${idRequest}/uploadFile`, {
    method: 'POST',
    body: formData,
    onUploadProgress,
    });
  }


  async getFiles() {
    return [];  //To do..
  };

}
