<template>
  <div style="{margin: 24px;}">
    <p><a-button type="primary" @click="add" size="large">新增</a-button></p>
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
          <a-form-model-item label="分类一">
            <a-input v-model="form.category1Id" />
          </a-form-model-item>
          <a-form-model-item label="分类二">
            <a-input v-model="form.category2Id" />
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

export default Vue.extend ({
  name: "admin-ebook",
  data() {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      form: {},

      visible: false,
      confirmLoading: false,

      ebooks: [],
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
          title: '分类一',
          dataIndex: 'category1Id'
        },
        {
          title: '分类二',
          dataIndex: 'category2Id'
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
    this.handleQuery({
      page: 1,
      size: this.pagination.pageSize,
    });
  },
  methods:{
    /**
     * 表格点击页码时触发
     */
    handleTableChange(pagination: any){
      console.log("看看自带的分页参数都有啥：" + pagination);
      this.handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      });
    },
    /**
     * 数据查询
     **/
    handleQuery(params: any){
        this.loading = true;
        console.log('12'+params);
        axios.get(process.env.VUE_APP_SERVER+"/ebook/list", {
          params:{
            page: params.page,
            size: params.size,
          }
        }).then((response) => {
        this.loading = false;
        console.log(response);
        let data = response.data;
        this.ebooks = data.content.list;

        // 重置分页按钮
        this.pagination.current = params.page;
        this.pagination.total = data.content.total;
      });
    },

    /**
     * 编辑模块框
     **/
    showModal(record: any) {
      this.visible = true;
      this.form = record;
    },
    handleOk(e: any) {
      this.confirmLoading = true;
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
  }
})
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>