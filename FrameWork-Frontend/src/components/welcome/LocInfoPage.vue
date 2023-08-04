<template>
  <div :style="widthStyle">
    <el-scrollbar ref="scrollbarRef" :native="false" class="custom-scrollbar" :noresize="true">
      <div class="scrollbar-flex-content">
        <el-image
          v-for="url in urls"
          :key="url"
          :src="url"
          :preview-src-list="urls"
          class="inline-image"
          :style="imgStyle"
        />
      </div>
    </el-scrollbar>
    <div style="margin-left: 20px; color: #e4e4e4; margin-top: -15px">
      <h1>{{ locationName }}</h1>
    </div>
    <div class="open-time" style="margin-top: -20px; margin-left: 20px; color: #e4e4e4">
      {{ openTimeText }}
      <div :style="funcBtnStyle" style="display: flex; flex-direction: row">
        <!-- route popover -->
        <div style="margin-right: 5px">
          <el-popover
            :visible="routeVisible"
            :show-arrow="false"
            trigger="click"
            :width="350"
            popper-style="border-radius: 10px;background-color: #305a92;box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 20px;"
          >
            <div style="height: 150px; width: 100%">
              <el-input
                v-model="origin"
                ref="inputRef"
                placeholder="Search location"
                class="search-input"
              >
                <template #prepend style="font-size: 20px"> Origin </template>
                <template #append>
                  <el-tooltip effect="dark" content="Your Location">
                    <el-button :icon="Location" @click="accquirePlace"
                  /></el-tooltip>
                </template>
              </el-input>
              <el-input
                v-model="destination"
                placeholder="Search location"
                class="search-input"
                ref="inputRefDes"
                ><template #prepend>Destination</template>
                <template #append>
                  <el-tooltip effect="dark" content="This Place">
                    <el-button :icon="Place" @click="destination = locationName"
                  /></el-tooltip>
                </template>
              </el-input>
              <el-button
                style="
                  position: relative;
                  top: 15px;
                  left: 105px;
                  border-radius: 4px;
                  box-shadow: 0px 0px 8px rgba(0, 0, 0, 1);
                "
                color="#305a92"
                @click="showRoute"
                >Show Route</el-button
              >
            </div>
            <div style="text-align: right; margin: 0">
              <el-button size="small" color="#305a92" @click="clearRoute">CLEAR</el-button>
              <el-button
                style="margin-left: 170px"
                size="small"
                color="black"
                text
                @click="routeVisible = false"
                >CANCEL</el-button
              >
            </div>

            <template #reference>
              <div>
                <el-tooltip effect="dark" content="To This Place">
                  <el-button
                    color="#17a25d"
                    :icon="Bicycle"
                    circle
                    size="small"
                    @click="routeVisible = true"
                /></el-tooltip>
              </div>
            </template>
          </el-popover>
        </div>
        <div>
          <el-tooltip effect="dark" content="Add to Itinerary">
            <el-button type="warning" :icon="Star" circle size="small" @click="addItinerary"
          /></el-tooltip>
        </div>
      </div>
    </div>
    <div style="margin-left: 20px; display: flex; flex-direction: row">
      <div style="position: relative; top: 10px; color: #ff914d; font-size: 20px">
        Current Busy Level :
      </div>
      &nbsp&nbsp
      <el-rate
        style="position: relative; top: 4px"
        size="large"
        v-model="value"
        :icons="icons"
        :disabled-void-icon="Minus"
        score-template="{value}"
        disabled
        :colors="['#00c763', '#FF9900', '#dc4d4d']"
      />
    </div>
    <hr />
    <div
      class="introduction"
      style="margin-left: 20px; margin-right: 20px; text-align: justify; font-size: 17px"
    >
      <p style="color: rgb(232, 232, 232); margin-top: 15px">
        {{ locationDes }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { post, get } from "@/net/axios";
import { ref, defineProps, watchEffect, onMounted, computed, onUnmounted } from "vue";
import {
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  Minus,
  BellFilled,
  Star,
  Bicycle,
  OfficeBuilding,
  Location,
  Place,
} from "@element-plus/icons-vue";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";

let data;
const routeVisible = ref(false);
const store = useStore();
const locationName = ref("");
const locationID = computed(() => store.state.locationID);
const locationDes = ref("");
const value = ref();
const urls = ref([]);
const inputRef = ref(null);
const inputRefDes = ref(null);

let yourLocation;
let autoOptions;
let autocomplete;
let autocompleteDes;
let marker;
let directionsService;
let directionsDisplay = props.directionsDisplay;
let infoWindowList = props.infoWindowList;
const icons = [OfficeBuilding, OfficeBuilding, OfficeBuilding];
const props = defineProps({
  isSmall: {
    type: Boolean,
    required: true,
  },
  map: {
    type: Object,
    required: true,
  },
  directionsDisplay: {
    type: Object,
    required: true,
  },
  infoWindowList: {
    type: Object,
    required: true,
  },
});
const openTimeText = ref("");
const scrollbarRef = ref(null);
const origin = ref("");
const destination = ref("");

const addItinerary = () => {
  routeVisible.value = false;

  const predictData = store.state.predictData;
  const itineraryData = store.state.itinerary;
  const lastItinerary = itineraryData[itineraryData.length - 1];

  lastItinerary.showSelection = true;
  lastItinerary.selectedLocation = locationName.value;
  lastItinerary.selectedDate = predictData[0].time.slice(0, 10);
  lastItinerary.selectedTime = predictData[0].time.slice(11, 13) + ":00";
  lastItinerary.busyLevel = value.value;

  itineraryData.push({
    id: lastItinerary.id + 1,
    showSelection: false,
    selectedLocation: null,
    selectedDate: null,
    selectedTime: null,
    busyLevel: 0,
  });

  store.commit("setItinerary", itineraryData);
  store.commit("setSideBarShow", true);
};

const isOpen = (openTime, closeTime) => {
  const now = new Date();
  const options = { timeZone: "America/New_York", hour12: false };
  const currentTime = now.toLocaleTimeString("en-US", options).slice(0, 5);
  const getTimeInMinutes = (timeStr) => {
    const [hour, minute] = timeStr.split(":").map(Number);
    return hour * 60 + minute;
  };

  let openTimeM = getTimeInMinutes(openTime);
  let closeTimeM = getTimeInMinutes(closeTime);
  let currentTimeM = getTimeInMinutes(currentTime);

  if (closeTimeM <= openTimeM) closeTimeM += 1440;

  return currentTimeM >= openTimeM && currentTimeM <= closeTimeM;
};

const getTargetInfo = (ID) => {
  const predictData = computed(() => store.state.predictData).value;
  const item = predictData.find((obj) => obj.pid === ID);
  store.commit("setTargetInfo", item);
  return item;
};

const accquirePlace = () => {
  origin.value = "Your Location";
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const pos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude,
        };
        yourLocation = pos;
        const infoWindow = new google.maps.InfoWindow();
        infoWindow.setPosition(pos);
        infoWindow.setContent("Your Place");
        infoWindow.open(props.map);
        props.map.setCenter(pos);
      },
      () => {
        handleLocationError(true, infoWindow, props.map.getCenter());
      }
    );
  } else {
    // Browser doesn't support Geolocation
    handleLocationError(false, infoWindow, props.map.getCenter());
  }

  const handleLocationError = (browserHasGeolocation, infoWindow, pos) => {
    infoWindow.setPosition(pos);
    infoWindow.setContent(
      browserHasGeolocation
        ? "Error: The Geolocation service failed."
        : "Error: Your browser doesn't support geolocation."
    );
    infoWindow.open(props.map);
  };
};

