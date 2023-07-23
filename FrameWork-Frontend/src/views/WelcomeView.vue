<script setup>
import {
  CloseBold,
  Search,
  Expand,
  Minus,
  Plus,
  DArrowRight,
  Close,
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
const colorDict = {
  1: "green",
  2: "blue",
  3: "yellow",
  4: "orange",
  5: "red",
};

watchEffect(() => {
  const isSmall = isSmallScreen.value;
  weatherWindowTrigger.value = isSmall ? "click" : "hover";
  weatherWindowPlacement.value = isSmall ? "right" : "right";

  searchAreaStyle.value = {
    left: infoWindowShow.value ? (isSmall ? "" : "-20%") : "",
    top: isSmall ? "2%" : "2.5%",
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
    bottom: isSmall ? (infoWindowShow.value ? "40vh" : "5px") : "20px",
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
    bottom: isSmall ? (infoWindowShow.value ? "47vh" : "70px") : "80px",
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
    backgroundColor: "white",
    height: "100%",
    zIndex: 1,
    width: isSmall ? "330px" : "500px",
  };
  inputStyle.value = {
    position: "relative",
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
    height: isSmall ? "40vh" : "100vh",
    borderRadius: isSmall ? "5px" : "",
  };
});

// **********************methods***********************
// ----------------------loader----------------------
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
  // Get the current date and time
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
  color: "#1989FA",
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
  for (let i = 0; i <= 5; i++) {
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
    ElMessage.warning("login to use this function");
    return true;
  }
  return false;
};

