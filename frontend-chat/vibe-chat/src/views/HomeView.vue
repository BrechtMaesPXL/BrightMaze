<script setup>
</script>

<template>
  <main>
    <div class="home-wrapper">
      <RouterLink to="/events">
        <button class="nav-button">Events</button>
      </RouterLink>
    </div>
  </main>
</template>

<style scoped>
.home-wrapper {
  text-align: center;
  margin-top: 2rem;
}

.nav-button {
  padding: 10px 20px;
  background-color: white;
  color: black;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  align-content: center;
}

.nav-button:hover {
  background-color: gray;
}
</style>
