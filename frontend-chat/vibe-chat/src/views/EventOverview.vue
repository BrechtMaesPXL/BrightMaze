<template>

  <div class="event-overview-container">
    <RouterLink to="/">
      <v-btn
    class="home-btn"
    icon
    variant="plain"
    @click="goHome"
  >
    <v-icon icon="mdi-home" />
  </v-btn>
    </RouterLink>

    <h1>Event Overview</h1>
    <div v-if="events.length === 0">Laden...</div>
    <div v-else class="event-grid">
      <div v-for="event in events" :key="event.id" class="event-card">
        <h2>{{ event.eventName }}</h2>
        <p>{{ event.eventDescription }}</p>
        <p><v-icon class="date-icon" style="color: black;">mdi-calendar</v-icon>{{ formatDate(event.startDate) }}</p>
        <img v-if="event.imageUrl" :src="event.imageUrl" alt="Event afbeelding" class="event-image" />
        <RouterLink :to="'/events/' + event.id">
          <button class="details-button">Details</button>
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { SETTINGS_URL } from '@/config/api';
import { EVENTS_URL } from '@/config/api';


const events = ref([]);
const formatDate = (dateString) => {
  const date = new Date(dateString)
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }
  return date.toLocaleString('nl-BE', options).replace(',', '')
}
const fetchEvents = async () => {
  try {
    const response = await fetch(EVENTS_URL, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      mode: 'cors',
    });

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const data = await response.json();

    events.value = data.map(item => {
      const event = item.event;

      return {
        id: event.id,
        eventName: event.eventName,
        startDate: event.startDate,
        endDate: event.endDate,
        location: event.location,
        eventDescription: event.eventDescription,
        imageUrl: item.base64Image 
          ? `data:image/jpeg;base64,${item.base64Image}`
          : null, // of een placeholder URL
      };
    });
  } catch (error) {
    console.error('Fout bij ophalen events:', error);
  }
};

onMounted(fetchEvents);
</script>

<style scoped>

.event-overview-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}



.home-button {
  display: inline-block;
  margin-bottom: 20px;
  padding: 10px 20px;
  background-color: white;
  color: black;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
h1 {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 32px;
  text-align: center;
  color: #222;
}



.home-button:hover {
  background-color: grey;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  justify-content: center;
  max-width: 100%;
}
.home-btn {
  position: fixed !important;
  top: 20px !important;
  left: 20px !important;
  z-index: 999 !important;
  cursor: pointer !important;
  background: white;
  color: black;
}


.event-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border: 1px solid #ddd;
  padding: 16px;
  border-radius: 8px;
  background: white;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.1);
  color: black;
  text-align: center;
  height: 100%;
  align-items: center;
  transition: box-shadow 0.3s ease;
}
.event-card:hover {
  box-shadow: 0 15px 30px rgba(0,0,0,0.5);
}

.event-card h2 {
  margin-bottom: 8px;
}

.event-card p {
  margin-bottom: 16px;
}

.event-image {
  width: 300px;
  height: auto;
  border-radius: 5px;
  margin-bottom: 10px;
}

.details-button {
  background-color: black;
  color: white;
  border: none;
  padding: 10px 15px;
  margin-top: auto;
  border-radius: 5px;
  cursor: pointer;
  transition: box-shadow 0.3s ease;
}

.details-button:hover {
  box-shadow: 0 15px 30px rgba(0,0,0,0.2);
}
</style>
