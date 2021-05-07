<template>
  <div class="bg">
    <a-form-model layout="inline" :model="param">
      <a-form-model-item>
        <a-input v-model="param.name" placeholder="Username">
          <a-icon slot="prefix" type="user" style="color:rgba(0,0,0,.25)" />
        </a-input>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" @click="handleQuery({page:1,size:pagination.pageSize})" size="large">查询</a-button>
      </a-form-model-item>
      <a-form-model-item>
        <a-button type="primary" @click="add" size="large">新增</a-button>
      </a-form-model-item>
    </a-form-model>
    <a-table
        :columns="columns"
        :row-key="record => record.id"
        :data-source="ebooks"
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
    >
      <template slot="cover" slot-scope="cover">
        <img v-if="cover" :src="cover" alt="avatar"/>
      </template>
      <template slot="category" slot-scope="text, record">
        <span>{{ getCategoryName(record.category1Id) }} / {{ getCategoryName(record.category2Id) }}</span>
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
          <a-form-model-item label="封面">
            <a-input v-model="form.cover" />
          </a-form-model-item>
          <a-form-model-item label="名称">
            <a-input v-model="form.name" />
          </a-form-model-item>
          <a-form-model-item label="分类">
            <a-cascader v-model="categoryIds" :field-names="{ label:'name', value:'id', children:'children'}" :options="level1" placeholder="Please select"/>
          </a-form-model-item>
          <a-form-model-item label="描述">
            <a-input v-model="form.description" />
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
  name: "admin-ebook",
  data() {
    return {
      param: {
        name: '',
      },
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      //---------表单---------
      form: {
        category1Id: 0,
        category2Id: 0,
      },
      //---------编辑表单---------
      visible: false,
      confirmLoading: false,

      level1: [] as any[],
      categorys: [] as any[],
      categoryIds: [] as any[],
      ebooks: [] as any[],
      pagination: {
        current: 1,
        pageSize: 4,
        total: 0
      },
      columns:[
        {
          title: '封面',
          dataIndex: 'cover',
          scopedSlots: {customRender: 'cover'}
        },
        {
          title: '名称',
          dataIndex: 'name'
        },
        {
          title: '分类',
          scopedSlots: {customRender: 'category'}
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
          scopedSlots: {customRender: 'action'}
        }
      ],
      loading: false,
    };
  },
  mounted(){
    this.handleQueryCategory();
  },
  props:{
  },
  methods:{
    /**
     * 表格点击页码时触发
     */
    handleTableChange(pagination: any){
      console.log("看看自带的分页参数都有啥：" + pagination);
      this.handleQuery({
        page: pagination.current,
        size: pagination.pageSize,
      });
    },
    /**
     * 数据查询
     **/
    handleQuery(params: any){
        this.loading = true;
        console.log('params'+params);
        axios.get(process.env.VUE_APP_SERVER+"/ebook/list", {
          params:{
            page: params.page,
            size: params.size,
            name: this.param.name,
          }
        }).then((response) => {
        this.loading = false;
        console.log(response);
        let data = response.data;
        if (data.success){
          this.ebooks = data.content.list;

          // 重置分页按钮
          this.pagination.current = params.page;
          this.pagination.total = data.content.total;
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
      this.categoryIds = [this.form.category1Id, this.form.category2Id];
    },
    handleOk(e: any) {
      console.log('handleOk', this.form);
      this.confirmLoading = true;
      this.form.category1Id = this.categoryIds[0];
      this.form.category2Id = this.categoryIds[1];
      axios.post(process.env.VUE_APP_SERVER+"/ebook/save", this.form
      ).then((response) => {
        let data = response.data;
        if(data.success){
          this.visible = false;
          this.confirmLoading = false;

          //重新加载列表
          this.handleQuery({
            page: this.pagination.current,
            size: this.pagination.pageSize,
          });
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
      this.form = {
        category1Id: 0,
        category2Id: 0,
      };
    },

    /*
    * 删除
    * */
    handleDelete(id: number){
      axios.delete(process.env.VUE_APP_SERVER+"/ebook/delete/"+id).then((response) => {
        let data = response.data;
        if(data.success){
          //重新加载列表
          this.handleQuery({
            page: this.pagination.current,
            size: this.pagination.pageSize,
          });
        }
      });
    },

    /*
    * 查询所以分类
    * */
    handleQueryCategory(){
      this.loading= true;
      axios.get(process.env.VUE_APP_SERVER + "/category/all").then((response) => {
        this.loading = false;
        const data = response.data;
        if (data.success) {
          this.categorys = data.content;
          console.log('原数组', this.categorys);

          this.level1 = Tool.array2Tree(this.categorys, 0);
          console.log('树形数组', this.categorys);

          this.handleQuery({
            page: 1,
            size: this.pagination.pageSize,
          });
        } else {
          message.error(data.message);
        }

      });
    },

    getCategoryName(cid: number){
      let result = "";
      this.categorys.forEach((item:any) => {
        if (item.id === cid)
          result = item.name;
      });
      return result;
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