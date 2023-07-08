<template>
  <div style="display: flex; flex-direction: column; justify-content: center; align-items: center">
    <h1>welcome, {{ username }}</h1>
    <p>welcome to use this application</p>
    <div class="footer">
      <el-button @click="logout()" type="danger" plain style="width: 80px">Sign Out</el-button>
      <el-button @click="setting()" type="info" plain style="width: 100px">Edit Profile</el-button>
    </div>
  </div>
</template>

<script setup>
import { get } from "@/net/axios";
import { ref } from "vue";
import router from "@/router";
import { ElMessage } from "element-plus";

const username = ref("");

get("/api/user/me", (message) => {
  console.log(message.name)
  if(message.name) username.value = message.name
  else username.value = message.username
});

const logout = () => {
  get("/api/auth/logout", (message) => {
    ElMessage.success(message);
    router.push("/");
  });
};
const setting = () => {
  router.push("/set");
};
</script>

<style lang="scss" scoped>
.footer {
  position: absolute;
  bottom: 2%;
  right: 4%;
}
</style>
