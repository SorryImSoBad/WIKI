<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :default-selected-keys="['1']"
          :default-open-keys="['1']"
          :style="{ height: '100%', borderRight: 0 }"
      >
        <a-menu-item key="1">
          <router-link to="/">
            <a-icon type="pie-chart"/>
            <span>欢迎</span>
          </router-link>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template slot="title">
            <span ><a-icon type="user"/>{{ item.name }}</span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <a-icon type="mail"/>{{ child.name }}
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout style="padding: 0 24px">
      <div >
        <span>
          欢迎
        </span>
      </div>
      <a-layout-content
          :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
      >
        <a-list item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
          <a-list-item slot="renderItem" key="item.title" slot-scope="item">
            <template v-for="{ type, text } in actions" slot="actions">
              <span :key="type">
                <a-icon :type="type" style="margin-right: 8px"/>
                {{ text }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <a slot="title" :href="item.href">{{ item.name }}</a>
              <a-avatar slot="avatar" :src="item.cover"/>
            </a-list-item-meta>
          </a-list-item>
        </a-list>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script lang="ts">
import Vue from 'vue';
import axios from "axios";
import {Tool} from "@/util/tool";
import {message} from "_ant-design-vue@1.7.4@ant-design-vue";

export default Vue.extend({
  name: 'Home',
  data() {
    return {
      ebooks: [] as any[],
      categorys: [] as any[],
      level1: [] as any[],

      pagination: {
        onChange: (page: any) => {
          console.log(page);
        },
        pageSize: 3,
      },
      actions: [
        {type: 'star-o', text: '156'},
        {type: 'like-o', text: '156'},
        {type: 'message', text: '2'},
      ],
    };
  },
  mounted() {
    this.handleQueryCategory();
    console.log('mounted123');
    axios.get(process.env.VUE_APP_SERVER + '/ebook/list', {
      params: {
        page: 1,
        size: 1000,
      }
    }).then((response) => {
      const data = response.data;
      this.ebooks = data.content.list;
      console.log(response);
      console.log(this.ebooks);
    });
  },
  methods:{
    /*
    * 查询所以分类
    * */
    handleQueryCategory(){
      axios.get(process.env.VUE_APP_SERVER + "/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          this.categorys = data.content;
          console.log('原数组', this.categorys);

          this.level1 = Tool.array2Tree(this.categorys, 0);
          console.log('树形数组', this.categorys);
        } else {
          message.error(data.message);
        }
      });
    },

    handleClick(){
      console.log("mune click");
    }
  }
});
</script>

<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>
