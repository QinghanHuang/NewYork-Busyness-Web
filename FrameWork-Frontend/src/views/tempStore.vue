<template>
  <el-dropdown style="position: absolute; top: 5px; font-size: 15px; left: 10px">
    {{ todayDate }}
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="date in futureDates"
          :key="date"
          :command="date"
          @command="handleDateSelect"
        >
          {{ date }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const todayDate = ref(""); // 格式为 "XXXX-XX-XX" 的当前日期
const futureDates = ref([]); // 未来五天的日期数组

const formatDate = (date) => {
  const year = date.getFullYear();
  const month = padNumber(date.getMonth() + 1);
  const day = padNumber(date.getDate());
  return `${year}-${month}-${day}`;
};

const padNumber = (num) => {
  return num.toString().padStart(2, "0");
};

onMounted(() => {
  todayDate.value = "XXXX-XX-XX"; // 设置当前日期
  generateFutureDates(); // 生成未来五天的日期
});

const generateFutureDates = () => {
  const today = new Date();
  for (let i = 0; i < 5; i++) {
    const futureDate = new Date(today);
    futureDate.setDate(today.getDate() + i + 1);
    futureDates.value.push(formatDate(futureDate));
  }
};

const handleDateSelect = (date) => {
  todayDate.value = date; // 更新todayDate的值为选择的日期
};
</script>
