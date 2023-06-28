

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import axios from "axios";

const app = createApp(App)

// 137.43.49.76 is public IP for student server
//without security rule config now just use 80

// axios.defaults.baseURL='http://137.43.49.76'
axios.defaults.baseURL='http://localhost:8080'

app.use(createPinia())
app.use(router)

app.mount('#app')
