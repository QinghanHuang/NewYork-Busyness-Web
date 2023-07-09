<script setup>
import { CloseBold, Expand, Minus, Plus, DArrowRight } from "@element-plus/icons-vue";
import { ref, onMounted, onUnmounted, watchEffect, Transition, computed } from "vue";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import { get } from "@/net/axios";
import { throttle, debounce } from "lodash";
import InfoPage from "../components/welcome/LocInfoPage.vue";


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
let data;
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
let isPlain;

// style edit
const inputStyle = ref({});
const buttonStyle = ref({});
const containerStyle = ref({});
const infoStyle = ref({});
const zoomStyle = ref({});
const typeChooseStyle = ref({});
const mapChooseStyle = ref({});
const searchAreaStyle = ref({});

watchEffect(() => {
  const isSmall = isSmallScreen.value;
  searchAreaStyle.value = {
    left: infoWindowShow.value ? (isSmall ? "" : "-20%") : "",
    top: isSmall ? "2%" : "2.5%",
    transition: "left 0.4s",
  };
  mapChooseStyle.value = {
    display: "flex",
    flexWrap: "wrap",
    width: isSmall ? "10vw" : "",
    alignItems: isSmall ? "left" : "center",
    justifyContent: isSmall ? "left" : "center",
    position: "absolute",
    bottom: isSmall ? (infoWindowShow.value ? "40vh" : "5px") : "20px",
    left: isSmall ? "5px" : "20px",
  };
  zoomStyle.value = {
    flexDirection: isSmall ? "column" : "row",
    position: "absolute",
    bottom: isSmall ? "43%" : "12%",
    left: isSmall ? "85%" : "1%",
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
    width: isSmall ? "75vw" : "300px",
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

// methods
// map relative
const zoomIn = () => map.setZoom(map.getZoom() + 1);
const zoomOut = () => map.setZoom(map.getZoom() - 1);

const search = () => {
  geocode({
    address: searchTerm.value.includes(",")
      ? searchTerm.value.match(/^[^,]+/)?.[0].toLowerCase()
      : searchTerm.value.toLowerCase(),
  });
};

const clear = () => {
  mapInfoWindow.close();
  store.commit("setInfoWindowShow", false);
  marker.setMap(null);
  searchTerm.value = "";
};

const getIdByName = (name) => {
  const foundItem = dataList.find((item) => item.name === name);
  return foundItem ? foundItem.id : null;
};

const geocode = (request) => {
  if (!request.address) return;

  mapInfoWindow.close();
  store.commit("setInfoWindowShow", false);

  geocoder
    .geocode(request)
    .then((result) => {
      const { results } = result;
      map.setCenter(results[0].geometry.location);
      marker.setPosition(results[0].geometry.location);
      marker.setMap(map);
      searchTerm.value = results[0].formatted_address.includes(",")
        ? results[0].formatted_address.match(/^[^,]+/)?.[0].toLowerCase()
        : results[0].formatted_address.toLowerCase();

      locaitonID = getIdByName(searchTerm.value);
      if (locaitonID) showLocInfo(locaitonID);

      return results;
    })
    .catch((e) => {
      ElMessage.warning("Geocode was not successful for the following reason: " + e);
    });
};

// set marker
const setMarkers = async () => {
  // get data
  await get("/api/poi", (message) => {
    dataList = message;
  });

  const colorDict = {
    1: "green",
    2: "blue",
    3: "yellow",
    4: "orange",
    5: "red",
  };

  const adviseDict = {
    1: "Enjoy the tranquility and serenity.",
    2: "Experience the charm and beauty of this popular destination without the crowds.",
    3: "Discover the vibrant atmosphere and cultural attractions of this bustling hotspot.",
    4: "Embrace the energy and excitement of this lively destination, but be prepared for crowds.",
    5: "Explore the iconic landmarks and must-see attractions of this bustling metropolis, but be ready for large crowds and queues.",
  };

  dataList.forEach((data) => {
    const busyLevel = data.busy;
    const color = colorDict[busyLevel];
    const customMarker = new window.google.maps.Marker({
      position: data.location,
      animation: google.maps.Animation.DROP,
      title: data.name,
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
    <h3>${data.name}</h3>

        <p>${adviseDict[data.busy]}</p></div>

    `;
    customMarker.addListener("click", () => {
      mapInfoWindow.setContent(contentString);
      mapInfoWindow.open(map, customMarker);
      map.panTo(data.location);
      showLocInfo(data.id);
    });

    markerList.push(customMarker);
  });
};

// set heatMap
const setHeatMap = async () => {
  await get("/api/poi", (message) => {
    dataList = message;
  });

  const tempData = [];

  dataList.forEach((data) => {
    tempData.push({
      location: new google.maps.LatLng(data.location.lat, data.location.lng),
      weight: data.busy,
    });
  });

  heatMapObj = new google.maps.visualization.HeatmapLayer({
    data: tempData,
    radius: 15,
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
  markerList.forEach((marker) => {
    marker.setVisible(true);
    marker.setAnimation(google.maps.Animation.DROP);
  });
};

// disappear heatmap
const disappearHeatmap = () => heatMapObj.setMap(null);

// show heatmap
const showHeatmap = () => heatMapObj.setMap(map);

// show poi location info
const showLocInfo = (id) => {
  store.commit("setLocationID", id);
  store.commit("setInfoWindowShow", true);
  if (!isPlain) marker.setMap(null);
};

// map type choose
const mapTypePlain = () => {
  disappearHeatmap();
  disappearMarkers();
  isPlain = true;
};

const mapTypeMarker = () => {
  disappearHeatmap();
  showMarkers();
  isPlain = false;
};

const mapTypeHeat = () => {
  showHeatmap();
  disappearMarkers();
  isPlain = false;
};

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

// life circle functions
onMounted(() => {
  get("/api/weather/forecast", (res) => {
    console.log(res);
  });

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
  // map marker geocoder autocomplete
  map = new google.maps.Map(document.getElementById("map"), mapOptions);
  marker = new google.maps.Marker({
    map,
  });

  geocoder = new google.maps.Geocoder();
  autocomplete = new google.maps.places.Autocomplete(
    inputRef.value.$el.querySelector("input"),
    autoOptions
  );
  mapInfoWindow = new google.maps.InfoWindow();

  // autocomplete
  autocomplete.bindTo("bounds", map);
  autocomplete.addListener("place_changed", () => {
    marker.setVisible(false);

    const place = autocomplete.getPlace();

    if (!place.geometry || !place.geometry.location) {
      search();
      return;
    }

    // If the place has a geometry, then present it on a map.
    if (place.geometry.viewport) {
      map.fitBounds(place.geometry.viewport);
    } else {
      map.setCenter(place.geometry.location);
      map.setZoom(17);
    }
    marker.setPosition(place.geometry.location);
    marker.setVisible(true);

    searchTerm.value = place.name.toLowerCase();
    locaitonID = getIdByName(searchTerm.value);
    if (locaitonID) showLocInfo(locaitonID);
    else marker.setMap(map);
  });

  // set marker list
  setMarkers();
  setHeatMap();
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
          ></el-input>
          <el-button
            :style="buttonStyle"
            @click="search"
            type="primary"
            size="large"
            class="search-button"
            >Search</el-button
          >
          <el-button
            :style="buttonStyle"
            @click="clear"
            type="success"
            size="large"
            class="clear-button"
            >Clear</el-button
          >
        </div>

      <!-- switch map type -->
      <div :style="mapChooseStyle">
        <el-button
          plain
          disabled
          type="info"
          size="small"
          style="
            color: black;
            background-color: white;
            box-shadow: 0 0 5px rgba(0, 0, 0, 0.5);
            cursor: default;
            margin-right: 20px;
          "
          >MAP STYLE</el-button
        >
        <el-button
          :style="typeChooseStyle"
          @click="mapTypePlain"
          type="info"
          :size="isSmallScreen ? 'default' : 'large'"
          >Plain</el-button
        >
        <el-button
          :style="typeChooseStyle"
          @click="mapTypeMarker"
          type="info"
          :size="isSmallScreen ? 'default' : 'large'"
          >Markers</el-button
        >
        <el-button
          :style="typeChooseStyle"
          @click="mapTypeHeat"
          type="info"
          :size="isSmallScreen ? 'default' : 'large'"
          >Heatmap</el-button
        >
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
    </div>
    <!-- <div v-show="false" class="overlay-map"></div> -->
    <div v-show="showOverlay" class="overlay-map" ref="overlayRef"></div>
    <!-- side bar position: absolute-->
    <div class="side-bar" ref="sideBarRef" @click="outsideClickFold">
      <transition name="slide-in-right">
        <div v-if="sideBarShow" :style="containerStyle">
          <!-- <div v-if="false" :style="containerStyle"> -->
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

<style lang="scss" scoped>
.main-view {
  width: 100vw;
  height: 100vh;
  overflow: hidden;

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
      left:0;
      transition: left 0.3s ease;
      .search-input {
        border-radius: 4px;
        box-shadow: 0px 0px 8px rgba(0, 0, 0, 1);
      }

      .search-button {
        margin-left: 10px;
        box-shadow: 0px 0px 8px rgba(0, 0, 0, 1);
      }

      .clear-button {
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
        margin-top: 10px; /* 添加上边距 */
      }
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
    background-color: white;
    right: 0%;
    top: 0%;
  }

  .info-style {
    background-color: white;
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
