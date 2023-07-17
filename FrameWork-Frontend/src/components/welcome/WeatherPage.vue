<template>
  <div class="this">
    <div class="curr-weather">
      <img :src="imgSrcCur" />
      <p class="temp-curr">{{ tempCur }}°</p>
      <div class="curr-date">
        <p class="date-curr">{{ dateCur }}</p>
        <p class="des-curr">{{ desCur }}</p>
      </div>
    </div>
    <div class="future-weather">
      <div v-for="column in columns" style="width: 95%">
        <div class="each-day">
          <p style="font-size: 20px">{{ column.date }}</p>
          <p style="font-size: 18px; border-radius: 5px; font-style: italic;">{{ column.dayOfWeek }}</p>
          <p style="font-size: 20px">{{ column.temp }}°</p>
          <img
            style="position: relative; top: 12px; width: 45px"
            :src="`https://openweathermap.org/img/wn/${weatherIconDict[column.weatherId]}.png`"
          />
          <p style="font-size: 18px">{{ column.weatherMain }}</p>
        </div>

        <hr style="margin-top: -20px" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { weatherIconDict } from "../../assets/weather/weatherIconDict.js";
import { onMounted, ref } from "vue";
import { get } from "@/net/axios";
const weatherData = ref(null);
const imgSrcCur = ref("");
const tempCur = ref("");
const dateCur = ref("");
const desCur = ref("");
const columns = ref(null);
const imgSrcFur = ref("");

onMounted(() => {
  get("/api/weather/forecast", (res) => {
    console.log(res);
    weatherData.value = res;
    console.log();
    console.log(weatherIconDict[weatherData.value[0].weatherId]);
    imgSrcCur.value = `https://openweathermap.org/img/wn/${
      weatherIconDict[weatherData.value[0].weatherId]
    }@4x.png`;
    tempCur.value = Math.round(weatherData.value[0].temp);
    dateCur.value = weatherData.value[0].dayOfWeek.slice(0, 3);
    desCur.value = weatherData.value[0].weatherMain;
    columns.value = weatherData.value.slice(1, 6);
    columns.value.forEach((day) => {
      console.log(day);
      day.date = day.date.slice(5).replace(/-/g, "/");
      day.dayOfWeek = day.dayOfWeek.slice(0, 3);
      day.temp = Math.round(day.temp);
    });
  });
});
</script>

<style lang="scss" scoped>
.this {
  font-family: "Franklin Gothic Medium", "Arial Narrow", Arial, sans-serif;

  .curr-weather {
    width: 100%;
    height: 100px;
    display: flex;
    align-items: center;
    color: #ffffff;

    img {
      margin-top: -12px;
      margin-left: -12px;
      height: 130px;
    }
    .temp-curr {
      font-size: 55px;
      margin-left: -20px;
    }
    .curr-date {
      margin-top: 30px;
      width: 60px;
      height: 90px;

      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;

      .date-curr {
        font-size: 30px;
        position: relative;
        top: 20px;
        margin-bottom: 35px;
        border-radius: 5px;
        font-style: italic;
      }

      .des-curr {
        font-size: 30px;
        position: relative;
        top: -50px;
      }
    }
  }
  .future-weather {
    width: 100%;
    height: 300px;
    // border: 1px solid rgb(255, 255, 255);
    border-radius: 5px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .each-day {
      color: #ffffff;
      display: flex;
      // background-color: aqua;
      width: 100%;
      justify-content: space-between;
      align-items: baseline;
    }
  }
}
</style>
