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
let itineraryObj = [
  {
    id: 1,
    showSelection: false,
    selectedLocation: null,
    selectedDate: null,
    selectedTime: null,
    busyLevel: 0,
  },
];
let predictDataObj = [];
let targetInfoObj =[]

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
      poiData: poiDataObj,
      zoneData: zoneDataObj,
      busy: 1,
      itinerary: itineraryObj,
      predictData: predictDataObj,
      targetInfo: targetInfoObj
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
    setPoiData(state, obj) {
      state.poiData = obj;
    },
    setZoneData(state, obj) {
      state.zoneData = obj;
    },
    setBusy(state, obj) {
      state.busy = obj;
    },
    setItinerary(state, obj) {
      state.itinerary = obj;
    },
    setPredictData(state, obj){
      state.predictData = obj
    },
    setTargetInfo( state, obj){
      state.targetInfo = obj
    }
  },
  actions: {
    updateUserName({ commit }, value) {
      commit("userName", value);
    },
  },
});

export default store;
