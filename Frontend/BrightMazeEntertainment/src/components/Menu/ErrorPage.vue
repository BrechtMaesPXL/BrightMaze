<template>
  <div class="error-page" >
    <h1>Oops! The page has burned up.</h1>
    <p>The page you where trying to access never existed or got an error.</p>
    <HomeButtonComponent/>
  </div>
</template>

<script setup>
import HomeButtonComponent from "@/components/Menu/HomeButtonComponent.vue";
</script>

<style scoped>


.error-page {
  justify-content: center;

  text-align: center;
  margin-top: 50px;
}

.error-page h1 {
  font-size: 2em;
  color: #e98616;
}

.error-page p {
  font-size: 1.2em;
  color: #e98616;

}
</style>
