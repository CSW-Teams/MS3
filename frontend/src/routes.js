import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import GlobalScheduleView from "./views/utente/GlobalScheduleView";
import { SchedulerGeneratorView } from "./views/pianificatore/ScheduleGeneratorView";
import SingleScheduleView from "./views/utente/SingleScheduleView";
import UserProfileView from "./views/utente/UserProfileView"
import Preference from "./views/utente/Preference"
import UserProfilesView from "./views/utente/UserProfilesView"
import UserProfileViewId from "./views/utente/UserProfileViewId";
import LoginView from "./views/utente/LoginView"
import CambiaPasswordView from "./views/utente/CambiaPasswordView"
import EmptyLayout from "./layouts/LoginLayout/Empty";
import ConfigurazioneVincoli from "./views/configuratore/ConfigurazioneVincoli";
import RegistraUtenteView from "./views/configuratore/RegistraUtenteView";
import RichiesteRitiroView from "./views/utente/RichiesteRitiroView";
import ShiftChangeView from "./views/utente/ShiftChangeView";


export default [
  {
    path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/login/" />
  },
  {
    path: "/pianificazione-privata",
    layout: DefaultLayout,
    component: SingleScheduleView
  },
  {
    path: "/pianificazione-globale",
    layout: DefaultLayout,
    component:  GlobalScheduleView
  },
  {
    path: "/generazione-scheduling",
    layout: DefaultLayout,
    component:  SchedulerGeneratorView
  },
  {
    path: "/user-profile/1",
    layout: DefaultLayout,
    component: UserProfileView
  },
  {
    path: "/info-utenti",
    layout: DefaultLayout,
    component: UserProfilesView
  },
  {
    path: "/profilo-utente/:idUser",
    layout: DefaultLayout,
    component: UserProfileViewId
  },
  {
    path: "/login/",
    layout: EmptyLayout,
    component: LoginView
  },
  {
    path: "/preference/",
    layout: DefaultLayout,
    component: Preference
  },
  {
    path: "/configurazione-vincoli/",
    layout: DefaultLayout,
    component: ConfigurazioneVincoli
  },
  {
    path: "/cambia-password/",
    layout: DefaultLayout,
    component: CambiaPasswordView
  },
  {
    path: "/nuovo-utente/",
    layout: DefaultLayout,
    component: RegistraUtenteView
  },
  {
    path: "/richieste-ritiro/",
    layout: DefaultLayout,
    component: RichiesteRitiroView
  },
  {
    path: "/scambio-turni",
    layout: DefaultLayout,
    component:  ShiftChangeView
  },

];

