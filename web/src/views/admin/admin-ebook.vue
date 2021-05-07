<template>

  <a-layout class="home">
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <a-form
          layout="inline"
          :model="param"
      >
        <a-form-item>
          <a-input v-model:value="param.name" placeholder="Username">
            <template #prefix>
              <UserOutlined style="color: rgba(0, 0, 0, 0.25)"/>
            </template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleQuery({page: 1,size: pagination.pageSize})" size="large">查询</a-button>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="add" size="large">新增</a-button>
        </a-form-item>
      </a-form>
      <a-table
          :columns="columns"
          :row-key="record => record.id"
          :data-source="ebooks"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #cover="{ text: cover }">
          <img v-if="cover" :src="cover" alt="avatar"/>
        </template>
        <template v-slot:category="{text, record }">
          <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
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
      title="Title"
      v-model:visible="visible"
      :confirm-loading="confirmLoading"
      @ok="handleOk"
  >
    <p>
      <a-form :model="formState" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-form-item label="封面">
          <a-input v-model:value="formState.cover"/>
        </a-form-item>
        <a-form-item label="名称">
          <a-input v-model:value="formState.name"/>
        </a-form-item>
        <a-form-item label="分类">
          <a-cascader v-model:value="categoryIds" :field-names="{ label: 'name', value: 'id', children: 'children'}"
                      :options="level1" placeholder="Please select"/>
        </a-form-item>
        <a-form-item label="描述">
          <a-input v-model:value="formState.description"/>
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

export default defineComponent({
  name: 'AdminEbook',
  setup: function () {
    const param = ref();
    param.value = {};
    const ebooks = ref();
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
        title: '封面',
        dataIndex: 'cover',
        slots: {customRender: 'cover'}
      },
      {
        title: '名称',
        dataIndex: 'name'
      },
      {
        title: '分类',
        slots: {customRender: 'category'}
      },
      {
        title: '文档数',
        dataIndex: 'docCount'
      },
      {
        title: '阅读数',
        dataIndex: 'viewCount'
      },
      {
        title: '点赞数',
        dataIndex: 'voteCount'
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
      axios.get(process.env.VUE_APP_SERVER + "/ebook/list", {
        params: {
          page: params.page,
          size: params.size,
          name: param.value.name,
        }
      }).then((response) => {
        loading.value = false;
        const data = response.data;
        if (data.success) {
          ebooks.value = data.content.list;

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
      categoryIds.value = [formState.value.category1Id, formState.value.category2Id]
    };

    //新增
    const add = () => {
      visible.value = true;
      formState.value = {};
    };

    const handleDelete = (id: number) => {
      axios.delete(process.env.VUE_APP_SERVER + "/ebook/delete/" + id).then((response) => {
        const data = response.data;
        if (data.success) {
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        }
      });
    };

    const handleOk = () => {
      console.log('handleOk', formState.value);
      confirmLoading.value = true;
      formState.value.category1Id = categoryIds.value[0];
      formState.value.category2Id = categoryIds.value[1];
      axios.post(process.env.VUE_APP_SERVER + "/ebook/save", formState.value
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

    let categorys: any;
    /*
    * 查询所以分类
    * */
    const handleQueryCategory = () => {
      loading.value = true;
      axios.get(process.env.VUE_APP_SERVER + "/category/all").then((response) => {
        loading.value = false;
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

    const getCategoryName = (cid: number) => {
      let result = "";
      categorys.forEach((item:any) => {
        if (item.id === cid)
          result = item.name;
      });
      return result;
    };



    onMounted(() => {
      handleQueryCategory();
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    return {
      ebooks,
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
      getCategoryName,
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