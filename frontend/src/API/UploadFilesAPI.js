
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

    formData.append("allegato",file)
    return await fetch(`/api/assegnazioneturni/richiesterimozione/${idRequest}/caricaAllegato`, {
      // content-type header should not be specified!
    method: 'POST',
    body: formData,
    onUploadProgress,
    });
  }


  async getFiles() {
    return [];  //To do..
  };

}