// ----------------------slider function----------------------
const sliderTimeChange = () => {
  const targetTime =
    forecastTime.value < 10
      ? `${todayDate.value}-0${forecastTime.value}`
      : `${todayDate.value}-${forecastTime.value}`;
  const tempData = computed(() => store.state.poiData).value;
  const filteredData = tempData.filter((data) => data.time === targetTime);

  markerList.forEach((marker, index) => {
    const busyLevel = filteredData[index].busy;
    const color = colorDict[busyLevel];
    const newIcon = {
      path: "M 0 12 C 5 12 5 4 0 4 C -5 4 -5 12 0 12 z M 0 0 q 2.906 0 4.945 2.039 t 2.039 4.945 q 0 1.453 -0.727 3.328 t -1.758 3.516 t -2.039 3.07 t -1.711 2.273 l -0.75 0.797 q -0.281 -0.328 -0.75 -0.867 t -1.688 -2.156 t -2.133 -3.141 t -1.664 -3.445 t -0.75 -3.375 q 0 -2.906 2.039 -4.945 t 4.945 -2.039 z",
      fillColor: color,
      fillOpacity: 1,
      strokeWeight: 1,
      rotation: 0,
      scale: 2,
      anchor: new google.maps.Point(0, 20),
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

  loading.close()

  const adviseDict = {
    1: "Enjoy the tranquility and serenity.",
    2: "Experience the charm and beauty of this popular destination without the crowds.",
    3: "Discover the vibrant atmosphere and cultural attractions of this bustling hotspot.",
    4: "Embrace the energy and excitement of this lively destination, but be prepared for crowds.",
    5: "Explore the iconic landmarks and must-see attractions of this bustling metropolis, but be ready for large crowds and queues.",
  };

  if (!dataList) return;
  const locationInfo = computed(() => store.state.poiInfo).value;
  dataList.forEach((data) => {
    if (data.time !== currTime) return;
    const ID = data.pid;
    const busyLevel = data.busy;
    const color = colorDict[busyLevel];
    const customMarker = new window.google.maps.Marker({
      position: locationInfo[ID - 1].location,
      animation: google.maps.Animation.DROP,
      title: locationInfo[ID - 1].name,
      map,
      icon: {
        path: "M 0 12 C 5 12 5 4 0 4 C -5 4 -5 12 0 12 z M 0 0 q 2.906 0 4.945 2.039 t 2.039 4.945 q 0 1.453 -0.727 3.328 t -1.758 3.516 t -2.039 3.07 t -1.711 2.273 l -0.75 0.797 q -0.281 -0.328 -0.75 -0.867 t -1.688 -2.156 t -2.133 -3.141 t -1.664 -3.445 t -0.75 -3.375 q 0 -2.906 2.039 -4.945 t 4.945 -2.039 z",
        fillColor: color,
        fillOpacity: 1,
        strokeWeight: 1,
        rotation: 0,
        scale: 2,
        anchor: new google.maps.Point(0, 20),
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
    const contentString = `<div style="position: relative; top: -15px; width:200px;height:90px;">
    <h3>${locationInfo[ID - 1].name}</h3>

        <p>${adviseDict[data.busy]}</p></div>

    `;
    customMarker.addListener("click", () => {
      mapInfoWindow.setContent(contentString);
      mapInfoWindow.open(map, customMarker);

      map.panTo(locationInfo[ID - 1].location);
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
    // gradient: ["rgba(0, 255, 0, 0)", "rgba(255, 0, 0, 1)"],
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

watchEffect(async () => {
  // watch the login auth of user
  const userAuth = computed(() => store.state.auth);
  if (userAuth.value) {
    setWeather();
    // store poi and taxi zone info
    await get("/api/poi/all", (res) => store.commit("setPoiInfo", res));
    await setMarkers(currTime, todayDate);
    setHeatMap(currTime, todayDate);
    setWeather();
  }
});

// life circle functions
onMounted(() => {
  // test
  get("/api/weather/forecast", (res) => console.log("/api/weather/forecast", res));

  // adjust side bar size
  handleWindowResize();
  window.addEventListener("resize", handleWindowResize);

  // side bar outside click
  document.addEventListener("click", outsideClickFold);

  // side bar effect
  store.commit("setSideBarShow", true);
  showOverlay.value = true;

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
      <div id="map" style="width: 100vw; height: 100vh"></div>
      <!-- search area -->
      <div class="search-area" :style="searchAreaStyle">
        <el-input
          :style="inputStyle"
          v-model="searchTerm"
          ref="inputRef"
          placeholder="Search location"
          class="search-input"
          size="large"
        >
          <template #append>
            <el-button style="width: 70px" :icon="Search" @click="search" /></template
        ></el-input>
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
              --el-switch-on-color: #003c55;
              --el-switch-off-color: #d11e3b;
            "
            active-value="HEATMAP"
            inactive-value="MARKERS"
            active-text="&nbsp&nbsp&nbsp MAEKERS &nbsp&nbsp&nbsp  "
            inactive-text=" &nbsp&nbsp&nbsp  HEATMAP  &nbsp&nbsp&nbsp "
            @change="mapStyleChange"
        /></el-tooltip>
      </div>

      <!-- busy level forecast slider -->
      <div style="position: absolute" :style="sliderStyle">
        <el-dropdown style="position: absolute; top: 5px; font-size: 15px; left: 10px">
          {{ todayDate }}
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
          type="warning"
          :icon="Expand"
          class="open-side-bar-button"
          @click="openSideBar"
          size="large"
        ></el-button>
      </div>

      <!-- zoom in/out button -->
      <div class="zoom" :style="zoomStyle">
        <el-button v-show="false"></el-button>
        <el-button type="success" :icon="Plus" @click="zoomIn" circle></el-button>
        <el-button type="success" :icon="Minus" @click="zoomOut" circle></el-button>
      </div>

      <!-- close info button -->
      <div style="position: absolute; bottom: 350px; left: 87vw">
        <el-button
          style="box-shadow: 0px 0px 5px rgba(0, 0, 0, 1)"
          v-show="infoWindowShow && isSmallScreen"
          :icon="Close"
          @click="closeInfo"
          circle
          size="large"
        ></el-button>
      </div>

      <!-- weather part -->
      <div class="weather-part" v-show="computed(() => store.state.auth).value">
        <el-popover
          :show-arrow="false"
          :trigger="weatherWindowTrigger"
          :placement="weatherWindowPlacement"
          :width="300"
          popper-style="border-radius: 10px;margin-top:100px;background-color: rgb(14, 116, 226);box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 20px;"
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
    <!-- <div v-show="false" class="overlay-map"></div> -->
    <div v-show="showOverlay" class="overlay-map" ref="overlayRef"></div>
    <!-- side bar position: absolute-->
    <div class="side-bar" ref="sideBarRef" @click="outsideClickFold">
      <transition name="slide-in-right">
        <!-- <div v-if="false" :style="containerStyle"> -->
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
            <component :is="Component" class="sign-register" />
          </router-view>
        </div>
      </transition>
    </div>
    <!-- info window -->
    <transition name="slide-in-left">
      <div v-if="infoWindowShow" class="info-style">
        <div class="infoWindow" :style="infoStyle">
          <InfoPage :isSmall="isSmallScreen"></InfoPage>
        </div>
      </div>
    </transition>
  </div>
</template>

<style lang="scss">
.main-view {
  width: 100vw;
  height: 100vh;
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
    .search-area {
      position: absolute;
      z-index: 1;
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
        background-color: rgb(14, 116, 226);

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
      background-color: #000000;
    }
    .el-slider__runway {
      background-color: red;
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
    position: absolute;
    top: 0;
    height: 100vh;
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
    background-color: rgb(255, 255, 255);
    right: 0%;
    top: 0%;
  }

  .info-style {
    background-color: rgb(255, 255, 255);
    position: absolute;
    width: 500px;
    height: 100vh;
    right: 0;
    top: 0;
    overflow-x: hidden;
    overflow-y: auto;
    box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
    border-radius: 5px;
    @media (max-width: 600px) {
      top: 60%;
      width: 100vw;
      height: 75vh;
    }
  }
}
</style>
