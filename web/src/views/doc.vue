<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1.length > 0"
              :tree-data="level1"
              @select="onSelect"
              :replaceFields="{title:'name', key:'id', value:'id'}"
              :defaultExpandAllRows="true"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
          <div :innerHTML='content.html'></div>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
import {defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from 'vue-router';

export default defineComponent({
  name: 'Doc',
  setup() {
    const route = useRoute();
    const docs = ref();
    const level1 = ref();
    level1.value = [];
    const content = reactive({
      html: '',
      text: '',
    });

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      level1.value = [];
      axios.get(process.env.VUE_APP_SERVER + "/doc/all/" + route.query.ebookId).then((response) => {
        const data = response.data;
        if (data.success) {
          docs.value = data.content;
          console.log('原数组', docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log('树形数组', docs.value);
        } else {
          message.error(data.message);
        }

      });
    };

    /**
     * 内容查询
     **/
    const handleQueryContent = (id: number) => {
      axios.get(process.env.VUE_APP_SERVER + "/doc/find-content/" + id
      ).then((response) => {
        const data = response.data;
        if (data.success) {
          content.html = data.content;
        } else {
          message.error(data.message);
        }

      });
    };

    const onSelect = (selectedKeys: any, info: any) => {
      console.log('selected', selectedKeys, info);
      if (Tool.isNotEmpty(selectedKeys)) {
        handleQueryContent(selectedKeys[0])
      }
    }

    onMounted(() => {
      handleQuery();
    });

    return {
      level1,
      onSelect,
      content,
    }
  }
});
</script>

<style scoped>
</style>