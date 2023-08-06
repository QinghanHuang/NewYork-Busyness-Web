<script setup>
import {
  CloseBold,
  Search,
  Expand,
  Minus,
  Plus,
  DArrowRight,
  Close,
  ArrowRightBold,
  ArrowDownBold,
} from "@element-plus/icons-vue";
import { ref, onMounted, onUnmounted, watchEffect, Transition, computed } from "vue";
import { useStore } from "vuex";
import { ElMessage, ElLoading } from "element-plus";
import { get } from "@/net/axios";
import { throttle, debounce } from "lodash";
import InfoPage from "../components/welcome/LocInfoPage.vue";
import WeatherPage from "../components/welcome/WeatherPage.vue";
import { weatherIconDict } from "../assets/weather/weatherIconDict.js";

// define
const store = useStore();
const infoWindowShow = computed(() => store.state.infoWindowShow);
const sideBarShow = computed(() => store.state.sideBarShow);
const isSmallScreen = ref(false);
const sideBarRef = ref(null);
const openSideBarRef = ref(null);
const inputRef = ref(null);
const overlayRef = ref(null);
const showOverlay = ref(false);
const searchTerm = ref("");
const markerList = [];
const weatherSrc = ref("");
const currTemp = ref(null);
const mapStyleSwitch = ref("HEATMAP");
const futureDates = ref([]);
const forecastTime = ref(0);
const todayDate = ref("");
const directionsDisplay = new google.maps.DirectionsRenderer();
let infoWindowList = [];
let currTime;
let weatherCurData;
let weatherForDate;
let geocoder;
let map;
let marker;
let autoOptions;
let autocomplete;
let mapOptions;
let heatMapObj;
let mapInfoWindow;
let dataList;
let locaitonID;
let isHeatmap;

// style edit
const inputStyle = ref({});
const buttonStyle = ref({});
const containerStyle = ref({});
const infoStyle = ref({});
const zoomStyle = ref({});
const typeChooseStyle = ref({});
const mapChooseStyle = ref({});
const searchAreaStyle = ref({});
const sliderStyle = ref({});
const weatherWindowTrigger = ref(null);
const weatherWindowPlacement = ref(null);
const openSideBarButtonStyle = ref({});
const titleBarStyle = ref({});
const infoWindowCloseBtnStyle = ref({});
const colorDict = {
  1: "green",
  2: "blue",
  3: "yellow",
  4: "orange",
  5: "red",
};
const iconDict = {
  1: "../public/1.svg",
  2: "../public/2.svg",
  3: "../public/3.svg",
  4: "../public/4.svg",
  5: "../public/5.svg",
};

