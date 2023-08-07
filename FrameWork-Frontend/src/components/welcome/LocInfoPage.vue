<template>
  <el-scrollbar :native="isSmall"  :noresize="false">
    <div :style="widthStyle">
      <!-- image -->
      <el-scrollbar>
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
      <!-- lcoation name -->
      <div style="margin-left: 20px; color: #e4e4e4; margin-top: -15px">
        <h1>{{ locationName }}</h1>
      </div>
      <!-- opentime  and -->
      <div class="open-time" style="margin-top: -20px; margin-left: 20px; color: #e4e4e4">
        {{ openTimeText }}
      </div>
      <!-- function btn -->
      <div :style="funcBtnStyle" style="display: flex; flex-direction: row">
        <!-- echarts -->
        <div style="margin-right: 5px">
          <el-tooltip effect="dark" content="Show Statics">
            <el-button
              class="add-shadow"
              color="#0c70b2"
              :icon="DataLine"
              circle
              size="small"
              @click="dialogStaticVisible = true"
          /></el-tooltip>
          <el-dialog
            v-model="dialogStaticVisible"
            style="
              background-color: #305a92;
              border-radius: 10px;
              box-shadow: 0 0 5px rgba(0, 0, 0, 1);
            "
            :style="{ width: isSmall ? '95%' : '' }"
            @open="echartsShow = true"
            @close="echartsShow = false"
          >
            <h2 style="color: whitesmoke; position: absolute; top: 0%">Statistical Graph</h2>
            <div
              style="
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
              "
            >
              <v-charts
                v-if="echartsShow"
                :option="chartOptions"
                style="width: 100%; height: 200px; overflow: hidden; border-radius: 10px"
              ></v-charts>
              <hr style="width: 100%" />
              <div
                style="
                  width: 100%;
                  height: 208px;
                  overflow: hidden;
                  border-radius: 10px;
                  background-color: #305a92;
                  margin-top: 0px;
                "
              >
                <v-charts
                  v-if="echartsShow"
                  :option="chartOptions2"
                  style="width: 100%; height: 210px; overflow: hidden; border-radius: 10px"
                ></v-charts>
              </div>
            </div>
          </el-dialog>
        </div>
        <!-- route popover -->
        <div style="margin-right: 5px">
          <el-popover
            :visible="routeVisible"
            :show-arrow="false"
            trigger="click"
            :width="350"
            popper-style="border-radius: 10px;background-color: #305a92;box-shadow: rgb(14 18 22 / 35%) 0px 10px 38px -10px, rgb(14 18 22 / 20%) 0px 10px 20px -15px; padding: 20px;"
          >
            <div style="height: 100px; width: 100%">
              <el-input
                v-model="origin"
                ref="inputRef"
                placeholder="Search location"
                class="search-input"
              >
                <template #prepend>Origin</template>
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
                ><template #prepend>&nbspDest.</template>
                <template #append>
                  <el-tooltip effect="dark" content="Switch">
                    <el-button :icon="Sort" @click="switchPlace"
                  /></el-tooltip>
                </template>
              </el-input>
              <el-button
                style="
                  position: relative;
                  top: 10px;
                  left: 100px;
                  border-radius: 4px;
                  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.5);
                  --el-button-text-color: white;
                "
                color="#ff8030"
                @click="showRoute"
                >Show Route</el-button
              >
            </div>
            <div style="text-align: right; margin: 0; height: 30px">
              <el-button
                style="position: absolute; left: 10px"
                size="small"
                color="#305a92"
                @click="clearRoute"
                >CLEAR</el-button
              >
              <el-button
                style="position: absolute; right: 10px"
                size="small"
                color="#305a92"
                @click="routeVisible = false"
                >CANCEL</el-button
              >
            </div>

            <template #reference>
              <div>
                <el-tooltip effect="dark" content="To This Place">
                  <el-button
                    class="add-shadow"
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
        <!-- add itinerary -->
        <div>
          <el-tooltip effect="dark" content="Add to Itinerary">
            <el-button
              class="add-shadow"
              type="warning"
              :icon="Star"
              circle
              size="small"
              @click="addItinerary"
          /></el-tooltip>
        </div>
      </div>
      <!-- busy level -->
      <div style="margin-left: 20px; display: flex; flex-direction: row">
        <div style="position: relative; top: 10px; color: #ff914d; font-size: 20px">
          Busy Level :
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
      <!-- introduction -->
      <div
        class="introduction"
        style="margin-left: 20px; margin-right: 20px; text-align: justify; font-size: 17px"
      >
        <p style="color: rgb(232, 232, 232); margin-top: 15px">
          {{ locationDes }}
        </p>
      </div>
      <!-- graph -->
      <div style="width: 100%; display: flex; align-items: center; justify-content: center">
        <v-charts
          :option="chartOptions"
          style="width: 80%; height: 200px; overflow: hidden; border-radius: 10px"
        ></v-charts>
      </div>
      <!-- mask -->
      <div
        v-show="routeVisible"
        style="
          position: absolute;
          top: 0;
          right: 0;
          width: 100%;
          background-color: rgba(0, 0, 0, 0.5);
        "
        :style="{ height: isSmall ? '100%' : '100%' }"
      ></div>
    </div>
  </el-scrollbar>
