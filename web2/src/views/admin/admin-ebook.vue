<template>
  <div style="{margin: 24px;}">
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
      <template slot="action">
        <a-space size="small">
          <a-button type="primary">
            编辑
          </a-button>
          <a-button type="danger">
            删除
          </a-button>
        </a-space>
      </template>
    </a-table>
  </div>

</template>

<script lang="ts">
import Vue from 'vue';
import axios from "axios";

export default Vue.extend ({
  name: "admin-ebook",
  data() {
    return {
      ebooks: [],
      pagination: {
        current: 1,
        pageSize: 2,
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
      size: 1,
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
        axios.get(process.env.VUE_APP_SERVER+"/ebook/list", params).then((response) => {
        this.loading = false;
        console.log(response);
        let data = response.data;
        this.ebooks = data.content;

        // 重置分页按钮
        this.pagination.current = params.page;
      });
    }
  }
})
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>