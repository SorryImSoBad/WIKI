import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import axios from "axios";

Vue.config.productionTip = false

Vue.use(Antd);

/**
 * axios拦截器
 */
axios.interceptors.request.use(function (config) {
  console.log('请求参数：', config);
  return config;
}, error => {
  return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
  console.log('返回结果：', response);
  return response;
}, error => {
  console.log('返回错误：', error);
  return Promise.reject(error);
});

console.log('环境：', process.env);

const app = new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