watchEffect(() => {
  const isSmall = isSmallScreen.value;
  weatherWindowTrigger.value = isSmall ? "click" : "hover";
  weatherWindowPlacement.value = isSmall ? "right" : "right";
  infoWindowCloseBtnStyle.value = {
    position: "absolute",
    right: isSmall ? "15px" : "501px",
    top: isSmall ? "calc(0.6 * var(--screen-height) - 32px)" : "45%",
  };
  titleBarStyle.value = {
    width: isSmall ? "100vw" : "200px",
    marginTop: isSmall ? "" : "17px",
    marginRight: isSmall ? "" : "10px",
    borderRadius: isSmall ? "" : "5px",
    right: "0px",
    height: "60px",
  };
  openSideBarButtonStyle.value = {
    top: isSmall ? "10px" : "",
    boxShadow: isSmall ? "0 0 0 0" : "",
    transform: isSmall ? "scale(1.45)" : "",
  };
  searchAreaStyle.value = {
    left: infoWindowShow.value ? (isSmall ? "" : "-20%") : "",
    top: isSmall ? "0" : "2.5%",
    transition: "left width 0.5s ease",
  };
  mapChooseStyle.value = {
    backdropFilter: "blur(5px)",
    borderRadius: "10px",
    boxShadow: "0 0 5px rgba(0, 0, 0, 1)",
    transition: "bottom 0.5s ease",
    marginBottom: "10px",
    display: "flex",
    flexWrap: "wrap",
    alignItems: "center",
    justifyContent: "center",
    position: "absolute",
    bottom: isSmall ? (infoWindowShow.value ? "calc(0.4 * var(--screen-height))" : "5px") : "20px",
    left: isSmall ? "10px" : "10px",
  };
  sliderStyle.value = {
    overflow: "hidden",
    backdropFilter: "blur(5px)",
    width: "60px",
    // backgroundColor: "white",
    boxShadow: "0 0 5px rgba(0, 0, 0, 1)",
    borderRadius: "10px",
    paddingTop: "50px",
    paddingBottom: "20px",
    transition: "bottom  0.5s ease, height 0.5s ease",
    bottom: isSmall
      ? infoWindowShow.value
        ? "calc(0.47 * var(--screen-height))"
        : "70px"
      : "80px",
    height: isSmall ? (infoWindowShow.value ? "200px" : "350px") : "350px",
    left: isSmall ? "10px" : "10px",
  };
  zoomStyle.value = {
    flexDirection: isSmall ? "column" : "row",
    position: "absolute",
    bottom: isSmall ? "50%" : "35px",
    left: isSmall ? "85vw" : "400px",
  };
  typeChooseStyle.value = {
    backgroundColor: "white",
    color: "black",
    margin: "10px 3px",
    borderRadius: "20px",
    boxShadow: "0 0 5px rgba(0, 0, 0, 01)",
  };
  containerStyle.value = {
    backgroundColor: "#305a92",
    height: "100%",
    zIndex: 1,
    width: isSmall ? "330px" : "500px",
  };
  inputStyle.value = {
    position: "relative",
    top: isSmall ? "70px" : "",
    left: isSmall ? "8%" : "",
    width: isSmall ? "75vw" : infoWindowShow.value ? "300px" : "400px",
  };
  buttonStyle.value = {
    position: "relative",
    top: isSmall ? "10px" : "",
    left: isSmall ? "7%" : "",
    marginLeft: isSmall ? "7px" : "",
    width: isSmall ? "37vw" : "",
  };
  infoStyle.value = {
    height: isSmall ? "calc(0.4 * var(--screen-height))" : "var(--screen-height)",
    borderRadius: isSmall ? "5px" : "",
  };
});

// **********************methods***********************
// ----------------------loader------------------------
const showLoader = () => {
  return ElLoading.service({
    lock: true,
    text: "Loading",
    background: "rgba(0, 0, 0, 0.7)",
  });
};

// ----------------------time relative----------------------
// get time
const acquireTime = () => {
  const today = new Date();

  // Extract the year, month, day, and hour
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0"); // Months are zero-based, so we add 1
  const day = String(today.getDate()).padStart(2, "0");
  const hours = String(today.getHours()).padStart(2, "0");

  // Format the date and time as "YYYY-MM-DD-HH"
  const formattedDateTime = `${year}-${month}-${day}-${hours}`;
  return formattedDateTime;
};

// slider time formatter
const formatTooltip = (val) => {
  return `${val}:00`;
};

// slider time show
const sliderTimeShow = ref({});
const timePoints = [0, 6, 12, 18, 23];
const commonStyle = {
  color: "#305a92",
  fontSize: "10px",
};

timePoints.forEach((hour) => {
  sliderTimeShow.value[hour] = {
    style: commonStyle,
    label: `${hour}:00`,
  };
});

// time formatter
const formatDate = (date) => {
  const year = date.getFullYear();
  const month = padNumber(date.getMonth() + 1);
  const day = padNumber(date.getDate());
  return `${year}-${month}-${day}`;
};
const padNumber = (num) => {
  return num.toString().padStart(2, "0");
};

// generate Future Dates
const generateFutureDates = () => {
  const today = new Date();
  for (let i = 0; i <= 10; i++) {
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + i);
    futureDates.value.push(formatDate(futureDate));
  }
};

// ----------------------weather relative----------------------
const setWeather = () => {
  // weather api
  get("/api/weather/current", (res) => {
    weatherCurData = res;
    weatherSrc.value = `https://openweathermap.org/img/wn/${
      weatherIconDict[weatherCurData.weatherId]
    }@2x.png`;
    currTemp.value = Math.round(weatherCurData.temp);
  });
};

// ----------------------map relative----------------------
// move Map Center
const moveHome = () => {
  store.commit("setInfoWindowShow", false);
  clear();
  map.panTo({ lat: 40.74039, lng: -73.99937 });
  map.setZoom(13);
};

