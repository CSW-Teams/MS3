import React, { useState, useEffect, useRef } from "react";
import {UploadFilesAPI} from "../../API/UploadFilesAPI"
import { t } from "i18next";
import {toast} from "react-toastify";

const FilesUpload = ({type, request, updateRequest}) => {


  const [selectedFiles, setSelectedFiles] = useState('');
  const [progressInfos, setProgressInfos] = useState({ val: [] });
  const [message, setMessage] = useState([]);
  const [fileInfos, setFileInfos] = useState([]);
  const progressInfosRef = useRef(null)

  useEffect(() => {
    let UploadAPI = new UploadFilesAPI()
    try {
      UploadAPI.getFiles().then((response) => {
        setFileInfos(response.data);
      });
    } catch (err) {

      toast(t('Connection Error, please try again later'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
    }
  }, []);

  const selectFiles = (event) => {
    setSelectedFiles(event.target.files);
    setProgressInfos({ val: [] });
  };

  const upload = async (idx, file) => {
    let _progressInfos = [...progressInfosRef.current.val];
    let uploadAPI = new UploadFilesAPI();
    let response = null;
    if (type === "retirement") {
      try {
        response = await uploadAPI.uploadFileRetirement(file, (event) => {
          _progressInfos[idx].percentage = Math.round(
            (100 * event.loaded) / event.total
          );
          setProgressInfos({ val: _progressInfos });
        }, request.idRequest)
      } catch (err) {

        toast(t('Connection Error, please try again later'), {
          position: 'top-center',
          autoClose: 1500,
          style : {background : "red", color : "white"}
        })
        return
      }
    } else {
      try {

        response = await uploadAPI.uploadGiustifica(file, (event) => {
          _progressInfos[idx].percentage = Math.round(
            (100 * event.loaded) / event.total
          );
          setProgressInfos({val: _progressInfos});
        })
      } catch (err) {

        toast(t('Connection Error, please try again later'), {
          position: 'top-center',
          autoClose: 1500,
          style : {background : "red", color : "white"}
        })
        return
      }
    }
    if(response.status === 202){
      setMessage((prevMessage) => ([
        ...prevMessage,
        t("File successfully uploaded: ") + file.name,
      ]));
    }else if(response.status ===417){
      setMessage((prevMessage) => ([
        ...prevMessage,
        t("Error uploading file: ")+ file.name+ "!",
      ]));
    }

    /* todo questo è stato messo solo per fare in modo che la tabella si aggiorni dicendo che l'allegato è presente, va gestito meglio quando verrà implementato il download dell'allegato
    *   probabilmente conviene mettere un booleano (tipo allegatoPresente), e recuperare il vero allegato nella fase di download
    * */
    request.file = true;
    updateRequest(request);
  };

  const uploadFiles = () => {
    const files = Array.from(selectedFiles);
    let UploadFiles = new UploadFilesAPI();

    let _progressInfos = files.map(file => ({ percentage: 0, fileName: file.name }));

    progressInfosRef.current = {
      val: _progressInfos,
    }

    const uploadPromises = files.map((file, i) => upload(i, file));

    try {
      Promise.all(uploadPromises)
        .then(() => UploadFiles.getFiles())
        .then((files) => {
          setFileInfos(files.data);
        });
    } catch (err) {

      toast(t('Connection Error, please try again later'), {
        position: 'top-center',
        autoClose: 1500,
        style : {background : "red", color : "white"}
      })
      return
    }

    setMessage([]);

    /* todo questo è stato messo solo per fare in modo che la tabella si aggiorni dicendo che l'allegato è presente, va gestito meglio quando verrà implementato il download dell'allegato
    *   probabilmente conviene mettere un booleano (tipo allegatoPresente), e recuperare il vero allegato nella fase di download
    * */
    request.allegato = true;
    updateRequest(request);

  };

  return (
    <div>
      {progressInfos && progressInfos.val.length > 0 &&
      progressInfos.val.map((progressInfo, index) => (
        <div className="mb-2" key={index}>
          <span>{progressInfo.fileName}</span>
          <div className="progress">
            <div
              className="progress-bar progress-bar-info"
              role="progressbar"
              aria-valuenow={progressInfo.percentage}
              aria-valuemin="0"
              aria-valuemax="100"
              style={{ width: progressInfo.percentage + "%" }}
            >
              {progressInfo.percentage}%
            </div>
          </div>
        </div>
      ))}

      <div className="row my-3">
        <div className="col-8">
          <label className="btn btn-default p-0">
            <input type="file" multiple onChange={selectFiles} />
          </label>
        </div>

        <div className="col-4">
          <button
            className="btn btn-success btn-sm"
            disabled={!selectedFiles}
            onClick={uploadFiles}
          >
            {t('Upload')}
          </button>
        </div>
      </div>

      {message.length > 0 && (
          <ul>
            {message.map((item, i) => {
              return <li> <h6 key={i}>{item}</h6></li>;
            })}
          </ul>
      )}

      <div className="card">
        <ul className="list-group list-group-flush">
          {fileInfos &&
          fileInfos.map((file, index) => (
            <li className="list-group-item" key={index}>
              <a href={file.url}>{file.name}</a>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default FilesUpload;
