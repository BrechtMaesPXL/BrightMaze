<template>
  <div class="event-details-container">
    <div class="event-details-content">
      <div class="event-text">
        <h1>{{ event.eventName }}</h1>
        <p><strong>Startdatum:</strong> {{ formatDate(event.startDate) }}</p>
        <p><strong>Einddatum:</strong> {{ formatDate(event.endDate) }}</p>
        <p><strong>Locatie:</strong> {{ event.location }}</p>
        <p><strong>Beschrijving:</strong> {{ event.eventDescription }}</p>

        <RouterLink to="/events">
          <button class="back-button">Back to Events</button>
        </RouterLink>
      </div>

      <img
        v-if="event.imageUrl"
        :src="event.imageUrl"
        alt="Event afbeelding"
        class="event-image"
      />
    </div>
  </div>
</template>



<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { EVENT_DETAILS_URL } from '@/config/api'

const event = ref({})
const route = useRoute()
const eventId = route.params.id

const fetchEventDetails = async () => {
  try {
    const response = await fetch(EVENT_DETAILS_URL(eventId))
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`)
    }

    const data = await response.json()

    // Netjes mappen zoals in de lijst
    event.value = {
      id: data.event.id,
      eventName: data.event.eventName,
      startDate: data.event.startDate,
      endDate: data.event.endDate,
      location: data.event.location,
      eventDescription: data.event.eventDescription,
      imageUrl: data.base64Image
        ? getImageSrc(data.base64Image)
        : null,
    }

    console.log('Event details:', event.value)
  } catch (error) {
    console.error('Fout bij ophalen event details:', error)
  }
}

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

// Detecteer jpg of png
const getImageSrc = (base64Image) => {
  const prefix = base64Image.startsWith('/9j') ? 'data:image/jpeg;base64,' : 'data:image/png;base64,'
  return `${prefix}${base64Image}`
}

onMounted(fetchEventDetails)
</script>


<style scoped>
.event-details-container {
  margin-top: 5%;
  margin-left: auto;
  margin-right: auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 1000px;
  color: black;
  line-height: 1.6;
}

.event-details-content {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.event-text {
  flex: 1;
}

.event-image {
  max-width: 70%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  object-fit: cover;
}

h1 {
  margin-bottom: 20px;
}

p {
  margin-bottom: 15px;
}

.back-button {
  background-color: black;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 30px;
}

.back-button:hover {
  background-color: gray;
}
</style>
