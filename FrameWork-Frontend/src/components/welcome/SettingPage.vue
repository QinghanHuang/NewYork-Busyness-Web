<template>
  <div class="this">
    <div class="title" style="position: absolute; top: 1%; margin-left: 2%; color: #343434">
      <h1 style="color: #ff914d">Edit Profile</h1>
    </div>
    <div style="margin-top: 5%">
      <div class="avatar">
        <el-avatar
          @click="chooseAvatar"
          class="avatarImg"
          :size="100"
          :src="avatarDict[form.avatar]"
        />
        <p class="upload-text" @click="chooseAvatar">Click here choose your avatar</p>
      </div>
      <el-divider />
      <el-form :model="form" label-width="100px" class="form">
        <el-form-item label="Email">
          <el-input disabled v-model="form.email" class="add-shadow" />
        </el-form-item>
        <el-form-item label="User Name">
          <el-input disabled v-model="form.username" class="add-shadow" />
        </el-form-item>
        <el-form-item label="Nick Name">
          <el-input v-model="form.name" class="add-shadow" />
        </el-form-item>
        <el-form-item label="Phone Num">
          <el-input v-model="form.phoneNumber" class="add-shadow" />
        </el-form-item>
        <el-form-item label="Gender">
          <el-select v-model="form.gender" class="add-shadow">
            <el-option label="male" value="0" />
            <el-option label="female" value="1" />
            <el-option label="others" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="Birthday">
          <el-col :span="17">
            <el-date-picker
              v-model="form.birthday"
              type="date"
              style="width: 100%"
              class="add-shadow"
            />
          </el-col>
        </el-form-item>

        <el-form-item label="Introduction">
          <el-input v-model="form.introduction" type="textarea" class="add-shadow" />
        </el-form-item>
        <el-form-item>
          <el-button color="#FF914d" @click="onSubmit">Save</el-button>
          <el-button type="info" @click="back">Cancel</el-button>
        </el-form-item>
      </el-form>
      <div class="choose-avatar" v-if="chooseAvatarShow">
        <div class="avatar-container">
          <img
            v-for="image in images"
            :key="image.id"
            :src="image.url"
            :class="{ 'image-item': true, selected: image.id === selectedImageId }"
            @click="selectImage(image.id)"
          />
        </div>
        <el-button
          style="position: relative; left: 50%; transform: translate(-50%, 50%)"
          @click="getSelectedImageId"
          color="#FF914d"
          round
          size="large"
          >Choose this!</el-button
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted, computed, ref } from "vue";
import { useStore } from "vuex";
import router from "@/router";
import { get, post, postJson } from "@/net/axios";
import { ElMessage } from "element-plus";
import { Message, CloseBold, Expand, Minus, Plus } from "@element-plus/icons-vue";
import { avatarDict } from "../../assets/avatars/avatarDict";

const store = useStore();
let userID;

// do not use same name with ref
const form = reactive({
  name: "",
  birthday: "",
  introduction: "",
  gender: "",
  username: "",
  email: "",
  phoneNumber: "",
  avatar: "0",
});

const images = ref(
  Object.entries(avatarDict)
    .slice(1)
    .map(([id, url]) => ({ id: Number(id), url }))
);
const selectedImageId = ref(null);
const chooseAvatarShow = ref(false);

onMounted(() => {
  const screenHeight = window.innerHeight;
  document.documentElement.style.setProperty("--screen-height", `${screenHeight}px`);

  get("/api/user/me", (message) => {
    form.name = message.name;
    form.birthday = message.birthday;
    form.introduction = message.introduction;
    form.gender = message.gender;
    form.username = message.username;
    form.email = message.email;
    form.phoneNumber = message.phoneNumber;
    form.avatar = message.avatar;
    userID = message.id;

    store.commit("setAuth", true);
  });
});

const onSubmit = () => {
  postJson(
    "/api/user/me",
    {
      name: form.name,
      birthday: form.birthday,
      introduction: form.introduction,
      gender: form.gender,
      username: form.username,
      email: form.email,
      phoneNumber: form.phoneNumber,
      avatar: form.avatar,
      id: userID,
    },
    (message) => {
      ElMessage.success(message);
      router.push("/func");
    },
    (message) => console.log(message)
  );
};

const back = () => router.push("/func");

const selectImage = (imageId) => {
  selectedImageId.value = imageId;
};

const getSelectedImageId = () => {
  if (selectedImageId.value !== null) {
    form.avatar = selectedImageId.value;
    chooseAvatarShow.value = false;
  } else {
    console.log("No image selected.");
  }
};

const chooseAvatar = () => (chooseAvatarShow.value = true);
</script>

<style lang="scss">
:root {
  --screen-height: 100vh;
}

.this {
  font-family: "Franklin Gothic Medium", "Arial Narrow", Arial, sans-serif;
  position: relative;
  top: -3%;

  .el-form-item__label {
    color: #ebebeb;
  }

  .form {
    margin-right: 15%;
    margin-left: 4%;

    @media (max-width: 600px) {
      margin-top: 50px;
    }
  }

  .avatar {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin-left: 20px;
    position: relative;
    left: 7%;
    bottom: 2%;

    .avatarImg {
      // box-shadow: 1px 2px 5px rgba(0, 0, 0, 1);
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

      &:hover {
        cursor: pointer;
      }
    }

    p {
      font-size: larger;
      color: #a9a9a9;
      margin-left: 20px;
      width: 30%;

      &:hover {
        color: #ff914d;
        text-decoration: underline;
        cursor: pointer;
      }
    }
  }

  .add-shadow {
    border-radius: 4px;
    box-shadow: 1px 2px 5px rgba(0, 0, 0, 0.3);
  }

  .choose-avatar {
    position: absolute;
    top: 3%;
    height: var(--screen-height);
    background-color: #305a92;

    .avatar-container {
      padding-top: 15%;
      width: 100%;
      height: calc(0.8 * var(--screen-height));
      overflow: hidden;
      display: flex;
      flex-wrap: wrap;
      justify-content: center;

      .image-item {
        width: 150px;
        height: 150px;
        object-fit: cover;
        object-position: center center;
        margin-left: 5px;

        margin-right: 5px;
        box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);
      }

      .selected {
        border: 2px solid #ff914d;
        width: 147px;
        height: 146px;
      }
    }
  }
}
</style>
