import React from "react";
import { NavItem, NavLink, Badge, Collapse, DropdownItem } from "shards-react";
import {NotificationAPI} from "../../../../API/NotificationAPI";
export default class Notifications extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      notifications: [], // Inizializza le notifiche come array vuoto
    };

    this.notificationAPI = new NotificationAPI();
    this.toggleNotifications = this.toggleNotifications.bind(this);
  }

  async componentDidMount() {
      // Ottieni le notifiche al caricamento del componente
      await this.fetchNotifications();
      // Aggiorna le notifiche periodicamente, ad esempio ogni 60 secondi
      this.intervalId = setInterval(() => this.fetchNotifications(), 60000);
    }
  componentWillUnmount() {
    // Cancella l'intervallo quando il componente viene smontato
    clearInterval(this.intervalId);
  }
  toggleNotifications() {
    this.setState({
      visible: !this.state.visible
    });
  }
  async fetchNotifications() {
      try {
        const notifications = await this.notificationAPI.getNotification(localStorage.getItem("id"));
        this.setState({ notifications });
      } catch (error) {
        console.error('Errore durante il recupero delle notifiche:', error);
      }
    }
 removeNotification = async (notificationId,description) => {
    try {
      // Chiamata API o logica di rimozione delle notifiche
      // In questo caso, ipotizziamo che la tua API supporti una funzione di rimozione
      console.log(notificationId)
      await this.notificationAPI.removeNotification(notificationId,description,localStorage.getItem("id"));

      // Aggiorna lo stato dopo la rimozione
      await this.fetchNotifications();
    } catch (error) {
      console.error('Errore durante la rimozione della notifica:', error);
    }
  };

  render() {
      const { notifications } = this.state;

      return (
        <NavItem className="border-right dropdown notifications" style={{cursor:"pointer"}}>
          <NavLink
            className="nav-link-icon text-center"
            onClick={this.toggleNotifications}
          >
            <div className="nav-link-icon__wrapper">
              <i className="material-icons">&#xE7F4;</i>
              <Badge pill theme="danger">
                {notifications.length}
              </Badge>
            </div>
          </NavLink>
          <Collapse
            open={this.state.visible}
            className="dropdown-menu dropdown-menu-small"
          >
            {notifications.map((notifica, index) => (
              <DropdownItem key={notifica.idNotification} onClick={() => this.removeNotification(notifica.idNotification,notifica.description)}>
                <div className="notification__icon-wrapper">
                  <div className="notification__icon">
                    <i className="material-icons">&#xE6E1;</i>
                  </div>
                </div>
                <div className="notification__content">
                  <span className="notification__category">Cambio Turno</span> //da sostituire
                  <p>{notifica.description}</p>
                </div>
              </DropdownItem>
            ))}
            <DropdownItem className="notification__all text-center">
              Vedi tutte le notifiche
            </DropdownItem>
          </Collapse>
        </NavItem>
      );
    }
}
