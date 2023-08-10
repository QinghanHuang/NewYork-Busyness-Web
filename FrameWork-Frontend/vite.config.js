import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import { visualizer } from "rollup-plugin-visualizer";

// element ui
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/




export default defineConfig({
    plugins: [
        vue(),
        AutoImport({
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            resolvers: [ElementPlusResolver()],
        }),
        visualizer({
            gzipSize: true,
            brotliSize: true,
            emitFile: false,
            filename: "test.html", //分析图生成的文件名
            open:true //如果存在本地服务端口，将在打包后自动展示
          }),
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    server: {
        proxy: {
            '/api': {
                target: 'http://137.43.49.76', // 代理目标的基础路径，修改为服务器的地址
                changeOrigin: true, // 是否改变请求源地址
                rewrite: (path) => path.replace(/^\/api/, ''), // 将 '/api' 替换为空字符串
            },
        },
    },

})
