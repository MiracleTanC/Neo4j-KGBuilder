import Vue from "vue"
import VueRouter from "vue-router"
import Home from "../views/Home.vue"
import KgDemo from "@/views/KgDemo.vue"
import KgDemo2 from "@/views/KgDemo2.vue"
Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/kg",
    name: "kg",
    component: KgDemo,
  },
  {
    path: "/kg2",
    name: "kg2",
    component: KgDemo2,
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/About.vue"),
  },
]

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
})

export default router
