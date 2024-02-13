import React from "react";
import { NavItem, NavLink, Badge, Collapse, DropdownItem } from "shards-react";
import { NotificationAPI } from "../../../../API/NotificationAPI";
import { useTranslation } from 'react-i18next';

export default function Notifications() {
  const { t } = useTranslation();

  const [state, setState] = React.useState({
    visible: false,
    notifications: [],
  });

  const notificationAPI = new NotificationAPI();

  const toggleNotifications = () => {
    setState((prevState) => ({
      ...prevState,
      visible: !prevState.visible,
    }));
  };

  const fetchNotifications = async () => {
    try {
      const notifications = await notificationAPI.getNotification(localStorage.getItem("id"));
      setState((prevState) => ({
        ...prevState,
        notifications,
      }));
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  };

  const removeNotification = async (notificationId, description) => {
    try {
      console.log(notificationId)
      await notificationAPI.removeNotification(notificationId, description, localStorage.getItem("id"));
      await fetchNotifications();
    } catch (error) {
      console.error('Error removing notification:', error);
    }
  };

  React.useEffect(() => {
    fetchNotifications();
    const intervalId = setInterval(() => fetchNotifications(), 6000);
    return () => clearInterval(intervalId);
  }, []);

  const { notifications } = state;

  return (
    <NavItem className="border-right dropdown notifications" style={{cursor:"pointer"}}>
      <NavLink
        className="nav-link-icon text-center"
        onClick={toggleNotifications}
      >
        <div className="nav-link-icon__wrapper">
          <i className="material-icons">&#xE7F4;</i>
          <Badge pill theme="danger">
            {notifications.length}
          </Badge>
        </div>
      </NavLink>
      <Collapse
        open={state.visible}
        className="dropdown-menu dropdown-menu-small"
      >
        {notifications.map((notifica, index) => (
          <DropdownItem key={notifica.idNotification} onClick={() => removeNotification(notifica.idNotification, notifica.description)}>
            <div className="notification__icon-wrapper">
              <div className="notification__icon">
                <i className="material-icons">&#xE6E1;</i>
              </div>
            </div>
            <div className="notification__content">
              <span className="notification__category">{t('Shift Change')}</span>
              <p>{notifica.description}</p>
            </div>
          </DropdownItem>
        ))}
        <DropdownItem className="notification__all text-center">
          {t("View all notifications")}
        </DropdownItem>
      </Collapse>
    </NavItem>
  );
}
