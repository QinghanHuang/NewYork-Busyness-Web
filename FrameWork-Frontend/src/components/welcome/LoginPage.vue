<script setup>
import { Lock, User } from "@element-plus/icons-vue";
import { reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { post, get } from "@/net/axios";
import router from "@/router";
import { useStore } from "vuex";
import "../../utils/color.scss";

onMounted(() => {
  if (document.cookie) {
    const userName = document.cookie.match(/[^=\s]+=[^;]+/)[0].split("=")[1];
    form.username = userName;
    form.remember = true;
  }
});

const store = useStore();

// record user info
let userInfo;

const form = reactive({
  username: "",
  password: "",
  remember: false,
});

const setCookieExpiration = (days) => {
  const now = new Date();
  const expirationDate = new Date(now.getTime() + days * 24 * 60 * 60 * 1000);
  return expirationDate.toUTCString();
};

const login = () => {
  if (!form.username || !form.password) {
    ElMessage.warning("Please Input Username and Password ");
  }
  store.commit("setUserName", form.username);
  post(
    "/api/auth/login",
    {
      username: form.username,
      password: form.password,
      remember: form.remember,
    },
    (message) => {
      ElMessage.success(message);
      get("/api/user/me", (message) => {
        userInfo = {
          name: message.name,
          birthday: message.birthday,
          introduction: message.introduction,
          gender: message.gender,
          username: message.username,
          email: message.email,
          phoneNumber: message.phoneNumber,
          avatar: message.avatar,
        };
        store.commit("setUserInfo", userInfo);
        router.push("/func");
      });
      if (form.remember) {
        const username = form.username;
        const expirationDate = setCookieExpiration(3);
        document.cookie = `username=${username}; expires=${expirationDate}; path=/`;
      } else document.cookie = "username=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      router.push("/func");
    }
  );
};
</script>

<template>
  <div
    style="
      text-align: center;
      margin: 0 30px;
      position: relative;
      top: -5vh;
      background-color: #305a92;
    "
  >
    <div style="margin-top: -20px">
      <img
        src="../../assets/logo/logo_icon.png"
        alt="ucd"
        style="width: 40px; padding-bottom: 10px"
      />&nbsp
      <img
        src="../../assets/logo/logo_text.png"
        alt="ucd"
        style="width: 130px; padding-bottom: 20px"
      />
    </div>
    <hr style="margin-bottom: 20px" />
    <div style="font-size: 35px; font-weight: bold; color: #ff914d">Sign In</div>
    <div style="font-size: 14px; color: rgb(244, 244, 244)">
      Please use a username or email to sign in
    </div>
    <div style="margin-top: 30px" class="big-screen">
      <el-input v-model="form.username" type="text" placeholder="Username/Email">
        <template #prefix>
          <el-icon>
            <User />
          </el-icon>
        </template>
      </el-input>
      <el-input
        v-model="form.password"
        type="password"
        style="margin-top: 10px"
        placeholder="Password"
      >
        <template #prefix>
          <el-icon>
            <Lock />
          </el-icon>
        </template>
      </el-input>
    </div>
    <el-row style="margin-top: 5px" class="big-screen">
      <el-col :span="12" style="text-align: left">
        <el-checkbox style="color: #e4e4e4" v-model="form.remember" label="Remember Me" />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click.stop="router.push('/forget')" type="info" style="color: rgb(244, 244, 244)"
          >Forgot Password?</el-link
        >
      </el-col>
    </el-row>
    <div style="margin-top: 40px">
      <el-button
        @click="login"
        style="width: 270px; box-shadow: 0 0 3px rgba(255, 255, 255, 0.5)"
        color="#0b4795"
        plain
        >Sign In</el-button
      >
    </div>
    <el-divider>
      <span style="background-color: #305a92; color: rgb(255, 255, 255); font-size: 13px"
        >No Account</span
      >
    </el-divider>
    <div>
      <el-button
        style="width: 270px; box-shadow: 0 0 3px rgba(255, 255, 255, 0.5)"
        @click.stop="router.push('/register')"
        color="#FF914d"
        plain
        >Sign Up</el-button
      >
    </div>
  </div>
</template>

<style lang="scss">
@import "../../utils/color.scss";
.big-screen {
  @media (min-width: 600px) {
    width: 80%;
    margin-left: 10%;
  }
}

.el-divider__text {
  background-color: $navy;
}
.el-input__wrapper {
  background-color: rgb(244, 244, 244);
}
.el-input__inner {
  background-color: rgb(244, 244, 244);
}
</style>