function moveMapCenter(offsetX, offsetY) {
  const center = map.getCenter();
  const pixelOffset = new google.maps.Point(offsetX, offsetY);
  const newCenter = map.getProjection().fromLatLngToPoint(center);
  newCenter.x += pixelOffset.x;
  newCenter.y += pixelOffset.y;
  const newLatLng = map.getProjection().fromPointToLatLng(newCenter);
  map.panTo(newLatLng);
}

const zoomIn = () => map.setZoom(map.getZoom() + 1);
const zoomOut = () => map.setZoom(map.getZoom() - 1);

const handleDateSelect = async (date) => {
  todayDate.value = date;
  currTime =
    forecastTime.value < 10 ? `${date}-0${forecastTime.value}` : `${date}-${forecastTime.value}`;
  mapStyleSwitch.value = "HEATMAP";
  disappearMarkers();
  disappearHeatmap();
  await setMarkers(currTime, todayDate);
  setHeatMap(currTime, todayDate);
  showMarkers();
};

const search = () => {
  if (isLoginFail()) return;
  clear();
  geocode({
    address: searchTerm.value.includes(",")
      ? searchTerm.value.match(/^[^,]+/)?.[0]
      : searchTerm.value,
  });
};

const clear = () => {
  mapInfoWindow.close();
  marker.setMap(null);
};

const getIdByName = (name) => {
  if (!dataList) return;
  dataList = computed(() => store.state.poiInfo).value;
  const foundItem = dataList.find((item) => item.name === name);
  return foundItem ? foundItem.id : null;
};

// useless
const geocode = (request) => {
  if (!request.address) return;
  clear();
  geocoder
    .geocode(request)
    .then((result) => {
      const { results } = result;
      map.setCenter(results[0].geometry.location);
      map.setZoom(15);
      marker.setPosition(results[0].geometry.location);
      marker.setMap(map);
      searchTerm.value = results[0].formatted_address.includes(",")
        ? results[0].formatted_address.match(/^[^,]+/)?.[0]
        : results[0].formatted_address;

      locaitonID = getIdByName(searchTerm.value);
      if (locaitonID) showLocInfo(locaitonID);

      return results;
    })
    .catch((e) => {
      ElMessage.warning("Geocode was not successful for the following reason: " + e);
    });
};

// ----------------------login auth fail----------------------

const isLoginFail = () => {
  if (!computed(() => store.state.auth).value) {
    ElMessage.warning("Please Login");
    openSideBar();
    return true;
  }

  return false;
};
const throttledIsLoginFail = throttle(isLoginFail, 200);
// ----------------------slider function----------------------
const sliderTimeChange = () => {
  if (throttledIsLoginFail()) return;
  store.commit("setInfoWindowShow", false);
  const targetTime =
    forecastTime.value < 10
      ? `${todayDate.value}-0${forecastTime.value}`
      : `${todayDate.value}-${forecastTime.value}`;
  const tempData = computed(() => store.state.poiData).value;
  const filteredData = tempData.filter((data) => data.time === targetTime);
  store.commit("setPredictData", filteredData);
  markerList.forEach((marker, index) => {
    const newIcon = {
      url: iconDict[filteredData[index].busy],
      scaledSize: new google.maps.Size(30, 30),
    };
    marker.setIcon(newIcon);
  });
  const locationInfo = computed(() => store.state.poiInfo).value;

  const tempData2 = [];
  filteredData.forEach((data, index) => {
    tempData2.push({
      location: new google.maps.LatLng(
        locationInfo[index].location.lat,
        locationInfo[index].location.lng
      ),
      weight: data.busy,
    });
  });
  heatMapObj.setData(tempData2);
};

