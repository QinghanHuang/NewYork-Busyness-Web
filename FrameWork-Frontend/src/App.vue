<script setup>
import router from "@/router";
import {useStore} from "@/stores/counter";
import {get} from "@/net/axios";



const store = useStore()

</script>


<template>
  <router-view/>
</template>

<style scoped>

</style>
