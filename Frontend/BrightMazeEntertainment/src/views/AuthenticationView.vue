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

    <h1 class="logo-text">Bright Maze Entertainment</h1>
    <div id="buttons">
      <v-btn @click="step = 1" class="neon-btn">login</v-btn>
      <v-btn @click="step = 2" class="neon-btn">register</v-btn>
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
  background-image: url("https://wallpapercave.com/w/wp13852284.jpg");
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


.logo-text {
  position: absolute;
  top: 30px;
  left: 30px;
  font-size: 3rem;
  font-weight: bold;
  color: #00f0ff;
  text-shadow: 0 0 8px #00f0ff, 0 0 16px #00cfff, 0 0 24px #0099ff;
  z-index: 10;
}
.neon-btn {
  margin: 0 15px;
  padding: 12px 30px;
  border-radius: 8px;
  font-weight: bold;
  color: #00f0ff !important;
  background: #ff0099;
  text-transform: uppercase;
  letter-spacing: 1px;
  box-shadow: 0 0 15px #ff33cc, 0 0 30px #ff0099;
  transition: all 0.3s ease-in-out;
}
.neon-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 15px #ff33cc, 0 0 30px #ff0099;
}
</style>
