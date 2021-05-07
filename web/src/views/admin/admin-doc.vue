<template>

  <a-layout class="home">
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form
          layout="inline"
          :model="param"
      >
        <a-form-item>
          <a-button type="primary" @click="handleQuery()" size="large">刷新</a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="add" size="large">新增</a-button>
        </a-form-item>
      </a-form>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="level1"
          :loading="loading"
          :pagination="false"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="showModal(record)">
              编辑
            </a-button>
            <a-popconfirm
                title="Are you sure delete this task?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      title="文档表单"
      v-model:visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
  >
    <p>
      <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 18}">
        <a-form-item label="名称">
          <a-input v-model:value="formState.name" />
        </a-form-item>
        <a-form-item label="父文档">
<!--          <a-input v-model:value="formState.parent" />-->
          <a-select
              v-model:value="formState.parent"
              ref="select"
          >
            <a-select-option value="0">无</a-select-option>
            <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="formState.id === c.id">{{ c.name }}</a-select-option>
          </a-select>
<!--          <a-tree-select-->
<!--              v-model:value="formState.parent"-->
<!--              show-search-->
<!--              style="width: 100%"-->
<!--              :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"-->
<!--              placeholder="Please select"-->
<!--              allow-clear-->
<!--              tree-default-expand-all-->
<!--          >-->
<!--            <a-tree-select-node key="0-1" value="parent 1" title="parent 1">-->
<!--              <a-tree-select-node key="0-1-1" value="parent 1-0" title="parent 1-0">-->
<!--                <a-tree-select-node key="random" value="leaf1" title="my leaf" />-->
<!--                <a-tree-select-node key="random1" value="leaf2" title="your leaf" />-->
<!--              </a-tree-select-node>-->
<!--              <a-tree-select-node key="random2" value="parent 1-1" title="parent 1-1">-->
<!--                <a-tree-select-node key="random3" value="sss">-->
<!--                  <template #title><b style="color: #08c">sss</b></template>-->
<!--                </a-tree-select-node>-->
<!--              </a-tree-select-node>-->
<!--            </a-tree-select-node>-->
<!--          </a-tree-select>-->
        </a-form-item>
        <a-form-item label="排序">
          <a-input v-model:value="formState.sort" />
        </a-form-item>
      </a-form>
    </p>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import { message } from 'ant-design-vue';
import { Tool} from "@/util/tool";

export default defineComponent({
  name: 'AdminDoc',
  setup() {
    const param = ref();
    param.value = {};
    const docs = ref();
    const level1 = ref();
    const loading = ref(false);

    const columns = [
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '父文档',
        key: 'parent',
        dataIndex: 'parent'
      },
      {
        title: '顺序',
        dataIndex: 'sort'
      },
      {
        title: 'Action',
        key: 'action',
        slots: {customRender: 'action'}
      }
    ];

    /**
     * 数据查询
     **/
    const handleQuery = () => {
      loading.value = true;
      axios.get(process.env.VUE_APP_SERVER + "/doc/all").then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success){
          docs.value = data.content;
          console.log('原数组',docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value,0);
          console.log('树形数组',docs.value);
        } else {
          message.error(data.message);
        }

      });
    };

    //---------表单---------
    const visible = ref<boolean>(false);
    const confirmLoading = ref<boolean>(false);

    //---------表单---------
    const formState = ref();

    //编辑
    const showModal = (record: any) => {
      visible.value = true;
      formState.value = Tool.copy(record);
    };

    //新增
    const add = () => {
      visible.value = true;
      formState.value = {};
    };

    const handleDelete = (id: number) =>{
      axios.delete(process.env.VUE_APP_SERVER + "/doc/delete/"+id).then((response) => {
        const data = response.data;
        if (data.success){
          //重新加载列表
          handleQuery();
        }
      });
    };

    const handleOk = () => {
      console.log('123',formState.value);
      confirmLoading.value = true;
      axios.post(process.env.VUE_APP_SERVER + "/doc/save", formState.value
      ).then((response) => {
        const data = response.data;
        confirmLoading.value = false;
        if (data.success){
          visible.value = false;

          //重新加载列表
          handleQuery();
        } else {
          message.error(data.message);

        }
      });
    };

    onMounted(() => {
      handleQuery();
    });

    return {

      docs,
      level1,
      columns,
      loading,
      visible,
      confirmLoading,
      showModal,
      handleOk,
      formState,
      add,
      handleDelete,
      handleQuery,
      param,
    };
  },
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>