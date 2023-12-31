import {createRouter, createWebHistory} from 'vue-router'
import {useStore} from "@/stores/counter";


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/components/welcome/LoginPage.vue'),
                },{
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/components/welcome/RegisterPage.vue'),

                },{
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/components/welcome/ForgetPage.vue'),

                },{
                    path: 'func',
                    name: 'welcome-func',
                    component: () => import('@/components/welcome/FunctionPage.vue'),

                },{
                    path: 'set',
                    name: 'welcome-set',
                    component: () => import('@/components/welcome/SettingPage.vue'),

                }
            ]
        }, {
            // path: '/func',
            // name: 'index',
            // component:() => import('@/views/IndexView.vue')

        }

    ]
})

// router.beforeEach((to, from, next) => {
//     const store = useStore()
//     if(store.auth.user != null && to.name.startsWith('welcome-')) {
//         next('/index')
//     } else if(store.auth.user == null && to.fullPath.startsWith('/index')) {
//         next('/')
//     } else if(to.matched.length === 0){
//         next('/index')
//     } else {
//         next()
//     }
// })

export default router
