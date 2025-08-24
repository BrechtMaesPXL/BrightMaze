<template>
  <div class="page-wrapper">

  <Navigation />

    <table-view/>
  </div>
</template>

<script setup>
    import Navigation from '../components/Menu/Navigation.vue'
    import TableView from "@/views/TableView.vue";
</script>
<style scoped>
.page-wrapper {
  padding-top: 100px;

  height: 100vh;
  display: flex;
  flex-direction: column;
  background:
    linear-gradient(rgba(0, 0, 0, 0.85), rgba(0, 0, 0, 0.85)),
    url('https://cdn.mos.cms.futurecdn.net/2NrTmPW9XnRMczrPiRM8M3.jpg');
  background-size: auto;
  background-repeat: repeat;
  background-position: center;
  padding-bottom: 20px;
  overflow: hidden;
}

</style>
