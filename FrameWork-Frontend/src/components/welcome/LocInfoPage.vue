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
    <div class="open-time" style="margin-top: -20px; margin-left: 20px; color: #00b2b5">
      {{ openTimeText }}
      <div :style="funcBtnStyle">
        <el-tooltip effect="dark" content="add to itinerary">
          <el-button type="warning" :icon="Star" circle size="small" @click="addItinerary"
        /></el-tooltip>
      </div>
    </div>
    <div style="margin-left: 20px; display: flex; flex-direction: row">
      <div style="position: relative; top: 6px; color: #ff914d; font-size: 20px">
        Current Busy Level :
      </div>
      &nbsp&nbsp
      <el-rate
        size="large"
        v-model="value"
        :icons="icons"
        :disabled-void-icon="Minus"
        show-score
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
import { ref, defineProps, watchEffect, onMounted, computed } from "vue";
import {
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  Minus,
  BellFilled,
  Star,
} from "@element-plus/icons-vue";
import { useStore } from "vuex";

let data;
const store = useStore();
const locationName = ref("");
const locationID = computed(() => store.state.locationID);
const locationDes = ref("");
const value = ref();
const urls = ref([]);
const icons = [SuccessFilled, WarningFilled, CircleCloseFilled];
const props = defineProps({
  isSmall: {
    type: Boolean,
    required: true,
  },
});
const openTimeText = ref("");
const scrollbarRef = ref(null);

const addItinerary = () => {
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
    locationDes.value = data.introduction;
    const busy = getTargetInfo(ID).busy;
    value.value = busy;
    urls.value = data.img;
    openTimeText.value = isOpen(data.openTime.open, data.openTime.close)
      ? `OPEN NOW (${data.openTime.open} - ${data.openTime.close})`
      : `CLOSED (${data.openTime.open} - ${data.openTime.close})`;
  });
});

const clear = () => store.commit("setInfoWindowShow", false);

onMounted(() => {});
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
</style>
