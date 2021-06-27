<template>

  <a-layout class="home">
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-row :gutter="24">
        <a-col :span="8">
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
              v-if="level1.length > 0"
              :columns="columns"
              :row-key="record => record.id"
              :data-source="level1"
              :loading="loading"
              :pagination="false"
              size="small"
              :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{ record.sort }} {{ text }}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="showModal(record)" size="small">
                  编辑
                </a-button>
                <a-popconfirm
                    title="Are you sure delete this task?"
                    ok-text="Yes"
                    cancel-text="No"
                    @confirm="CshowModal(record.id)"
                >
                  <a-button type="danger" size="small">
                    删除
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave">
                  保存
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="formState" layout="vertical">
            <a-form-item>
              <a-input v-model:value="formState.name" placeholder="请输入名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="formState.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  :tree-data="treeSelectData"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >

              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="formState.sort" placeholder="请输入顺序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviweContent">
                <EyeOutlined/>内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div ref='editor'></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>
  <!--  <a-modal-->
  <!--      title="文档表单"-->
  <!--      v-model:visible="visible"-->
  <!--      :confirm-loading="confirmLoading"-->
  <!--      @ok="handleOk"-->
  <!--  >-->
  <!--  </a-modal>-->
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
import WangEditor from "wangeditor";
import {defineComponent, onMounted, reactive, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";
import {useRoute} from 'vue-router';
import Wang_Editor from "@/components/wangeditor.vue";

export default defineComponent({
  name: 'AdminDoc',
  components: {
    Wang_Editor,
  },
  setup() {
    const route = useRoute();
    console.log("路由", route);
    const param = ref();
    param.value = {};
    const docs = ref();
    docs.value = {};
    const level1 = ref();
    level1.value = [];
    const loading = ref(false);
    const treeSelectData = ref();
    treeSelectData.value = [];
    const columns = [
      {
        title: '名称',
        dataIndex: 'name',
        slots: {customRender: 'name'}
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
      axios.get(process.env.VUE_APP_SERVER + "/doc/all/" + route.query.ebookId
      ).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          docs.value = data.content;
          console.log('原数组', docs.value);

          level1.value = [];
          level1.value = Tool.array2Tree(docs.value, 0);
          console.log('树形数组', level1.value);

          //不能选择当前节点及子孙节点，作为父节点，会使树断开
          treeSelectData.value = Tool.copy(level1.value);
          //为选择添加一个无
          treeSelectData.value.unshift({id: 0, name: '无'});

        } else {
          message.error(data.message);
        }

      });
    };

    /**
     * 内容查询
     **/
    const handleQueryContent = () => {
      axios.get(process.env.VUE_APP_SERVER + "/doc/find-content/" + formState.value.id
      ).then((response) => {
        const data = response.data;
        if (data.success) {
          instance.txt.html(data.content)
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

    const editor = ref();
    let instance: WangEditor;


    formState.value = {
      ebookId: route.query.ebookId,
    };

    //编辑
    const showModal = (record: any) => {
      //清空富文本框
      instance.txt.html("");
      visible.value = true;
      formState.value = Tool.copy(record);
      handleQueryContent();

      //不能选择当前节点及子孙节点，作为父节点，会使树断开
      treeSelectData.value = Tool.copy(level1.value);
      setDisabled(treeSelectData.value, record.id);

      //为选择添加一个无
      treeSelectData.value.unshift({id: 0, name: '无'});
    };

    //新增
    const add = () => {
      //清空富文本框
      instance.txt.html("");
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

    const handleSave = () => {
      console.log('123', formState.value);
      confirmLoading.value = true;
      formState.value.content = instance.txt.html();
      axios.post(process.env.VUE_APP_SERVER + "/doc/save", formState.value
      ).then((response) => {
        const data = response.data;
        confirmLoading.value = false;
        if (data.success) {
          //visible.value = false;
          message.success("保存成功！")

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

    //----------------富文本预览---------------
    const drawerVisible = ref(false);
    const previewHtml = ref();
    const handlePreviweContent = () => {
      const html = instance.txt.html();
      previewHtml.value = html;
      drawerVisible.value = true;
    }
    const onDrawerClose = () => {
      drawerVisible.value = false;
    }


    onMounted(() => {
      handleQuery();
      instance = new WangEditor(editor.value);
      instance.config.zIndex = 0;
      instance.create();
    });

    return {
      editor,
      docs,
      level1,
      columns,
      loading,
      visible,
      confirmLoading,
      showModal,
      handleSave,
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
      drawerVisible,
      previewHtml,
      handlePreviweContent,
      onDrawerClose
    };
  },
});
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
/* table 样式 */
.wangeditor table {
  border-top: 1px solid #ccc;
  border-left: 1px solid #ccc;
}
.wangeditor table td,
.wangeditor table th {
  border-bottom: 1px solid #ccc;
  border-right: 1px solid #ccc;
  padding: 3px 5px;
}
.wangeditor table th {
  border-bottom: 2px solid #ccc;
  text-align: center;
}

/* blockquote 样式 */
.wangeditor blockquote {
  display: block;
  border-left: 8px solid #d0e5f2;
  padding: 5px 10px;
  margin: 10px 0;
  line-height: 1.4;
  font-size: 100%;
  background-color: #f1f1f1;
}

/* code 样式 */
.wangeditor code {
  display: inline-block;
  *display: inline;
  *zoom: 1;
  background-color: #f1f1f1;
  border-radius: 3px;
  padding: 3px 5px;
  margin: 0 3px;
}
.wangeditor pre code {
  display: block;
}

/* ul ol 样式 */
.wangeditor ul, ol {
  margin: 10px 0 10px 20px;
}

.wangeditor blockquote p{
  font-family: "YouYuan";
  margin: 20px 10px !important;
  font-size: 16px !important;
  font-weight: 600;
}
</style>