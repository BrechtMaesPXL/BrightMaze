<script>
import loginComponent from "../components/Menu/LoginScreen.vue";
import registerComponent from "../components/Menu/RegisterScreen.vue";
import {RouterLink} from "vue-router";

export default {
  components: {RouterLink, loginComponent, registerComponent },
  data: () => ({
    step: 1,
  }),
}
</script>

<template>

  <div id="app">

    <h1>Wildfires</h1>
    <div id="buttons">
      <v-btn @click="step = 1">login</v-btn>
      <v-btn @click="step = 2">register</v-btn>
    </div>

    <v-window v-model="step">
      <v-window-item :value="1">
        <loginComponent />
      </v-window-item>
      <v-window-item :value="2">
        <registerComponent />
      </v-window-item>
    </v-window>
  </div>
</template>
<style scoped>

body, html {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  background-image: url('https://demos.codezeel.com/wordpress/WP02/WP031/default/wp-content/uploads/2024/09/main-banner-1.jpg');
  background-size: cover;
  background-position: center center;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

#buttons {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.v-btn {
  margin-left: 10px;
  margin-right: 10px;
  background-color: #e98616 !important;
  color: white !important;
  padding: 10px 20px;
  border-radius: 5px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
}

h1 {
  position: absolute;
  top: 20px;
  left: 20px;
  color: orange;
  font-size: 3rem;
  font-weight: bold;
  margin: 0;
  z-index: 10;
}
</style>