// set marker
const setMarkers = async (currTime, todayDate) => {
  // clear markerList
  markerList.splice(0);

  // loader
  const loading = showLoader();

  // get data
  await get(`/api/prediction/poi/${todayDate.value}`, (message) => {
    dataList = message;
    store.commit("setPoiData", dataList);
  });

  // set data to predictData
  const filteredData = dataList.filter((data) => data.time === currTime);
  store.commit("setPredictData", filteredData);

  loading.close();

  if (!dataList) return;
  const locationInfo = computed(() => store.state.poiInfo).value;
  dataList.forEach((data) => {
    if (data.time !== currTime) return;
    const ID = data.pid;
    const busyLevel = data.busy;

    const customMarker = new window.google.maps.Marker({
      position: locationInfo[ID - 1].location,
      animation: google.maps.Animation.DROP,
      map,
      icon: {
        url: iconDict[data.busy],
        scaledSize: new google.maps.Size(30, 30),
      },
    });

    // click bounce
    customMarker.addListener("click", () => {
      customMarker.setAnimation(google.maps.Animation.BOUNCE);
      setTimeout(() => {
        customMarker.setAnimation(null);
      }, 500);
    });

    // click info window
    const contentString = `
    <div style="position: relative; top: -15px; width:100px;height:${
      locationInfo[ID - 1].name.length * 1.55 + 80
    }px;text-align:center">
      <h4>${locationInfo[ID - 1].name}</h4>
      <img style="" src="${locationInfo[ID - 1].photo}" alt="${
      locationInfo[ID - 1].name
    } img" width="100" height="70" >
    </div>
    `;

    customMarker.addListener("mouseover", () => {
      if (computed(() => store.state.infoWindowShow).value) return;
      mapInfoWindow.setContent(contentString);
      mapInfoWindow.open(map, customMarker);
    });

    customMarker.addListener("mouseout", () => {
      if (computed(() => store.state.infoWindowShow).value) return;
      // mapInfoWindow.close();
    });

    customMarker.addListener("click", () => {
      if (isLoginFail()) return;
      mapInfoWindow.setContent(contentString);
      mapInfoWindow.open(map, customMarker);

      map.panTo(locationInfo[ID - 1].location);
      store.commit("setBusy", data.busy);
      showLocInfo(ID);
    });

    markerList.push(customMarker);
  });
};

// set heatMap
const setHeatMap = async (currTime, todayDate) => {
  const tempData = [];
  const locationInfo = computed(() => store.state.poiInfo).value;
  let count = 0;

  dataList = computed(() => store.state.poiData).value;

  if (!dataList) return;

  dataList.forEach((data) => {
    if (data.time !== currTime) return;
    tempData.push({
      location: new google.maps.LatLng(
        locationInfo[count].location.lat,
        locationInfo[count].location.lng
      ),
      weight: data.busy,
    });
    count++;
  });

  heatMapObj = new google.maps.visualization.HeatmapLayer({
    data: tempData,
    radius: 50,
  });

  heatMapObj.setOptions({
    gradient: ["rgba(48, 90, 146, 0.5)", "yellow", "#fd7a28"],
    maxIntensity: 5,
    minIntensity: 1,
  });
};

// set all marker disappear
const disappearMarkers = () => {
  markerList.forEach((marker) => {
    marker.setVisible(false);
  });
};

// show marker
const showMarkers = () => {
  isHeatmap = false;
  showMarkersLazy();
};

const showMarkersLazy = () => {
  if (isHeatmap) return;
  const bounds = map.getBounds();
  markerList.forEach((marker) => {
    if (bounds.contains(marker.getPosition()) && !marker.getVisible()) {
      marker.setVisible(true);
      marker.setAnimation(google.maps.Animation.DROP);
    } else if (!bounds.contains(marker.getPosition())) {
      marker.setVisible(false);
    }
  });
};

// disappear heatmap
const disappearHeatmap = () => heatMapObj.setMap(null);

// show heatmap
const showHeatmap = () => {
  directionsDisplay.setMap(null);
  if (infoWindowList.length) {
    infoWindowList.forEach((infoWindow) => {
      infoWindow.close();
    });
  }
  if (isSmallScreen.value) store.commit("setInfoWindowShow", false);
  moveHome();
  heatMapObj.setMap(map);
  isHeatmap = true;
};

// map style change
const mapStyleChange = () => {
  if (mapStyleSwitch.value === "MARKERS") {
    clear();
    showHeatmap();
    disappearMarkers();
  } else {
    showMarkers();
    disappearHeatmap();
  }
};

// show poi location info
const showLocInfo = (id) => {
  if (isLoginFail()) return;
  store.commit("setLocationID", id);
  store.commit("setInfoWindowShow", true);
  if (!isSmallScreen.value) moveMapCenter(0.01, 0);
  else moveMapCenter(0, 0.003);
  if (!isHeatmap) marker.setMap(null);
};

// close poi location info
const closeInfo = () => store.commit("setInfoWindowShow", false);

// window size relative
const handleWindowResize = throttle(() => (isSmallScreen.value = window.innerWidth < 600), 200);

