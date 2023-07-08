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
import { ref, defineProps, watchEffect, onMounted, computed } from "vue";
import { SuccessFilled, WarningFilled, CircleCloseFilled, Minus } from "@element-plus/icons-vue";
import { useStore } from "vuex";

let data ;
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
const widthStyle = ref({});
const imgStyle = ref({});

watchEffect(() => {
  widthStyle.value = {
    backgroundColor: "white",
    width: props.isSmall ? "100vw" : "500px",
  };
  imgStyle.value = {
    height: props.isSmall ? "170px" : "",
  };

  const ID = locationID.value;
  if (ID === "01") {
    data = {
      id: "01",
      name: "central park",
      detailedLocation: "New York, NY, United States",
      openTime: {
        open: "6:00",
        close: "1:00",
      },
      busy: 5,
      img: [
        "https://upload.wikimedia.org/wikipedia/commons/1/13/Central_Park_-_The_Pond_%2848377220157%29.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/26_-_New_York_-_Octobre_2008.jpg/1280px-26_-_New_York_-_Octobre_2008.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/f/f9/Centralpark_fg01.jpg",
      ],
      introduction:
        "Sprawling park with pedestrian paths & ballfields, plus a zoo, carousel, boat rentals & a reservoir.",
    };
  } else if (ID === "02") {
    data = {
      id: "02",
      name: "time square",
      detailedLocation: "199 W 45th St, New York, NY 10036, United States",
      openTime: {
        open: "11:00",
        close: "20:00",
      },
      busy: 3,
      img: [
        "https://upload.wikimedia.org/wikipedia/commons/4/47/New_york_times_square-terabass.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/c/c0/1_times_square_night_2013.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/6/6d/Times_Square_--_February_1965.jpg",
      ],
      introduction:
        "Times Square is a major commercial intersection, tourist destination, and entertainment hub in New York City, United States.",
    };
  }
  locationName.value = data.name;
  value.value = data.busy;
  urls.value = data.img;
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
