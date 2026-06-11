<template>
  <component :is="is" v-bind="$attrs"></component>
</template>
<script setup lang="ts">
defineProps<{
  is: string
}>()
</script>
<style scoped lang="scss"></style>
