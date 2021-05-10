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
                @confirm="CshowModal(record.id)"
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
    <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 18}">
      <a-form-item label="名称">
        <a-input v-model:value="formState.name"/>
      </a-form-item>
      <a-form-item label="父文档">
        <a-tree-select
            v-model:value="formState.parent"
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            :tree-data="treeSelectData"
            placeholder="Please select"
            tree-default-expand-all
            :replaceFields="{title: 'name', key: 'id', value: 'id'}"
        >

        </a-tree-select>
      </a-form-item>
      <a-form-item label="排序">
        <a-input v-model:value="formState.sort"/>
      </a-form-item>
      <a-form-item label="内容">
        <Wang_Editor/>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal
      title="确认"
      v-model:visible="Cvisible"
      :confirm-loading="CconfirmLoading"
      @ok="handleDelete"
  >
    <p>{{ modalText }}</p>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from 'vue-router';
import Wang_Editor from "@/components/wangeditor.vue";

export default defineComponent({
  name: 'AdminDoc',
  components:{
    Wang_Editor,
  },
  setup() {
    const route = useRoute();
    console.log("路由", route);
    const param = ref();
    param.value = {};
    const docs = ref({});
    const level1 = ref();
    const loading = ref(false);
    const treeSelectData = ref();
    treeSelectData.value = [];
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
      level1.value = [];
      axios.get(process.env.VUE_APP_SERVER + "/doc/all").then((response) => {
        loading.value = false;
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

    //---------表单---------
    const visible = ref<boolean>(false);
    const confirmLoading = ref<boolean>(false);

    //---------表单---------
    const formState = ref();

    //编辑
    const showModal = (record: any) => {
      visible.value = true;
      formState.value = Tool.copy(record);

      //不能选择当前节点及子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisabled(treeSelectData.value, record.id);

      //为选择添加一个无
      treeSelectData.value.unshift({id: 0, name: '无'});
    };

    //新增
    const add = () => {
      visible.value = true;
      formState.value = {
        ebookId: route.query.ebookId,
      };

      treeSelectData.value = Tool.copy(level1.value);

      //为选择添加一个无
      treeSelectData.value.unshift({id: 0, name: '无'});
    };

    const handleDelete = () => {
      modalText.value = 'The modal will be closed after two seconds';
      CconfirmLoading.value = true;
      axios.delete(process.env.VUE_APP_SERVER + "/doc/delete/" + ids.join(",")).then((response) => {
        const data = response.data;
        CconfirmLoading.value = false;
        if (data.success) {
          Cvisible.value = false;

          //重新加载列表
          handleQuery();
        } else {
          message.error(data.message);

        }
      });
    };

    const handleOk = () => {
      console.log('123', formState.value);
      confirmLoading.value = true;
      axios.post(process.env.VUE_APP_SERVER + "/doc/save", formState.value
      ).then((response) => {
        const data = response.data;
        confirmLoading.value = false;
        if (data.success) {
          visible.value = false;

          //重新加载列表
          handleQuery();
        } else {
          message.error(data.message);

        }
      });
    };

    /*
    * 删除二次确认
    * */
    const modalText = ref<string>('Content of the modal');
    const Cvisible = ref<boolean>(false);
    const CconfirmLoading = ref<boolean>(false);

    const CshowModal = (id: number) => {
      ids = [];
      names = [];
      getDeleteIds(level1.value, id);
      modalText.value = '将删除:【' + names.join(',') + '】删除后不可恢复,确认删除?';
      Cvisible.value = true;
    };

    /*
     * 将当前节点与其子节点变成disabled
     */
    const setDisabled = (treeSelectData: any, id: any) => {
      //遍历某一节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点是目标节点
          console.log("disabled", node);
          node.disabled = true;

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let i = 0; i < children.length; i++) {
              setDisabled(children, children[i].id);
            }
          }
        } else {
          //如果当前节点不是目标节点，找其子节点是不是目标节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisabled(children, id);
          }
        }
      }
    };

    let ids: Array<string> = [];
    let names: Array<string> = [];
    /*
     * 查找整根树枝
     */
    const getDeleteIds = (treeSelectData: any, id: any) => {
      //遍历某一节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          //如果当前节点是目标节点
          ids.push(id);
          names.push(node.name);

          //遍历所有子节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let i = 0; i < children.length; i++) {
              getDeleteIds(children, children[i].id);
            }
          }
        } else {
          //如果当前节点不是目标节点，找其子节点是不是目标节点
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
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
      treeSelectData,
      modalText,
      Cvisible,
      CconfirmLoading,
      CshowModal,
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