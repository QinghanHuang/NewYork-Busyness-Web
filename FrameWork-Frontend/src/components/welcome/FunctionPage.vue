<template>
  <div
    style="
      display: flex;
      flex-direction: column;
      align-items: center;
      font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
    "
  >
    <div @click="setting">
      <div style="position: absolute; left: 15px; top: 10px; border-radius: 5px; padding: 5px">
        <img
          src="../../assets/logo/logo_icon.png"
          alt="logo_icon"
          style="width: 25px; margin-right: 10px"
        />
        <img src="../../assets/logo/logo_text.png" alt="logo_icon" style="width: 120px" />
      </div>
      <div class="avatar">
        <el-avatar class="avatarImg" :size="70" :src="avatar" />
        <div style="display: flex; flex-direction: column">
          <p style="color: rgb(255, 255, 255); font-size: 25px; margin-bottom: 0px">
            {{ username }}
          </p>
          <p style="color: rgb(241, 241, 241); font-size: 12px; width: 200px">{{ introduction }}</p>
        </div>
      </div>
    </div>
    <el-divider style="width: 95%; position: absolute;top:70px" />
    <div
      style="
        color: #ff914d;
        position: absolute;
        left: 10px;
        top: 92px;
        display: flex;
        flex-direction: row;
      "
    >
      <h1 class="itinerary">Itinerary</h1>
      <p class="instruction">Choose Location & Time</p>
    </div>
    <div class="func">
      <el-scrollbar>
        <div class="container">
          <div class="column" v-for="column in columns" :key="column.id">
            <el-button
              @click="toggleSelection(column)"
              :icon="column.showSelection ? Minus : Plus"
              color="#FF914d"
              circle
              size="small"
              style="margin-left: 8px; box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.7)"
            />
            <transition name="el-zoom-in-center">
              <div v-if="column.showSelection">
                <el-date-picker
                  :clearable="false"
                  :editable="false"
                  size="small"
                  class="selection"
                  v-model="column.selectedDate"
                  type="date"
                  placeholder="Pick a Day"
                  format="YYYY/MM/DD"
                  value-format="YYYY-MM-DD"
                />
                <el-time-select
                  :clearable="false"
                  :editable="false"
                  size="small"
                  class="selection"
                  v-model="column.selectedTime"
                  start="00:00"
                  step="01:00"
                  end="23:00"
                  placeholder="Time"
                />

                <el-select
                  :clearable="false"
                  v-model="column.selectedLocation"
                  placeholder="Select a Location"
                  class="selection-location"
                  size="small"
                >
                  <el-option
                    v-for="location in locations"
                    :key="location"
                    :label="location"
                    :value="location"
                  />
                </el-select>
                <span style="margin-left: 10px; color: #ff914d">Busy Level:</span>
                <el-rate
                  v-model="column.busyLevel"
                  size="large"
                  :icons="icons"
                  score-template="{value}"
                  disabled
                  disabled-void-color="#305a92"
                  :colors="['#00c763', '#FF9900', '#dc4d4d']"
                  style="width: 50px; position: relative; top: 3px; left: 8px"
                />
              </div>
            </transition>
          </div></div
      ></el-scrollbar>
    </div>
    <div class="footer">
      <el-button @click="logout()" color="#FF914d" plain style="width: 80px">Sign Out</el-button>
      <el-button @click="setting()" type="info" plain style="width: 100px">Edit Profile</el-button>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { get } from "@/net/axios";
import { ref, computed, onMounted, watchEffect, watch } from "vue";
import router from "@/router";
import { ElMessage, ElLoading } from "element-plus";
import { avatarDict } from "../../assets/avatars/avatarDict";
import {
  Minus,
  Plus,
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
  OfficeBuilding,
  Van,
} from "@element-plus/icons-vue";

const store = useStore();
const username = ref("");
const avatar = ref("");
const introduction = ref("");
const props = defineProps({
  isSmall: {
    type: Boolean,
    required: true,
  },
});
const columns = ref([
  {
    id: 1,
    showSelection: false,
    selectedLocation: null,
    selectedDate: null,
    selectedTime: null,
    busyLevel: 0,
  },
]);

// const icons = [SuccessFilled, WarningFilled, CircleCloseFilled];
const icons = [OfficeBuilding, OfficeBuilding, OfficeBuilding];
let locations;

const getIdByName = (name) => {
  const dataList = computed(() => store.state.poiInfo).value;
  const foundItem = dataList.find((item) => item.name === name);
  return foundItem ? foundItem.id : null;
};

