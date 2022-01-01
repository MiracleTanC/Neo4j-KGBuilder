import Vue from "vue";
import VueRouter from "vue-router";
import Home from "../views/Home.vue";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home
  },
  {
    path: "/builder",
    name: "console",
    component: () => import("../views/kgbuilder/index.vue")
  },
  {
    path: "/er",
    name: "er",
    component: () => import("../views/erbuilder/index.vue")
  },
  {
    path: "/ds",
    name: "ds",
    component: () => import("../views/datasource/index.vue")
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/About.vue")
  }
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes
});

export default router;
