<template>
  <a-layout-header class="header">
    <div class="logo">Harry</div>
    <a-menu
        theme="dark"
        mode="horizontal"
        v-model:selectedKeys="selectedKeys1"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/">
        <router-link to="/">
          首页
        </router-link>
      </a-menu-item>
      <a-menu-item key="/admin/user" :style="user.id? {} : {display: 'none'}">
        <router-link to="/admin/user">
          用户管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/admin/ebook" :style="user.id? {} : {display: 'none'}">
        <router-link to="/admin/ebook">
          电子书管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/admin/category" :style="user.id? {} : {display: 'none'}">
        <router-link to="/admin/category">
          分类管理
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          关于我们
        </router-link>
      </a-menu-item>
      <a class="login-menu" @click="showLoginModal" v-show="!user.id">
        <span>登录</span>
      </a>
      <a-popconfirm
          title="确认退出登录?"
          ok-text="Yes"
          cancel-text="No"
          @confirm="logout"
      >
        <a class="login-menu" v-show="user.id">
          <span>退出登录</span>
        </a>
      </a-popconfirm>
      <a class="login-menu" v-show="user.id">
        <span>您好：{{ user.name }}</span>
      </a>
    </a-menu>

    <a-modal
        title="登录"
        v-model:visible="loginModalVisible"
        :confirm-loading="loginModalLoading"
        @ok="login"
    >
      <p>
        <a-form :model="loginUser" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
          <a-form-item label="登陆名">
            <a-input v-model:value="loginUser.loginName"/>
          </a-form-item>
          <a-form-item label="密码">
            <a-input v-model:value="loginUser.password" type="password"/>
          </a-form-item>
        </a-form>
      </p>
    </a-modal>
  </a-layout-header>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import store from '@/store';

declare let hexMd5: any
declare let KEY: any

export default defineComponent({
  name: 'the-header',
  setup: function () {
    const user = computed(() => {
      if (store.state.user) {
        return store.state.user
      } else {
        return ""
      }
    });

    //---------表单---------
    const loginModalVisible = ref<boolean>(false);
    const loginModalLoading = ref<boolean>(false);

    //---------表单---------
    const loginUser = ref({
      loginName: "test",
      password: "test123"
    });

    //编辑
    const showLoginModal = (record: any) => {
      loginModalVisible.value = true;
    };

    //登录
    const login = () => {
      console.log("开始登录")
      loginModalLoading.value = true
      loginUser.value.password = hexMd5(loginUser.value.password + KEY)

      axios.post(process.env.VUE_APP_SERVER + "/user/login", loginUser.value
      ).then((response) => {
        const data = response.data;
        loginModalLoading.value = false;
        if (data.success) {
          loginModalVisible.value = false;
          message.success("登陆成功");
          store.commit("setUser", data.content)
        } else {
          message.error(data.message);
        }
      });
    };

    //退出登录
    const logout = () => {
      console.log("退出登录开始")
      axios.get(process.env.VUE_APP_SERVER + "/user/logout/" + user.value.token
      ).then((response) => {
        const data = response.data;
        if (data.success) {
          message.success("退出成功");
          store.commit("setUser", {})
        } else {
          message.error(data.message);
        }
      });
    };

    return {
      loginModalVisible,
      loginModalLoading,
      loginUser,
      showLoginModal,
      login,
      user,
      logout,
    }
  }
});
</script>

<style scoped>
.logo {
  float: left;
  width: 120px;
  height: 31px;
  /*margin: 16px 24px 16px 0;*/
  /*background: rgba(255, 255, 255, 0.3);*/
  font-size: 28px;
  color: white;
}

.site-layout-background {
  background: #fff;
}

.login-menu {
  float: right;
  color: white;
  padding-left: 10px;
}
</style>