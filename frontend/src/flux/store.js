import { EventEmitter } from "events";

import Dispatcher from "./dispatcher";
import Constants from "./constants";
import {attore, configuratore, pianificatore} from "../data/sidebar-nav-items";
import i18n from '../i18n';

function translateSidebarItems(items, translateFn) {
  return items.map(item => ({
    ...item,
    title: translateFn(item.title),
  }));
}

function getNavBar() {
  const att = localStorage.getItem('actor');
  const translateFn = key => i18n.t(key);

  if (att === 'DOCTOR') {
    return translateSidebarItems(attore(), translateFn);
  } else if (att === 'PLANNER') {
    return translateSidebarItems(pianificatore(), translateFn);
  } else {
    return translateSidebarItems(configuratore(), translateFn);
  }
}

let _store = {
  menuVisible: false,
  navItems: getNavBar(),
};

class Store extends EventEmitter {
  constructor() {
    super();

    this.registerToActions = this.registerToActions.bind(this);
    this.toggleSidebar = this.toggleSidebar.bind(this);

    Dispatcher.register(this.registerToActions.bind(this));
  }

  registerToActions({ actionType, payload }) {
    switch (actionType) {
      case Constants.TOGGLE_SIDEBAR:
        this.toggleSidebar();
        break;
      default:
    }
  }

  toggleSidebar() {
    _store.menuVisible = !_store.menuVisible;
    this.emit(Constants.CHANGE);
  }

  getMenuState() {
    return _store.menuVisible;
  }

  getSidebarItems() {
    return _store.navItems;
  }

  addChangeListener(callback) {
    this.on(Constants.CHANGE, callback);
  }

  removeChangeListener(callback) {
    this.removeListener(Constants.CHANGE, callback);
  }
}

export default new Store();