const closeSidebar = () => {
  store.commit("setSideBarShow", false);
  showOverlay.value = false;
};
const openSideBar = () => {
  store.commit("setSideBarShow", true);
  showOverlay.value = true;
};
const outsideClickFold = (event) => {
  if (sideBarShow) {
    if (
      sideBarRef.value &&
      !sideBarRef.value.contains(event.target) &&
      !openSideBarRef.value.contains(event.target) &&
      overlayRef.value.contains(event.target)
    ) {
      store.commit("setSideBarShow", false);
      showOverlay.value = false;
    }
  }
};

const showMarkerBegin = async () => {
  // store poi and taxi zone info
  await get("/api/poi/all", (res) => store.commit("setPoiInfo", res));
  await setMarkers(currTime, todayDate);
  setHeatMap(currTime, todayDate);
};

watchEffect(async () => {
  // watch the login auth of user
  const userAuth = computed(() => store.state.auth);
  if (userAuth.value) {
    // showMarkerBegin();
  }
});

watchEffect(() => {
  // watch opensidebarshow
  const isOpenSideBar = computed(() => store.state.sideBarShow).value;
  if (isOpenSideBar) showOverlay.value = true;
});

// life circle functions
onMounted(() => {
  // set auth
  // get("/api/user/me", () => store.commit("setAuth", true));

  // set weather
  setWeather();

  // adjust side bar size
  handleWindowResize();
  window.addEventListener("resize", handleWindowResize);

  // side bar outside click
  document.addEventListener("click", outsideClickFold);

  // side bar effect
  // store.commit("setSideBarShow", true);
  // showOverlay.value = true;

  // map options
  mapOptions = {
    center: { lat: 40.7829, lng: -73.9654 },
    zoom: 14,
    disableDefaultUI: true,
    mapId: "7e8e325b22fdf080",
  };

  // autocomplete
  autoOptions = {
    fields: ["formatted_address", "geometry", "name"],
    strictBounds: false,
    types: ["establishment"],
  };

  // map
  map = new google.maps.Map(document.getElementById("map"), mapOptions);

  // marker
  marker = new google.maps.Marker({
    map,
  });

  // geocoder
  geocoder = new google.maps.Geocoder();

  // mapInfoWindow
  mapInfoWindow = new google.maps.InfoWindow();

  google.maps.event.addListener(map, "bounds_changed", showMarkersLazy);

  // autocomplete
  autocomplete = new google.maps.places.Autocomplete(
    inputRef.value.$el.querySelector("input"),
    autoOptions
  );
  autocomplete.bindTo("bounds", map);
  autocomplete.addListener("place_changed", () => {
    marker.setVisible(false);

    const place = autocomplete.getPlace();

    if (!place.geometry || !place.geometry.location) {
      search();
      return;
    }

    clear();
    // If the place has a geometry, then present it on a map.
    if (place.geometry.viewport) {
      map.fitBounds(place.geometry.viewport);
      map.setZoom(15);
    } else {
      map.setCenter(place.geometry.location);
      map.setZoom(15);
    }
    if (isLoginFail()) return;
    marker.setPosition(place.geometry.location);
    marker.setVisible(true);
    marker.setMap(map);
    searchTerm.value = place.name;
    locaitonID = getIdByName(searchTerm.value);
    if (locaitonID) showLocInfo(locaitonID);
  });

  // set future dates option
  generateFutureDates();

  // set curr time
  currTime = acquireTime();
  forecastTime.value = +currTime.slice(11, 13);
  todayDate.value = currTime.slice(0, 10);

  // accquire real height
  const screenHeight = window.innerHeight;
  document.documentElement.style.setProperty("--screen-height", `${screenHeight}px`);

  showMarkerBegin();
});

onUnmounted(() => {
  window.removeEventListener("resize", handleWindowResize);
  document.removeEventListener("click", outsideClickFold);
});
</script>

