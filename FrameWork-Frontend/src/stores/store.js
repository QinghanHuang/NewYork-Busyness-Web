import { createStore } from "vuex";

let userObj = {
  name: " ",
  birthday: "",
  introduction: "",
  gender: "",
  username: "",
  email: "",
  phoneNumber: "",
  avatar: "0",
};

let poiObj = [
  {
    id: "00",
    name: null,
  },
];

let zoneObj = [{}];
let poiDataObj = [{}];
let zoneDataObj = [{}];

const store = createStore({
  state() {
    return {
      sideBarShow: false,
      auth: false,
      userName: "",
      infoWindowShow: false,
      userInfo: userObj,
      poiInfo: poiObj,
      zoneInfo: zoneObj,
      locationID: "",
      poiList: [],
      poiData: poiDataObj,
      zoneData: zoneDataObj,
      busy: 1,
    };
  },
  mutations: {
    setSideBarShow(state, value) {
      state.sideBarShow = value;
    },
    setUserName(state, value) {
      state.userName = value;
    },
    setInfoWindowShow(state, value) {
      state.infoWindowShow = value;
    },
    setUserInfo(state, obj) {
      state.userInfo = obj;
    },
    setPoiInfo(state, obj) {
      state.poiInfo = obj;
    },
    setZoneInfo(state, obj) {
      state.zoneInfo = obj;
    },
    setLocationID(state, value) {
      state.locationID = value;
    },
    setAuth(state, value) {
      state.auth = value;
    },
    setPoiList(state, obj) {
      state.poiList = obj;
    },
    setPoiData(state, obj) {
      state.poiData = obj;
    },
    setZoneData(state, obj) {
      state.zoneData = obj;
    },
    setBusy(state, obj) {
      state.busy = obj;
    },
  },
  actions: {
    updateUserName({ commit }, value) {
      commit("userName", value);
    },
  },
});

export default store;
