import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./layouts";

// Route Views
import GlobalScheduleView from "./views/GlobalScheduleView";
import SingleScheduleView from "./views/SingleScheduleView";

export default [
  {
    path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/Global-Schedule" />
  }, 
  {
    path: "/Private-Schedule",
    layout: DefaultLayout,
    component: SingleScheduleView
  },
  {
    path: "/Global-Schedule",
    layout: DefaultLayout,
    component:  GlobalScheduleView
  }
];

