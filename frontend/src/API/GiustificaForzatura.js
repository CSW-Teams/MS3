export class GiustificaForzatura {
   async caricaGiustifica(message, utenteId, delibere) {
     let giustifica = new Object();
     giustifica.message=message;
     giustifica.utente_id=utenteId;
     giustifica.delibere=delibere;

     const requestOptions = {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify(giustifica)
     };
     const url = "/api/giustifica/carica";
     const response = await fetch(url , requestOptions);
     return response.status;
   }

   async CaricaFile(file) {

     const requestOptions = {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify(file)
     };
     const url = "/api/giustifica/caricaFile";
     const response = await fetch(url , requestOptions);
     return response.status;

   }
}
