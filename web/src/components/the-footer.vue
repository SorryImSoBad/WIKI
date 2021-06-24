<template>
  <a-layout-footer style="text-align: center">
    Ant Design ©2018 Created by Ant UED12,欢迎：{{ user.name }}
  </a-layout-footer>
</template>

<script lang="ts">
import store from '@/store';
import {computed, defineComponent} from 'vue';

export default defineComponent({
  name: 'the-footer',
  setup: function () {
    const user = computed(() => {
      if (store.state.user){
        return store.state.user
      } else {
        return ""
      }
    });

    return {
      user,
    }
  }
});
</script>

<style scoped>

</style>