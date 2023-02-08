import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import GlobalScheduleView from "./views/GlobalScheduleView";
import { SchedulerGeneratorView } from "./views/ScheduleGeneratorView";
import SingleScheduleView from "./views/SingleScheduleView";
import UserProfileView from "./views/UserProfileView"
import UserProfilesView from "./views/UserProfilesView"
import UserProfileViewId from "./views/UserProfileViewId";
import LoginView from "./views/LoginView"
import EmptyLayout from "./layouts/LoginLayout/Empty";


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

];