<template>
  <!-- main view for all view objects -->
  <div class="main-view">
    <!-- map view for map and map relative elements -->
    <div class="map-view">
      <!-- map -->
      <div id="map" style="width: 100vw; height: var(--screen-height)"></div>

      <!-- search area -->
      <div class="search-area" :style="searchAreaStyle">
        <el-input
          :style="inputStyle"
          v-model="searchTerm"
          ref="inputRef"
          placeholder="Search location"
          class="search-input"
          size="large"
          :suffix-icon="Search"
        >
        </el-input>
      </div>

      <!-- title bar -->
      <div class="title-bar" :style="titleBarStyle" @click="moveHome">
        <img
          src="../assets/logo/logo_icon.png"
          alt="logo_icon"
          style="width: 20px; margin-right: 10px"
        />
        <img src="../assets/logo/logo_text.png" alt="logo_icon" style="width: 110px" />
      </div>

      <!-- switch map type -->
      <div :style="mapChooseStyle">
        <div style="color: black; cursor: default; margin-right: 20px; margin-left: 20px">
          <b>CHOOSE MAP STYLE</b>
        </div>
        <el-tooltip :content="'change to map: ' + mapStyleSwitch" placement="top">
          <el-switch
            v-model="mapStyleSwitch"
            inline-prompt
            size="large"
            style="
              margin-right: 20px;
              --el-switch-on-color: #305a92;
              --el-switch-off-color: #ff914d;
            "
            active-value="HEATMAP"
            inactive-value="MARKERS"
            active-text="&nbsp&nbsp&nbsp MARKERS &nbsp&nbsp&nbsp  "
            inactive-text=" &nbsp&nbsp&nbsp  HEATMAP  &nbsp&nbsp&nbsp "
            @change="mapStyleChange"
        /></el-tooltip>
      </div>

      <!-- busy level forecast slider -->
      <div style="position: absolute" :style="sliderStyle">
        <el-dropdown
          style="color: black; position: absolute; top: 9px; font-size: 17px; left: 10px"
        >
          {{ todayDate.slice(5) }}
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="date in futureDates"
                :key="date"
                @click="handleDateSelect(date)"
                >{{ date }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-slider
          v-model="forecastTime"
          vertical
          :max="23"
          :format-tooltip="formatTooltip"
          @input="sliderTimeChange"
          :marks="sliderTimeShow"
        />
      </div>

      <!-- open side bar button -->
      <div ref="openSideBarRef">
        <el-button
          color="#305a92"
          :icon="Expand"
          class="open-side-bar-button"
          :style="openSideBarButtonStyle"
          @click="openSideBar"
          size="large"
        ></el-button>
      </div>

      <!-- zoom in/out button -->
      <div class="zoom" :style="zoomStyle">
        <el-button v-show="false"></el-button>
        <el-button color="#305a92" :icon="Plus" @click="zoomIn" circle></el-button>
        <el-button color="#305a92" :icon="Minus" @click="zoomOut" circle></el-button>
      </div>

      <!-- close info button -->
      <transition name="slide-in-left">
        <div v-show="infoWindowShow" :style="infoWindowCloseBtnStyle">
          <el-button
            style="width: 30px; box-shadow: 0px 0px 5px rgba(0, 0, 0, 1)"
            :style="{
              height: isSmallScreen ? '' : '70px',
            }"
            color="#305a92"
            :icon="isSmallScreen ? ArrowDownBold : ArrowRightBold"
            @click="closeInfo"
          ></el-button></div
      ></transition>

      <!-- weather part -->
      <div class="weather-part">
        <el-popover
          :show-arrow="false"
          :trigger="weatherWindowTrigger"
          :placement="weatherWindowPlacement"
          :width="300"
          popper-style="border-radius: 10px;margin-top:100px;background-color: #305a92;box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 20px;"
        >
          <template #reference>
            <div class="current-weather" @click="open">
              <img :src="weatherSrc" />
              <p>
                <b>{{ currTemp }}℃</b>
              </p>
            </div>
          </template>
          <template #default>
            <WeatherPage></WeatherPage>
          </template>
        </el-popover>
      </div>
    </div>

    <!-- mask -->
    <div v-show="showOverlay" class="overlay-map" ref="overlayRef"></div>

    <!-- side bar -->
    <div class="side-bar" ref="sideBarRef" @click="outsideClickFold">
      <transition name="slide-in-right">
        <div v-if="sideBarShow" :style="containerStyle">
          <!-- close button -->
          <el-button
            type="info"
            :icon="CloseBold"
            circle
            size="small"
            class="close-side-bar-button"
            @click="closeSidebar"
          />
          <!-- sign-register component -->
          <router-view v-slot="{ Component }">
            <component :is="Component" class="sign-register" :isSmall="isSmallScreen" />
          </router-view>
        </div>
      </transition>
    </div>

    <!-- info window -->
    <transition name="slide-in-left">
      <div v-if="infoWindowShow" class="info-style">
        <div class="infoWindow" :style="infoStyle">
          <InfoPage
            :isSmall="isSmallScreen"
            :map="map"
            :directionsDisplay="directionsDisplay"
            :infoWindowList="infoWindowList"
          ></InfoPage>
        </div>
      </div>
    </transition>
  </div>