const executeFunction = (column) => {
  if (
    column.selectedDate !== null &&
    column.selectedTime !== null &&
    column.selectedLocation !== null
  ) {
    const predictTime = column.selectedDate + "-" + column.selectedTime.slice(0, 2);
    get(`/api/prediction/poi/${getIdByName(column.selectedLocation)}/${predictTime}`, (res) => {
      column.busyLevel = res.busy;
      store.commit("setItinerary", columns.value);
    });
  }
};

const addWatch = (column) => {
  watchEffect(() => {
    executeFunction(column);
  });
};

const addLocationList = () => {
  locations = computed(() => store.state.poiInfo).value.map((item) => item.name);
  locations.sort();
};

onMounted(() => {
  get("/api/user/me", (res) => {
    avatar.value = avatarDict[res.avatar];
    introduction.value = res.introduction;
    if (res.name) username.value = res.name;
    else username.value = res.username;
    store.commit("setAuth", true);
  });

  // get the itinerary data
  const itineraryData = computed(() => store.state.itinerary).value;
  if (itineraryData.length > 0) columns.value = itineraryData;

  addLocationList();

  columns.value.forEach((column, index) => {
    if (index < columns.value.length - 1)
      watchEffect(() => {
        executeFunction(column);
      });
  });
});

// login auth fail
const isLoginFail = () => {
  if (!computed(() => store.state.auth).value) {
    ElMessage.warning("login to use this function");
    return true;
  }
  return false;
};

const logout = () => {
  get("/api/auth/logout", (message) => {
    ElMessage.success(message);
    store.commit("setAuth", false);
    router.push("/");
  });
};

const setting = () => {
  router.push("/set");
};

const toggleSelection = (column) => {
  if (isLoginFail()) return;
  addLocationList();
  column.showSelection = !column.showSelection;
  if (column.showSelection) {
    columns.value.push({
      id: column.id + 1,
      showSelection: false,
      selectedLocation: null,
      selectedDate: null,
      selectedTime: null,
      busyLevel: 0,
    });
    addWatch(column);
  } else {
    const index = columns.value.indexOf(column);
    if (index > -1) {
      columns.value.splice(index, 1);
    }
  }
};
</script>

<style lang="scss">
.avatar {
  display: flex;
  flex-direction: row;
  align-items: center;
  margin-top: 2px;
  margin-left: 210px;
  position: absolute;
  left: 1%;
  top: 0%;
  @media (max-width: 600px) {
    flex-direction: column;
    transform: scale(0.7);
    margin-left: 150px;
    margin-top: -15px;
  }

  .avatarImg {
    img {
      width: 100px;
      height: 100px;
      object-fit: cover;
      object-position: center center;
    }

    @media (max-width: 600px) {
      margin-top: 5px;
      width: 80px;
      height: 80px;
      margin-left: -65px;
    }
  }

  p {
    color: #a9a9a9;
    margin-left: 20px;
    width: 30%;

    @media (max-width: 600px) {
      margin-top: 0px;
    }
  }
}
.itinerary {
  position: relative;
  top: -10px;
  left: 2px;

  @media (max-width: 600px) {
    top: 10px;
  }
}
.instruction {
  color: rgb(238, 238, 238);
  position: relative;
  top: 10px;
  margin-left: 20px;

  @media (max-width: 600px) {
    top: 35px;
    margin-left: 20px;
  }
}
.func {
  overflow-x: hidden;
  position: absolute;

  width: 94%;
  height: 60vh;
  margin-top: 80px;
  border-radius: 5px;
  background-color: #305a92;

  .container {
    overflow-x: hidden;
    padding-bottom: 10px;
    display: flex;
    flex-direction: column;

    .column {
      display: flex;
      flex-direction: row;
      align-items: center;
      margin-top: 20px;
    }

    .selection {
      margin-left: 8px;
      box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.7);
      border-radius: 4px;
      width: 200px;

      @media (max-width: 600px) {
        width: 125px;
      }
    }

    .selection-location {
      margin-left: 8px;
      box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.7);
      border-radius: 4px;
      margin-top: 8px;
      width: 408px;

      @media (max-width: 600px) {
        width: 258px;
      }
    }

    .el-rate__text {
      color: #305a92;
    }
  }
}

.footer {
  position: absolute;
  bottom: 2%;
  right: 4%;
}
</style>
