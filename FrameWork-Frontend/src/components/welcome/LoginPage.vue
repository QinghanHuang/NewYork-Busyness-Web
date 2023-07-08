<script setup>
import { Lock, User } from "@element-plus/icons-vue";
import { reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { post, get } from "@/net/axios";
import router from "@/router";
import { useStore } from "vuex";

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
        console.log(message);
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
  <div style="text-align: center; margin: 0 20px">
    <div style="font-size: 25px; font-weight: bold">Sign In</div>
    <div style="font-size: 14px; color: grey">Please use a username or email to sign in</div>

    <div style="margin-top: 50px">
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
    <el-row style="margin-top: 5px">
      <el-col :span="12" style="text-align: left">
        <el-checkbox v-model="form.remember" label="Rememver Me" />
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click.stop="router.push('/forget')">Forgot Password?</el-link>
      </el-col>
    </el-row>
    <div style="margin-top: 40px">
      <el-button @click="login" style="width: 270px" type="success" plain>Sign In</el-button>
    </div>
    <el-divider>
      <span style="color: grey; font-size: 13px">No Account</span>
    </el-divider>
    <div>
      <el-button style="width: 270px" @click.stop="router.push('/register')" type="warning" plain
        >Sign Up</el-button
      >
    </div>
  </div>
</template>

<style scoped></style>
