<template>
  <div ref='editor'></div>
  <button @click='syncHTML'>同步内容</button>
  <div :innerHTML='content.html'></div>
</template>

<script>
import {onMounted, onBeforeUnmount, ref, reactive} from 'vue';
import WangEditor from 'wangeditor';

export default {
  name: "wangeditor",
  setup() {
    const editor = ref();
    const content = reactive({
      html: '',
      text: '',
    });
    let instance;
    onMounted(() => {
      instance = new WangEditor(editor.value);
      instance.config.zIndex = 0;
      Object.assign(instance.config, {
        onchange() {
          console.log('change');
        },
      });
      instance.create();
    });
    onBeforeUnmount(() => {
      instance.destroy();
      instance = null;
    });
    const syncHTML = () => {
      content.html = instance.txt.html();
      this.$emit("content", content.html);
    };
    return {
      syncHTML,
      editor,
      content,
    };
  },
}
</script>