</template>

<script setup>
import { post, get } from "@/net/axios";
import { ref, defineProps, watchEffect, onMounted, computed, onUnmounted } from "vue";
import {
  Minus,
  Star,
  Bicycle,
  OfficeBuilding,
  Location,
  Place,
  DataLine,
  Sort,
} from "@element-plus/icons-vue";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
import VCharts from "vue-echarts";
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { GridComponent } from "echarts/components";
import { LineChart } from "echarts/charts";
import { TitleComponent, TooltipComponent } from "echarts/components";
import {
  DataZoomComponent,
  DataZoomInsideComponent,
  DataZoomSliderComponent,
  DatasetComponent,
} from "echarts/components";
import { BarChart } from "echarts/charts";
import * as echarts from "echarts/core";

use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  DataZoomComponent,
  BarChart,
  DataZoomInsideComponent,
  DataZoomSliderComponent,
  DatasetComponent,
]);

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
const overlayShow = ref(false);
const dialogStaticVisible = ref(false);
const echartsShow = ref(false);

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

// **********************echarts**********************
const chartOptions = ref({
  backgroundColor: "#305a92",
  borderRadius: "10px",
  title: {
    textStyle: {
      fontSize: 14,
      color: "#e5e5e5",
    },
    left: "center",
  },
  tooltip: {
    trigger: "axis",
    axisPointer: {
      lineStyle: {
        color: "#e5e5e5",
      },
    },
  },
  grid: {
    left: "3%",
    right: "4%",
    bottom: "3%",
    top: "70px",
    containLabel: true,
  },
  xAxis: [
    {
      type: "category",
      boundaryGap: false,
      axisLine: {
        lineStyle: {
          color: "#e5e5e5",
        },
      },
      axisLabel: {
        margin: 15,
        fontSize: 10,
        rotate: -45,
      },
      data: ["00:00"],
    },
  ],
  yAxis: [
    {
      type: "value",
      name: "Busy Level",
      nameTextStyle: {
        align: "left",
      },
      min: 0,
      max: 5,
      minInterval: 1,
      axisTick: {
        show: false,
      },
      axisLine: {
        lineStyle: {
          color: "#e5e5e5",
        },
      },
      axisLabel: {
        margin: 5,
        fontSize: 11,
      },
      splitLine: {
        lineStyle: {
          color: "#57617B",
        },
      },
    },
  ],
  series: [
    {
      type: "line",
      smooth: true,
      symbol: "circle",
      symbolSize: 5,
      showSymbol: false,
      lineStyle: {
        width: 1,
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(
          0,
          0,
          0,
          1,
          [
            {
              offset: 0,
              color: "rgba(255, 145, 77, 0.3)",
            },
            {
              offset: 0.8,
              color: "rgba(255, 145, 77, 0)",
            },
          ],
          false
        ),
        shadowColor: "rgba(0, 0, 0, 0.1)",
        shadowBlur: 10,
      },
      itemStyle: {
        color: "rgb(255, 145, 77)",
        borderColor: "rgba(255, 145, 77,0.27)",
        borderWidth: 12,
      },
      data: [220, 182, 191, 134, 150, 120, 110, 125, 145, 122, 165, 122],
    },
  ],
});

