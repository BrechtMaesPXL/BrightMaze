<template>
  <div class="page-wrapper">
    <Navigation />

    <div class="container">
      <div class="welcome-text">
        <h1>Welcome {{ userStore.username }},</h1>
        <h1>The house is your's; Lose to your friends.</h1>
      </div>
    </div>

    <div class="content-section">
      <h2>Top Games</h2>
      <div
        class="scrollList"
        @mousedown="startDrag"
        @mousemove="handleDrag"
        @mouseup="stopDrag"
        @mouseleave="stopDrag"
        ref="scrollContainer1"
      >
        <v-row class="card-row" no-gutters>
          <v-col v-for="(card, index) in useHomeStore.cardsMap" :key="index" class="card-col">
            <CardComponent
              :route="card.route"
              :img="card.img"
              :title="card.title"
              :description="card.description"
            />
          </v-col>
        </v-row>
      </div>
    </div>

    <!-- Cards Data Section -->
    <div class="content-section">
      <h2>Interesting data:</h2>
      <div
        class="scrollList"
        @mousedown="startDrag"
        @mousemove="handleDrag"
        @mouseup="stopDrag"
        @mouseleave="stopDrag"
        ref="scrollContainer2"
      >
        <v-row class="card-row" no-gutters>
          <v-col v-for="(card, index) in useHomeStore.cardsData" :key="index" class="card-col">
            <CardComponent
              :route="card.route"
              :img="card.img"
              :title="card.title"
              :description="card.description"
            />
          </v-col>
        </v-row>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '../stores/user.js';
import CardComponent from '@/components/Menu/CardComponent.vue';
import Navigation from "@/components/Menu/Navigation.vue";
import {useHomeStore} from "@/stores/HomeStore.js";

const userId = localStorage.getItem('userId');
const userStore = useUserStore();


const isDragging = ref(false);
const startX = ref(0);
const scrollLeft = ref(0);
let currentScrollContainer = null;

const startDrag = (e) => {
  isDragging.value = true;
  currentScrollContainer = e.currentTarget;
  startX.value = e.pageX - currentScrollContainer.offsetLeft;
  scrollLeft.value = currentScrollContainer.scrollLeft;
};

const handleDrag = (e) => {
  if (!isDragging.value) return;
  e.preventDefault();
  const x = e.pageX - currentScrollContainer.offsetLeft;
  const walk = (x - startX.value) * 2;
  currentScrollContainer.scrollLeft = scrollLeft.value - walk;
};

const stopDrag = () => {
  isDragging.value = false;
};

const getUser = async (userId) => {
  try {
    await userStore.fetchUser(userId);
    console.log(`User fetched: ${JSON.stringify(userStore.$state)}`);
  } catch (error) {
    console.error('Error fetching users:', error);
  }
};

onMounted(() => {
  getUser(userId);
});
</script>

<style scoped>
body, html {
  margin: 0;
  padding: 0;
  height: 100%;
}

h1, h2, a {
  color: #e98616;
  margin: 0.5em 0;
}

.page-wrapper {
  min-height: 100vh;
  overflow-y: auto;
  background:
    linear-gradient(rgba(0, 0, 0, 0.85), rgba(0, 0, 0, 0.85)),
    url('https://cdn.mos.cms.futurecdn.net/2NrTmPW9XnRMczrPiRM8M3.jpg');
  background-size: auto;
  background-repeat: repeat;
  background-attachment: scroll;
}

.container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  margin-top: 5vh;
  flex-shrink: 0;
  text-align: center;
}

.section-title {
  margin: 20px 20px 10px;
  text-align: left;
  padding-left: 20px;
}

.scrollList {
  display: flex;
  overflow-x: auto;
  margin-top: 10px;
  padding: 20px;
  -webkit-overflow-scrolling: touch;
  scroll-snap-type: x mandatory;
  cursor: grab;
}

.card-row {
  display: flex;
  flex-wrap: nowrap;
}

.card-col {
  margin-right: 20px;
  scroll-snap-align: start;
}

.scrollList::-webkit-scrollbar {
  display: none;
}

.welcome-text {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  text-align: center;
}

.content-section {
  margin: 2rem 0;
  padding: 0 20px;
}

.scrollList {
  cursor: grab;
  scroll-behavior: smooth;
}

.scrollList:active {
  cursor: grabbing;
}

.scrollList::-webkit-scrollbar {
  display: none;
}

.scrollList {
  -ms-overflow-style: none;
  scrollbar-width: none;
}

.card-col {
  min-width: 300px;
  margin-right: 20px;
  transition: transform 0.2s;
}

.card-col:hover {
  transform: translateY(-5px);
}
</style>
