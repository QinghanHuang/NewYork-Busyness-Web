<template>
  <div style="display: flex; flex-direction: column;  align-items: center">
    <div class="avatar">
      <el-avatar class="avatarImg" :size="100" :src="avatar" />
      <div style="display: flex; flex-direction: column; ">
        <p style="color: black; font-size: 25px; margin-bottom: 0px">{{ username }}</p>
        <p style="color: grey; font-size: 12px;width: 200px;">{{ introduction }}</p>
      </div>
    </div>
    <el-divider />
    <h1>welcome, {{ username }}</h1>
    <p>welcome to use this application</p>
    <div class="func"></div>
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
import { avatarDict } from "../../assets/avatars/avatarDict";

const username = ref("");
const avatar = ref("");
const introduction = ref("");

get("/api/user/me", (res) => {
  avatar.value = avatarDict[res.avatar];
  introduction.value = res.introduction;
  if (res.name) username.value = res.name;
  else username.value = res.username;
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

.func{
  width: 300px;
  height: 330px;
  background-color: rgb(124, 124, 124);
}

.footer {
  position: absolute;
  bottom: 2%;
  right: 4%;
}
</style>
