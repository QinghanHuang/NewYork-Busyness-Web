<template>
  <div :style="widthStyle">
    <el-scrollbar>
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
    <hr />
    <div style="margin-left: 20px">
      <h1>{{ locationName }}</h1>
    </div>
    <div class="open-time" style="margin-left: 20px; color: #00989a">{{ openTimeText }}</div>
    <div style="margin-left: 20px; display: flex; flex-direction: row">
      <div style="position: relative; top: 6px; color: rgb(123, 123, 123)">
        busy situation:&nbsp&nbsp
      </div>
      <el-rate
        v-model="value"
        :icons="icons"
        :disabled-void-icon="Minus"
        show-score
        score-template="{value}"
        disabled
        :colors="['#409eff', '#FF9900', 'red']"
      />
    </div>
    <hr />
    <div
      class="introduction"
      style="margin-left: 20px; margin-right: 20px; text-align: justify; font-size: 18px"
    >
      <p>
        &nbsp&nbsp&nbsp&nbsp&nbsp&nbspCentral Park, located in the heart of Manhattan, is a
        sprawling urban oasis covering 843 acres. Designed by Frederick Law Olmsted and Calvert
        Vaux, it offers a serene escape from the city's hustle and bustle. With its scenic
        landscapes, meandering pathways, and iconic landmarks like Bethesda Terrace and the
        Jacqueline Kennedy Onassis Reservoir, Central Park is a beloved destination for leisurely
        walks, picnics, and outdoor activities. Its rich history, cultural institutions, and natural
        beauty make it a cherished symbol of New York City's vibrant spirit.
      </p>
    </div>
    <div v-if="!isSmall" @click="clear()" class="close">------click Here to close------</div>
  </div>
</template>

<script setup>
import { post, get } from "@/net/axios";
import { ref, defineProps, watchEffect, onMounted, computed } from "vue";
import { SuccessFilled, WarningFilled, CircleCloseFilled, Minus } from "@element-plus/icons-vue";
import { useStore } from "vuex";

let data;
const locationName = ref("");
const locationID = computed(() => store.state.locationID);
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

const isOpen = (openTime, closeTime) => {
  const now = new Date();
  const options = { timeZone: "America/New_York", hour12: false };
  const currentTime = now.toLocaleTimeString("en-US", options).slice(0, 5); // 获取当前时间的小时和分钟部分（格式：HH:mm）
  const getTimeInMinutes = (timeStr) => {

    const [hour, minute] = timeStr.split(":").map(Number);
    return hour * 60 + minute;
  };

  let openTimeM = getTimeInMinutes(openTime);
  let closeTimeM = getTimeInMinutes(closeTime);
  let currentTimeM = getTimeInMinutes(currentTime);

  if (closeTimeM < openTimeM) closeTimeM += 1440;

  return currentTimeM >= openTimeM && currentTimeM <= closeTimeM;
};

watchEffect(() => {
  widthStyle.value = {
    backgroundColor: "white",
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
    value.value = data.busy;
    urls.value = data.img;
    openTimeText.value = isOpen(data.openTime.open, data.openTime.close)
      ? `OPEN NOW (${data.openTime.open} - ${data.openTime.close})`
      : `CLOSED (${data.openTime.open} - ${data.openTime.close})`;
  });
});

const clear = () => store.commit("setInfoWindowShow", false);
</script>

<style lang="scss" scoped>
.scrollbar-flex-content {
  display: flex;
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
  color: rgb(172, 172, 172);
  text-align: center;
  width: 100%;

  &:hover {
    color: #00989a;
    cursor: pointer;
  }
}
</style>
