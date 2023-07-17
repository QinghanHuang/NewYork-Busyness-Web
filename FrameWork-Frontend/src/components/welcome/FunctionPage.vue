<template>
  <div
    style="
      display: flex;
      flex-direction: column;
      align-items: center;
      font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
    "
  >
    <div class="avatar">
      <el-avatar class="avatarImg" :size="100" :src="avatar" />
      <div style="display: flex; flex-direction: column">
        <p style="color: black; font-size: 25px; margin-bottom: 0px">{{ username }}</p>
        <p style="color: grey; font-size: 12px; width: 200px">{{ introduction }}</p>
      </div>
    </div>
    <el-divider />
    <div style="position: absolute; left: 10px; top: 100px; display: flex; flex-direction: row">
      <h2>Itinerary</h2>
      <p style="color: gray; position: relative; top: 5px; margin-left: 20px">
        choose where you want to go and when
      </p>
    </div>
    <div class="func">
      <el-scrollbar>
        <div class="container">
          <div class="column" v-for="column in columns" :key="column.id">
            <el-button
              @click="toggleSelection(column)"
              :icon="column.showSelection ? Minus : Plus"
              circle
              size="small"
              style="margin-left: 8px; box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.7)"
            />
            <transition name="el-zoom-in-center">
              <div v-if="column.showSelection">
                <el-date-picker
                  size="small"
                  class="selection"
                  v-model="column.selectedDate"
                  type="date"
                  placeholder="Pick a day"
                  style="width: 100px"
                />
                <el-time-select
                  size="small"
                  class="selection"
                  v-model="column.selectedTime"
                  start="00:00"
                  step="00:30"
                  end="23:00"
                  placeholder="time"
                  style="width: 90px"
                />
                <el-select
                  v-model="column.selectedLocation"
                  placeholder="Select Location"
                  class="selection"
                  style="width: 100px"
                  size="small"
                >
                  <el-option
                    v-for="location in locations"
                    :key="location"
                    :label="location"
                    :value="location"
                  />
                </el-select>
                <el-rate
                  v-model="column.busyLevel"
                  :icons="icons"
                  :disabled-void-icon="Minus"
                  show-score
                  score-template="{value}"
                  disabled
                  :colors="['#409eff', '#FF9900', 'red']"
                  style="width: 50px; position: relative; top: 3px; left:8px"
                  size="small"
                />
              </div>
            </transition>
          </div></div
      ></el-scrollbar>
    </div>
    <div class="footer">
      <el-button @click="logout()" type="danger" plain style="width: 80px">Sign Out</el-button>
      <el-button @click="setting()" type="info" plain style="width: 100px">Edit Profile</el-button>
    </div>
  </div>
</template>

<script setup>
import { useStore } from "vuex";
import { get } from "@/net/axios";
import { ref, computed, onMounted, watchEffect } from "vue";
import router from "@/router";
import { ElMessage } from "element-plus";
import { avatarDict } from "../../assets/avatars/avatarDict";
import {
  Minus,
  Plus,
  SuccessFilled,
  WarningFilled,
  CircleCloseFilled,
} from "@element-plus/icons-vue";

const store = useStore()
const username = ref("");
const avatar = ref("");
const introduction = ref("");
const columns = ref([
  {
    id: 1,
    showSelection: false,
    selectedLocation: null,
    selectedDate: null,
    selectedTime: null,
    busyLevel: 1,
  },
]);

const icons = [SuccessFilled, WarningFilled, CircleCloseFilled];
let locations

get("/api/user/me", (res) => {
  avatar.value = avatarDict[res.avatar];
  introduction.value = res.introduction;
  if (res.name) username.value = res.name;
  else username.value = res.username;
  store.commit('setAuth', true)
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
    store.commit('setAuth', false)
    router.push("/");
  });
};

const setting = () => {
  router.push("/set");
};

const toggleSelection = (column) => {
  if(isLoginFail()) return
  locations = computed(()=> store.state.poiList).value.map((item) => item.name)
  column.showSelection = !column.showSelection;
  if (column.showSelection) {
    columns.value.push({
      id: column.id + 1,
      showSelection: false,
      selectedLocation: null,
      selectedDate: null,
      selectedTime: null,
      busyLevel: 5,
    });
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
  margin-left: 20px;
  position: absolute;
  left: 1%;
  top: 0%;

  .avatarImg {
    img {
      width: 150px;
      height: 150px;
      object-fit: cover;
      object-position: center center;
    }

    @media (max-width: 600px) {
      width: 100px;
      height: 100px;
    }
  }

  p {
    color: #a9a9a9;
    margin-left: 20px;
    width: 30%;
  }
}

.func {
  overflow-x: hidden;
  width: 96%;
  height: 60vh;
  margin-top: 35px;
  border-radius: 5px;
  background-color: rgb(235, 235, 235);

  .container {
    overflow-x: hidden;
    padding-bottom: 10px;
    display: flex;
    flex-direction: column;

    .column {
      display: flex;
      flex-direction: row;
      align-items: center;
      margin-right: 20px;
      margin-top: 20px;
    }

    .selection {
      margin-left: 8px;
      box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.7);
      border-radius: 4px;
    }
  }
}

.footer {
  position: absolute;
  bottom: 2%;
  right: 4%;
}
</style>
