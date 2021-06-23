<template>

  <a-layout class="home">
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form
          layout="inline"
          :model="param"
      >
        <a-form-item>
          <a-input v-model:value="param.name" placeholder="登录名">
            <template #prefix>
              <UserOutlined style="color: rgba(0, 0, 0, 0.25)"/>
            </template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleQuery({page: 1,size: pagination.pageSize})" size="large">
            查询
          </a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="add" size="large">
            新增
          </a-button>
        </a-form-item>
      </a-form>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="users"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template v-slot:action="{ text, record }">
          <a-space size="small">
            <a-button type="primary" @click="resetshowModal(record)">
              重置密码
            </a-button>
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
      title="用户表单"
      v-model:visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
  >
    <p>
      <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="登陆名">
          <a-input v-model:value="formState.loginName" :disabled="!!formState.id"/>
        </a-form-item>
        <a-form-item label="名称">
          <a-input v-model:value="formState.name"/>
        </a-form-item>
        <a-form-item label="密码" v-show="!formState.id">
          <a-input v-model:value="formState.password" />
        </a-form-item>
      </a-form>
    </p>
  </a-modal>

  <a-modal
      title="重置密码"
      v-model:visible="resetvisible"
      :confirm-loading="resetconfirmLoading"
      @ok="handleResetOk"
  >
    <p>
      <a-form :model="formState" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="新密码">
          <a-input v-model:value="formState.password" />
        </a-form-item>
      </a-form>
    </p>
  </a-modal>
</template>

<script lang="ts">
import {defineComponent, onMounted, ref} from 'vue';
import axios from 'axios';
import {message} from 'ant-design-vue';
import {Tool} from "@/util/tool";

declare let hexMd5: any
declare let KEY: any

export default defineComponent({
  name: 'AdminEbook',
  setup: function () {
    const param = ref();
    param.value = {};
    const users = ref();
    const level1 = ref();
    const categoryIds = ref();
    const pagination = ref({
      current: 1,
      pageSize: 4,
      total: 0
    });
    const loading = ref(false);

    const columns = [
      {
        title: '登陆名',
        dataIndex: 'loginName',
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '密码',
        dataIndex: 'password'
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
    const handleQuery = (params: any) => {
      loading.value = true;
      console.log('params:', params);
      axios.get(process.env.VUE_APP_SERVER + "/user/list", {
        params: {
          page: params.page,
          size: params.size,
          loginName: param.value.name,
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          users.value = data.content.list;

          // 重置分页按钮
          pagination.value.current = params.page;
          pagination.value.total = data.content.total;
        } else {
          message.error(data.message);
        }

      });
    };

    /**
     * 表格点击页码时触发
     */
    const handleTableChange = (pagination: any) => {
      console.log("看看自带的分页参数都有啥：" + pagination);
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
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

    const handleDelete = (id: number) => {
      axios.delete(process.env.VUE_APP_SERVER + "/user/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message)
        }
      });
    };

    const handleOk = () => {
      console.log('handleOk', formState.value);
      confirmLoading.value = true;

      formState.value.password = hexMd5(formState.value.password + KEY)

      axios.post(process.env.VUE_APP_SERVER + "/user/save", formState.value
      ).then((response) => {
        const data = response.data;
        confirmLoading.value = false;
        if (data.success) {
          visible.value = false;

          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);

        }
      });
    };

    //---------重置密码---------
    const resetvisible = ref<boolean>(false);
    const resetconfirmLoading = ref<boolean>(false);

    //编辑
    const resetshowModal = (record: any) => {
      resetvisible.value = true;
      formState.value = Tool.copy(record);
      formState.value.password = null
    };

    const handleResetOk = () => {
      console.log('resethandleOk', formState.value);
      resetconfirmLoading.value = true;

      formState.value.password = hexMd5(formState.value.password + KEY)

      axios.post(process.env.VUE_APP_SERVER + "/user/reset-password", formState.value
      ).then((response) => {
        const data = response.data;
        resetconfirmLoading.value = false;
        if (data.success) {
          resetvisible.value = false;

          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          message.error(data.message);

        }
      });
    };


    onMounted(() => {
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });

    });

    return {
      users,
      pagination,
      columns,
      loading,
      handleTableChange,
      visible,
      confirmLoading,
      showModal,
      handleOk,
      formState,
      add,
      handleDelete,
      handleQuery,
      param,
      categoryIds,
      level1,
      resetvisible,
      resetconfirmLoading,
      resetshowModal,
      handleResetOk,
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