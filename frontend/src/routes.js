import React from "react";
import { Redirect } from "react-router-dom";

// Layout Types
import { DefaultLayout } from "./multitenancyapp/layouts";

// Route Views
import EmptyLayout from "./multitenancyapp/layouts/LoginLayout/Empty";
import MultiTenancyLoginView from "./multitenancyapp/views/MultiTenancyLoginView";
import TenantUsersView from "./multitenancyapp/views/TenantUsersView";


export default [
  {
    // For multi-tenancy test
    path: "/",
    exact: true,
    layout: DefaultLayout,
    component: () => <Redirect to="/multitenancy/login/" />
  },
  {
    // For multi-tenancy test
    path: "/multitenancy/info-utenti",
    layout: DefaultLayout,
    component: TenantUsersView
  },
  {
    // For multi-tenancy test
    path: "/multitenancy/login/",
    layout: EmptyLayout,
    component: MultiTenancyLoginView
  }
];
