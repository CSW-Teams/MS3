import {
    AppointmentForm,
  } from '@devexpress/dx-react-scheduler-material-ui';

import Drawer from '@material-ui/core/Drawer';

/**
 * Questo componente aggiunge una label al form che permette di modificare un assegnazione turno
 * @param {*} param0
 * @returns
 */
export  const BasicLayout = ({ onFieldChange, appointmentData, ...restProps }) => {

    return (

      <AppointmentForm.BasicLayout
        appointmentData={appointmentData}
        onFieldChange={onFieldChange}
        {...restProps}
      >
        <AppointmentForm.Label
          text="Effettua le modifiche selezionando i nuovi utenti da allocare "
          type="ordinaryLabel"
        />

      </AppointmentForm.BasicLayout>
    );
  };


/**
 * Questo componente è utilizzato nel form che si crea quando bisogna modificare un assegnazione turno.
 * Serve per eliminare alcune componenti che il template mette a disposizione di default.
 * Utilizzato nel componente <AppointmentForm>
 * @returns
 */
export const Nullcomponent = () => {
    // eslint-disable-next-line react/destructuring-assignment
    return null;
  };


/**
 * Questo componente crea la base del form attraverso cui sarà possibile modificare un assegnazione turno
 * @param {*} param0
 * @returns
 */
export const Overlay = ({
    children,
    visible,
    className,
    fullSize,
    target,
    onHide,
  }) => {


    return (
      <Drawer anchor='bottom' open={visible} >
        <div style={{height: '5vh',}}></div>
        {children}
        <div style={{height: '15vh',}}></div>
      </Drawer>

    );
  };
