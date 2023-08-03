<template>
  <div :style="widthStyle">
    <el-scrollbar ref="scrollbarRef" :native="false" class="custom-scrollbar" :noresize="true">
      <div class="scrollbar-flex-content">
        <el-image
          v-for="url in urls"
          :key="url"
          :src="url"
          class="inline-image"
          :style="imgStyle"
        />
      </div>
    </el-scrollbar>
    <div style="display: flex; flex-direction: row">
      <div style="margin-left: 20px; color: #e4e4e4; margin-top: -15px">
        <h1>{{ locationName }}</h1>
      </div>
      <div class="open-time" style="margin-top: 22px; margin-left: 10px; color: #00b2b5">
        {{ openTimeText }}
      </div>
    </div>

    <div style="margin-top:-20px;margin-left: 20px; position: relative; top: 6px; color: #ff914d; font-size: 18px">
      Current Busy Level :
    </div>
    &nbsp&nbsp
    <el-rate
    style="margin-left: 5px;"
      size="large"
      v-model="value"
      :icons="icons"
      :disabled-void-icon="Minus"
      show-score
      score-template="{value}"
      disabled
      :colors="['#00c763', '#FF9900', '#dc4d4d']"
    />

    <hr />
    <div
      class="introduction"
      style="margin-left: 20px; margin-right: 20px; text-align: justify; font-size: 17px"
    >
      <p style="color: rgb(232, 232, 232); margin-top: 15px">
        &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp{{ locationDes }}
      </p>
    </div>
    <div v-if="!isSmall" @click="clear()" class="close">------click Here to close------</div>
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
} from "@element-plus/icons-vue";
import { useStore } from "vuex";

let data;
const locationName = ref("");
const locationID = computed(() => store.state.locationID);
const locationDes = ref("");
const value = ref();
const urls = ref([]);
const store = useStore();
const icons = [SuccessFilled, WarningFilled, CircleCloseFilled];
const props = defineProps({
  isSmall: {
    type: Boolean,
    required: true,
  },
});
const openTimeText = ref("");
const widthStyle = ref({});
const imgStyle = ref({});
const scrollbarRef = ref(null);

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

const getBusyByTimeAndPid = (data, time, pid) => {
  const dataByTime = {};
  for (const key in data) {
    const item = data[key];
    if (!dataByTime[item.time]) {
      dataByTime[item.time] = {};
    }
    dataByTime[item.time][item.pid] = item.busy;
  }

  if (dataByTime[time] && dataByTime[time][pid] !== undefined) {
    return dataByTime[time][pid];
  } else {
    return null;
  }
};

watchEffect(() => {
  widthStyle.value = {
    backgroundColor: "#305a92",
    width: props.isSmall ? "100vw" : "500px",
  };
  imgStyle.value = {
    height: props.isSmall ? "170px" : "",
  };

  const ID = locationID.value;
  const url = `/api/poi/${ID}`;
  get(url, (res) => {
    data = res;
    locationName.value = data.name;
    locationDes.value = data.introduction;
    const busy = computed(() => store.state.busy).value;
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

.close {
  position: absolute;
  bottom: 2%;
  color: #ff914d;
  text-align: center;
  width: 100%;

  &:hover {
    color: #00989a;
    cursor: pointer;
    // background-color: rgb(38, 52, 67),
  }
}

.el-rate__text {
  color: #ff914d;
}
</style>
