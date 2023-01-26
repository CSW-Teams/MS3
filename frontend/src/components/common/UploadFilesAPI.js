
export class UploadFilesAPI {

  constructor() {
  }

  async uploadFile(files, onUploadProgress) {

    let formData = new FormData();

    files.map((file, index) => {
      formData.append(`file${index}`, file);
    });

    fetch('/api/giustifica/caricaFile', {
      // content-type header should not be specified!
      method: 'POST',
      body: formData,
      onUploadProgress,
    })
      .then(response => response.json())
      .then(success => {
      })
      .catch(error => console.log(error)
      );
  }

  async getFiles() {
    return null;  //To do..
  };

}
