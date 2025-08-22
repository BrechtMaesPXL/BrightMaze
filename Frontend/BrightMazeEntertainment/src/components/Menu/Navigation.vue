<script setup>
import { RouterLink } from 'vue-router'

function logout() {
  localStorage.clear();
}
</script>

<template>
  <nav>
    <RouterLink to="/"><h1>Wildfires</h1></RouterLink>
    <ul>
      <li><RouterLink to="/home">Home</RouterLink></li>
      <li><RouterLink to="/insights">Insights</RouterLink></li>

      <li><RouterLink @click="logout" to="/">Log out</RouterLink></li>
    </ul>
  </nav>
</template>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  text-decoration: none;
}

nav {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: rgba(16, 16, 16, 0.85);
  border-bottom: 5px solid #e98616;
  padding: 0 1vw;
}

h1 {
  color: #e98616;
  font-size: 50px;
}

ul {
  display: flex;
}

ul li {
  list-style: none;
}

ul li a {
  color: #e98616;
  font-size: 30px;
  padding: 30px;
  transition: opacity 0.3s;
}

ul li a:hover {
  opacity: 0.6;
}
</style>