const chartOptions2 = ref({
  title: {
    text: "Busy Level of All POI",
    textStyle: {
      fontSize: 14,
      color: "#e5e5e5",
    },
    left: "center",
  },
  tooltip: {
    trigger: "axis",
    axisPointer: {
      lineStyle: {
        color: "#e5e5e5",
      },
    },
    confine: true,
  },
  grid: {
    bottom: "auto",
    width: "93%",
    left: "3%",
    containLabel: true,
    transformOrigin: "center center",
  },
  xAxis: [
    {
      type: "category",
      axisPointer: {
        type: "shadow",
      },
      axisLine: {
        lineStyle: {
          color: "#e5e5e5",
        },
      },
      axisLabel: {
        show: true,
        interval: 0,
        rotate: -45,
        color: "#e5e5e5",
        formatter: function (value) {
          if (value.length > 10) return value.substring(0, 10) + "...";
          return value;
        },
      },
    },
  ],
  yAxis: [
    {
      type: "value",
      name: "Busy Level",
      nameTextStyle: {
        align: "left",
      },
      min: 0,
      axisLabel: {
        formatter: "{value}",
      },
      axisLine: {
        lineStyle: {
          color: "#e5e5e5",
        },
      },
      splitLine: {
        lineStyle: {
          color: "#57617B",
        },
      },
      minInterval: 1,
    },
  ],
  dataset: {
    source: [
      {
        name: "1",
        number: 5,
      },
    ],
  },
  dataZoom: [
    {
      type: "slider",
      show: true,
      backgroundColor: "#F6F5F3",
      fillerColor: "#BBC4D3",
      borderColor: "rgba(19, 63, 100, 0)",
      borderRadius: 10,
      showDetail: false,
      startValue: 0,
      endValue: props.isSmall ? 10 : 20,
      filterMode: "empty",
      width: "100%",
      height: 8,
      left: "center",
      zoomLoxk: true,
      handleSize: 0,
      bottom: 0,
    },
    {
      type: "inside",
      zoomOnMouseWheel: false,
      moveOnMouseMove: true,
      moveOnMouseWheel: true,
    },
  ],
  series: [
    {
      itemStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            {
              offset: 1,
              color: "#fff2ea",
            },
            {
              offset: 0,
              color: "#d07841",
            },
          ],
        },
      },
      type: "bar",
      color: "#5087ec",
      barMaxWidth: 10,
      barGap: "200%",
    },
  ],
});
//***************************************************

const switchPlace = () => {
  const temp = origin.value;
  origin.value = destination.value;
  destination.value = temp;
};
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

const setDataAllPoiGraph = () => {
  const poiListData = computed(() => store.state.predictData).value;
  const poiDataAll = computed(() => store.state.poiInfo).value;
  const dataSet = [];
  for (let i = 0; i < poiListData.length; i++) {
    dataSet.push({
      name: poiDataAll[i].name,
      value: poiListData[i].busy,
    });
  }
  // dataSet.sort((a, b) => (a.name > b.name ? 1 : -1));
  dataSet.sort((a, b) => (a.value < b.value ? 1 : -1));
  chartOptions2.value.dataset.source = dataSet;
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
    position:'relative',
    backgroundColor: "#305a92",
    width: props.isSmall ? "100vw" : "500px",
  };
  imgStyle.value = {
    height: props.isSmall ? "170px" : "",
    objectFit: "cover",
  };
  funcBtnStyle.value = {
    position: "absolute",
    right: "20px",
    marginTop: "-15px",
    left:props.isSmall ? "75%" : "80%",
  };
  const ID = locationID.value;
  const url = `/api/poi/${ID}`;

  const allData = computed(() => store.state.poiData).value;
  const dateOfAdayList = allData.filter((item) => item.pid === ID).map((item) => item.busy);
  chartOptions.value.series[0].data = dateOfAdayList;
  get(url, (res) => {
    data = res;
    locationName.value = data.name;
    destination.value = data.name;
    chartOptions.value.title.text = `Busy Level of a Day`;
    destination.value = data.name;
    locationDes.value = data.introduction;
    setDataAllPoiGraph();
    const busy = getTargetInfo(ID).busy;
    value.value = busy;
    data.img.reverse();
    urls.value = data.img;
    openTimeText.value = isOpen(data.openTime.open, data.openTime.close)
      ? `OPEN NOW (${data.openTime.open} - ${data.openTime.close})`
      : `CLOSED (${data.openTime.open} - ${data.openTime.close})`;
  });
});

watchEffect(() => {
  const trackValue = computed(() => store.state.sideBarShow).value;
  if (trackValue) routeVisible.value = false;
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
    destination: destination.value === "Your Location" ? yourLocation : destination.value,
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

  //set echarts options
  const timeList = [];
  for (let i = 0; i < 24; i++) {
    const hour = i.toString().padStart(2, "0");
    timeList.push(`${hour}:00`);
  }

  chartOptions.value.xAxis[0].data = timeList;
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
    width: 300px;
    height: 250px;
    margin: 10px;
    border-radius: 4px;
    background: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
    object-fit: cover;
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

.add-shadow {
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.5);
}

</style>