</template>

<style lang="scss">
@import "../utils/color.scss";
:root {
  --screen-height: 100vh;
}

.main-view {
  width: 100vw;
  height: var(--screen-height);
  overflow: hidden;
  font-family: "Franklin Gothic Medium", "Arial Narrow", Arial, sans-serif;

  .welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
  }

  // slide css
  .slide-in-right-enter-active,
  .slide-in-right-leave-active,
  .slide-in-left-enter-active,
  .slide-in-left-leave-active {
    transition: transform 0.5s ease-out;
  }

  .slide-in-right-enter-from,
  .slide-in-right-leave-to {
    transform: translateX(-500px);
  }

  .slide-in-left-enter-from,
  .slide-in-left-leave-to {
    transform: translateX(500px);
  }

  @media (max-width: 600px) {
    .slide-in-left-enter-from,
    .slide-in-left-leave-to {
      transform: translateY(100vh);
    }
  }

  .map-view {
    position: fixed;
    width: 100%;
    overflow-x: hidden;
    .open-side-bar-button {
      position: fixed;
      width: 60px;
      top: 17px;
      left: 10px;
      box-shadow: 0px 0px 5px rgba(0, 0, 0, 1);
      z-index: 2;
    }
    .title-bar {
      background-color: $navy;
      position: fixed;
      box-shadow: 0 0 3px rgba(0, 0, 0, 1);
      top: 0;
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: center;
    }
    .search-area {
      position: absolute;
      display: flex;
      justify-content: center;
      width: 100vw;
      flex-wrap: wrap;
      left: 0;
      transition: left 0.5s ease, width 0.5s ease;
      .search-input {
        border-radius: 4px;
        box-shadow: 0px 0px 8px rgba(0, 0, 0, 1);
      }
    }

    .zoom {
      display: flex;
      align-items: baseline;
      justify-content: center;

      button {
        box-shadow: 0 0 5px rgba(0, 0, 0, 1);
      }

      & > *:last-child {
        margin-top: 10px;
      }
    }

    .weather-part {
      width: auto;
      height: auto;
      // background-color: rgb(0, 0, 0);
      position: fixed;
      top: 0;
      margin-top: 70px;
      margin-left: 10px;

      .current-weather {
        width: 60px;
        height: 80px;
        display: flex;
        align-items: center;
        flex-direction: column;
        justify-content: center;
        border-radius: 5px;
        box-shadow: 0 0 5px rgba(0, 0, 0, 1);
        background-color: $navy;

        &::before {
          content: "";
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          z-index: -1;
        }
        img {
          margin-top: 20px;
          width: 100%;
          filter: saturate(200%);
        }

        p {
          color: rgb(237, 237, 237);
          position: relative;
          top: -20px;
        }
      }
    }

    .el-slider__bar {
      background-color: $navy;
    }
    .el-slider__runway {
      background-color: $orange;
    }

    .el-divider__text {
      background-color: black;
    }
    .el-input__wrapper {
      background-color: rgb(244, 244, 244);
    }
    .el-input__inner {
      background-color: rgb(244, 244, 244);
    }
    .el-step__title.is-process {
      color: #ff914d;
    }
    .el-step__head.is-process {
      color: #ff914d;
      border-color: #ff914d;
    }
  }
  .overlay-map {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: 5;
  }
  .side-bar {
    position: fixed;
    top: 0;
    height: var(--screen-height);
    display: flex;
    justify-items: center;
    z-index: 10;

    .close-side-bar-button {
      position: absolute;
      top: 2%;
      right: 5%;
      z-index: 10;
    }

    .sign-register {
      padding-top: 10vh;
    }
  }

  .infoWindow {
    position: absolute;
    background-color: $navy;
    right: 0%;
    top: 0%;
  }

  .info-style {
    background-color: #305a92;
    position: absolute;
    width: 500px;
    height: var(--screen-height);
    right: 0;
    top: 0;
    overflow-x: hidden;
    overflow-y: auto;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
    border-radius: 5px;
    @media (max-width: 600px) {
      top: 60%;
      width: 100vw;
      height: calc(0.8 * var(--screen-height));
    }
  }
}
</style>
