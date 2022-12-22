import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import GlobalScheduleView from "./views/GlobalScheduleView";
import SingleScheduleView from "./views/SingleScheduleView";
import InsertAssignedSheet from "./views/InsertAssignedSheet";

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
    path: "/Pianifica-Turno",
    layout: DefaultLayout,
    component:  InsertAssignedSheet
  }

];

