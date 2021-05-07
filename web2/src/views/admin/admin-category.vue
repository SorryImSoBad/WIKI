<template>
  <div class="bg">
    <a-form-model layout="inline" :model="param">
      <a-form-model-item>
        <a-button type="primary" @click="handleQuery" size="large">刷新</a-button>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" @click="add" size="large">新增</a-button>
      </a-form-model-item>
    </a-form-model>
    <a-table
        :columns="columns"
        :row-key="record => record.id"
        :data-source="categorys"
        :loading="loading"
        :pagination="false"
    >
      <template slot="cover" slot-scope="cover">
        <img v-if="cover" :src="cover" alt="avatar"/>
      </template>
      <template slot="action" slot-scope="text, record">
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
    <a-modal
        title="Title"
        :visible="visible"
        :confirm-loading="confirmLoading"
        @ok="handleOk"
        @cancel="handleCancel"
    >
      <p>
        <a-form-model :model="form" :label-col="labelCol" :wrapper-col="wrapperCol">
          <a-form-model-item label="名称">
            <a-input v-model="form.name" />
          </a-form-model-item>
          <a-form-model-item label="分类一">
            <a-input v-model="form.parent" />
          </a-form-model-item>
          <a-form-model-item label="分类二">
            <a-input v-model="form.sort" />
          </a-form-model-item>
        </a-form-model>
      </p>
    </a-modal>
  </div>

</template>

<script lang="ts">
import Vue from 'vue';
import axios from "axios";
import {Tool} from "@/util/tool";
import { message } from 'ant-design-vue';

export default Vue.extend ({
  name: "admin-category",
  data() {
    return {
      param: {
        name: '',
      },
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      form: {},

      visible: false,
      confirmLoading: false,

      categorys: [],
      columns:[
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '父分类',
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
          scopedSlots: {customRender: 'action'}
        }
      ],
      loading: false,
    };
  },
  mounted(){
    this.handleQuery();
  },
  methods:{
    /**
     * 数据查询
     **/
    handleQuery(){
        this.loading = true;
        axios.get(process.env.VUE_APP_SERVER+"/category/all").then((response) => {
        this.loading = false;
        console.log(response);
        let data = response.data;
        if (data.success){
          this.categorys = data.content;
        } else {
          message.error(data.message);
        }

      });
    },

    /**
     * 编辑模块框
     **/
    showModal(record: any) {
      this.visible = true;
      this.form = Tool.copy(record);
    },
    handleOk(e: any) {
      this.confirmLoading = true;
      axios.post(process.env.VUE_APP_SERVER+"/category/save", this.form
      ).then((response) => {
        let data = response.data;
        if(data.success){
          this.visible = false;
          this.confirmLoading = false;

          //重新加载列表
          this.handleQuery();
        } else {
          message.error(data.message);
        }
      });
    },
    handleCancel(e: any) {
      console.log('Clicked cancel button');
      this.visible = false;
    },

    /*
    * 新增
    * */
    add(){
      this.visible = true;
      this.form = {};
    },

    /*
    * 删除
    * */
    handleDelete(id: number){
      axios.delete(process.env.VUE_APP_SERVER+"/category/delete/"+id).then((response) => {
        let data = response.data;
        if(data.success){
          //重新加载列表
          this.handleQuery();
        }
      });
    },
  }
})
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}

.bg{
  margin: 24px;
}
</style>