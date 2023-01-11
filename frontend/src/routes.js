import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import GlobalScheduleView from "./views/GlobalScheduleView";
import { SchedulerGeneratorView } from "./views/ScheduleGeneratorView";
import SingleScheduleView from "./views/SingleScheduleView";
import UserProfileView from "./views/UserProfileView"

export default [
  {
    path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/pianificazione-globale" />
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
    path: "/user-profile",
    layout: DefaultLayout,
    component: UserProfileView
  }
];

