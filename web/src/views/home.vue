<template>
  <a-layout class="home">
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
      >
        <a-menu-item key="1">
          <router-link :to="'/'">
            <MailOutlined/>
            <span>欢迎</span>
          </router-link>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <span>
              <user-outlined/>
              {{ item.name }}
            </span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id" @click="handleClick">
            <MailOutlined/>
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-list item-layout="vertical" size="large" :grid="{ gutter: 20, column: 3 }" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span v-for="{ type, text } in actions" :key="type">
                <component v-bind:is="type" style="margin-right: 8px"/>
                {{ text }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <a :href="item.href">{{ item.name }}</a>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover"/>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>

    </a-layout-content>
  </a-layout>

</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import {StarOutlined, LikeOutlined, MessageOutlined} from '@ant-design/icons-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";
import {message} from "ant-design-vue";


export default defineComponent({
  name: 'Home',
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  setup() {
    console.log('setup');
    const ebooks = ref();
    const level1 = ref();

    let categorys: any;
    /*
    * 查询所以分类
    * */
    const handleQueryCategory = () => {
      axios.get(process.env.VUE_APP_SERVER + "/category/all").then((response) => {
        const data = response.data;
        if (data.success) {
          categorys = data.content;
          console.log('原数组', categorys);

          level1.value = [];
          level1.value = Tool.array2Tree(categorys, 0);
          console.log('树形数组', categorys);
        } else {
          message.error(data.message);
        }

      });
    };

    const handleClick = () => {
      console.log("mune click");
    }

    onMounted(() => {
      handleQueryCategory();
      console.log('onMounted');
      axios.get(process.env.VUE_APP_SERVER + '/ebook/list', {
        params: {
          page: 1,
          size: 1000,
        }
      }).then((response) => {
        const data = response.data;
        ebooks.value = data.content.list;
        console.log(response);
      });
    });

    const pagination = {
      onChange: (page: number) => {
        console.log(page);
      },
      pageSize: 3,
    };
    const actions: Record<string, string>[] = [
      {type: 'StarOutlined', text: '156'},
      {type: 'LikeOutlined', text: '156'},
      {type: 'MessageOutlined', text: '2'},
    ];

    return {
      ebooks,
      pagination,
      actions,
      level1,
      handleClick,
    };
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

