import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./multitenancyapp/layouts";

// Route Views
import GlobalScheduleView from "./views/utente/GlobalScheduleView";
import { SchedulerGeneratorView } from "./views/pianificatore/ScheduleGeneratorView";
import SingleScheduleView from "./views/utente/SingleScheduleView";
import Preference from "./views/utente/Preference"
import UserProfilesView from "./views/utente/UserProfilesView"
import LoginView from "./views/utente/LoginView"
import CambiaPasswordView from "./views/utente/CambiaPasswordView"
import EmptyLayout from "./multitenancyapp/layouts/LoginLayout/Empty";
import ConfigurazioneVincoli from "./views/configuratore/ConfigurazioneVincoli";
import RegistraUtenteView from "./views/configuratore/RegistraUtenteView";
import RichiesteRitiroView from "./views/utente/RichiesteRitiroView";
import ShiftChangeView from "./views/utente/ShiftChangeView";
import MedicalServicesView from "./views/configuratore/MedicalServicesView";
import InserisciFestivita from "./views/configuratore/InserisciFestivita";
import ModifyUserProfileView from "./views/utente/ModifyUserProfileView";
import SingleUserProfileView from "./views/utente/SingleUserProfileView";
import PersonalSingleUserProfileView
  from "./views/utente/PersonalSingleUserProfileView";
import MultiTenancyLoginView from "./multitenancyapp/views/MultiTenancyLoginView";
import TenantUsersView from "./multitenancyapp/views/TenantUsersView";


export default [
  {
    // For multi-tenancy test
    path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/multitenancy/login/" />

    /*path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/login/" />*/
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
    path: "/personal-single-user-profile",
    layout: DefaultLayout,
    component: PersonalSingleUserProfileView
  },
  {
    path: "/single-user-profile/:idUser",
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
    // For multi-tenancy test
    path: "/multitenancy/info-utenti",
    layout: DefaultLayout,
    component: TenantUsersView
  },
  {
    path: "/login/",
    layout: EmptyLayout,
    component: LoginView
  },
  {
    // For multi-tenancy test
    path: "/multitenancy/login/",
    layout: EmptyLayout,
    component: MultiTenancyLoginView
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
    path: "/gestione-festivita",
    layout: DefaultLayout,
    component: InserisciFestivita
  },
];