const clearRoute = () => {
  console.log(directionsDisplay);
  const emptyDirectionsResult = {
    routes: [],
    status: google.maps.DirectionsStatus.OK,
  };
  directionsDisplay.setDirections(emptyDirectionsResult);
  directionsDisplay.setMap(props.map);
  if (!infoWindowList.length) return;
  infoWindowList.forEach((infoWindow) => {
    infoWindow.close();
  });
};

const widthStyle = ref({});
const imgStyle = ref({});
const funcBtnStyle = ref({});

watchEffect(() => {
  widthStyle.value = {
    backgroundColor: "#305a92",
    width: props.isSmall ? "100vw" : "500px",
  };
  imgStyle.value = {
    height: props.isSmall ? "170px" : "",
  };
  funcBtnStyle.value = {
    position: "absolute",
    right: "20px",
    marginTop: "-15px",
  };
  const ID = locationID.value;
  const url = `/api/poi/${ID}`;
  get(url, (res) => {
    data = res;
    locationName.value = data.name;
    destination.value = data.name;
    locationDes.value = data.introduction;
    const busy = getTargetInfo(ID).busy;
    value.value = busy;
    urls.value = data.img;
    openTimeText.value = isOpen(data.openTime.open, data.openTime.close)
      ? `OPEN NOW (${data.openTime.open} - ${data.openTime.close})`
      : `CLOSED (${data.openTime.open} - ${data.openTime.close})`;
  });
});

