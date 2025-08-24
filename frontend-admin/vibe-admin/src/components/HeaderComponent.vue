<template>
  <header class="header">
    <img
      src="@/assets/image-removebg-preview.png"
      class="logo"
      alt="Logo"
    />
  </header>
</template>

<script>
export default {
  name: "HeaderComponent"
}
</script>

<style scoped>
.header {
  position: relative;
  padding: 1rem;
}

/* The logo is positioned in the top right */
.logo {
  position: absolute;
  top: 1rem;
  right: 1rem;
  width: 5vw;      /* 10% of the viewport width */
  height: auto;     /* maintain aspect ratio */
}
</style>
