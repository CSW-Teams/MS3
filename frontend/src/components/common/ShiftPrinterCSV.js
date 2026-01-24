import React from 'react';
import {CSVLink} from 'react-csv';
import {Button} from "@mui/material";

/**FIXME: non sono riuscito a scrivere questo componente come classe.
 * Il problema sta nel fatto che per qualche motivo i rawShifts nelle props sono sempre vuoti,
 * anche se nel componente padre se loggati sono stampati correttamente.
 * Usando un componente funzione invece funziona tutto.
 * Dato che non credo nella magia nera, e che secondo me i componenti classe sono più comodi e leggibili,
 * se qualcuno riesce a capire il perché di questo comportamento
 * e a correggerlo, sarebbe molto apprezzato.
 */
export function ShiftPrinterCSV(props) {

  /**
   * Dobbiamo trasformare i turni grezzi in oggetti che possiamo stampare come CSV
   */
  let printableShifts = props.rawShifts.map((rawShift) => {
    return {
      "Turno": rawShift.title,    // Si assume che indichi anche mansione e seervizio
      "Data e ora inizio": rawShift.startDate,
      "Data e ora fine": rawShift.endDate,
      "Utenti allocati": rawShift.utenti_guardia.map(buildNameFromUser),
      // stampiamo gli utenti in reperibilità solo se il turno la prevede
      "Utenti reperibili": rawShift.reperibilitaAttiva ? rawShift.utenti_reperibili.map(buildNameFromUser) : [],
    }
  });

  const handleClick = (event) => {
    if (!props.enable) {
      event.preventDefault();
    }
  };

  return (
    <Button
      variant="contained"
      style={props.style}
      component={CSVLink}
      data={printableShifts}
      onClick={handleClick}
      disabled={!props.enable}
    >
      {props.textLink}
    </Button>
  )
}

function buildNameFromUser(user) {
  return user.name + " " + user.lastname;
}