const closeInfowindow = () => store.commit("setInfoWindowShow", false);

const setupAutocomplete = (inputRef, map, field, string) => {
  const autoOptions = {
    fields: ["formatted_address", "geometry", "name"],
    strictBounds: false,
    types: ["establishment"],
  };

  const autocomplete = new google.maps.places.Autocomplete(
    inputRef.value.$el.querySelector("input"),
    autoOptions
  );

  autocomplete.bindTo("bounds", map);
  autocomplete.addListener("place_changed", () => {
    const place = autocomplete.getPlace();
    if (place.geometry.viewport) {
      map.fitBounds(place.geometry.viewport);
      map.setZoom(15);
    } else {
      map.setCenter(place.geometry.location);
      map.setZoom(15);
    }
    const infoWindow = new google.maps.InfoWindow();
    infoWindow.setPosition(place.geometry.location);
    infoWindow.setContent(string + " Place");
    infoWindow.open(props.map);
    infoWindowList.push(infoWindow);
    field.value = place.name;
  });

  return autocomplete;
};

const showRoute = () => {
  routeVisible.value = false;
  const request = {
    origin: origin.value === "Your Location" ? yourLocation : origin.value,
    destination: destination.value,
    travelMode: google.maps.TravelMode.WALKING, // You can change this to WALKING or TRANSIT
  };

  directionsService.route(request, function (result, status) {
    if (status == google.maps.DirectionsStatus.OK) {
      directionsDisplay.setDirections(result);
      fitMapBounds(result);
      closeInfowindow();
    } else {
      ElMessage("Can not find the route.");
    }
  });
};

const fitMapBounds = (result) => {
  var bounds = new google.maps.LatLngBounds();
  var legs = result.routes[0].legs;
  for (var i = 0; i < legs.length; i++) {
    var steps = legs[i].steps;
    for (var j = 0; j < steps.length; j++) {
      var path = steps[j].path;
      for (var k = 0; k < path.length; k++) {
        bounds.extend(path[k]);
      }
    }
  }
  props.map.fitBounds(bounds);
};

onMounted(() => {
  marker = new google.maps.Marker({});
  autocomplete = setupAutocomplete(inputRef, props.map, origin, "Origin");
  autocompleteDes = setupAutocomplete(inputRefDes, props.map, destination, "Destination");

  directionsService = new google.maps.DirectionsService();
  directionsDisplay.setMap(props.map);
});
</script>

<style lang="scss">
.scrollbar-flex-content {
  display: flex;
  min-width: 200%;
  .inline-image {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: auto;
    height: 250px;
    margin: 10px;
    border-radius: 4px;
    background: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }
}

.el-rate__text {
  color: #ff914d;
}

.search-input {
  margin-top: 10px;
  border-radius: 4px;
  box-shadow: 0px 0px 8px rgba(0, 0, 0, 1);
}
.el-input-group__prepend {
  font-family: "Franklin Gothic Medium", "Arial Narrow", Arial, sans-serif;
  background-color: #ff8030;
  color: white;
  padding: 0 10px;
}
.pac-container {
  z-index: 10000;
}
</style>
