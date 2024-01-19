import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import GlobalScheduleView from "./views/utente/GlobalScheduleView";
import { SchedulerGeneratorView } from "./views/pianificatore/ScheduleGeneratorView";
import SingleScheduleView from "./views/utente/SingleScheduleView";
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
import MedicalServicesView from "./views/configuratore/MedicalServicesView";
import InserisciFestivita from "./views/configuratore/InserisciFestivita";
import ModifyUserProfileView from "./views/utente/ModifyUserProfileView";
import SingleUserProfileView from "./views/utente/SingleUserProfileView";


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
    path: "/single-user-profile",
    layout: DefaultLayout,
    component: SingleUserProfileView
  },
  {
    path: "/modify-single-user-profile",
    layout: DefaultLayout,
    component: ModifyUserProfileView
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
  {
    path: "/servizi",
    layout: DefaultLayout,
    component:  MedicalServicesView
  },

  {
    path: "/nuova-festivita",
    layout: DefaultLayout,
    component: InserisciFestivita
  },
];

