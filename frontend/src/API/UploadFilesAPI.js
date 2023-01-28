
export class UploadFilesAPI {

  constructor() {
  }

  async uploadFile(file, onUploadProgress) {

    let formData = new FormData();

    formData.append("file",file)
    return await fetch('/api/giustifica/caricaFile', {
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
