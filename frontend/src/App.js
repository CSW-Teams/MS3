import React from "react";
import { BrowserRouter as Router, Route } from "react-router-dom";
import { I18nextProvider } from 'react-i18next';
import i18n from './i18n';

import routes from "./routes";
import withTracker from "./withTracker";

import "bootstrap/dist/css/bootstrap.min.css";
import "./shards-dashboard/styles/shards-dashboards.1.1.0.min.css";
import {ToastContainer} from "react-toastify";
import PanicTableau from "./components/common/Panic";

const App = () => (
  <I18nextProvider i18n={i18n}>
    <Router basename={process.env.REACT_APP_BASENAME || ""}>
      <div>
        {routes.map((route, index) => {
          return (
            <Route
              key={index}
              path={route.path}
              exact={route.exact}
              component={withTracker(props => {
                return (
                  <route.layout {...props}>
                    <route.component {...props} />
                  </route.layout>
                );
              })}
            />
          );
        })}
        <ToastContainer/>
      </div>
    </Router>
    <PanicTableau/>
  </I18nextProvider>
);

export default App;
