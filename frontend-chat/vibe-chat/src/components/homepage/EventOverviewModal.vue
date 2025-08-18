<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="isOpen" class="event-modal-overlay" @click.self="closeModal">
        <div class="event-modal-content">
          <button class="close-button" @click="closeModal">✕</button>
          <div class="event-overview-container">
            <h1>Event Overview</h1>
            <div v-if="events.length === 0" class="loading-message">Laden...</div>
            <div v-else class="event-grid">
              <div v-for="event in events" :key="event.id" class="event-card">
                <h2>{{ event.eventName }}</h2>
                <img
                  v-if="event.imageUrl"
                  :src="event.imageUrl"
                  alt="Event afbeelding"
                  class="event-image"
                  @error="handleImageError(event.id)"
                />
                <div v-else class="no-image">Geen afbeelding beschikbaar</div>
                <p>{{ event.eventDescription }}</p>
                <button class="details-button" @click="openDetailsModal(event)">Details</button>
              </div>
            </div>
          </div>
          <EventDetailsModal
            :is-open="isDetailsModalOpen"
            :event="selectedEvent"
            @update:is-open="isDetailsModalOpen = $event"
          />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import EventDetailsModal from './EventDetailsModal.vue';

const props = defineProps({
  isOpen: Boolean,
});

const emit = defineEmits(['close']);

const events = ref([]);
const isDetailsModalOpen = ref(false);
const selectedEvent = ref({});

const fetchEvents = async () => {
  try {
    const response = await fetch('http://localhost:8095/post/api/events', {
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
      const base64Image = item.base64Image;

      const imageUrl = base64Image
        ? getImageSrc(base64Image)
        : null;

      if (!imageUrl) {
        console.warn(`Geen afbeelding gevonden voor event: ${event.eventName}`);
      }

      return {
        id: event.id,
        eventName: event.eventName,
        startDate: event.startDate,
        endDate: event.endDate,
        location: event.location,
        eventDescription: event.eventDescription,
        imageUrl,
      };
    });
  } catch (error) {
    console.error('Fout bij ophalen events:', error);
  }
};

const openDetailsModal = (event) => {
  selectedEvent.value = event;
  isDetailsModalOpen.value = true;
};

const closeModal = () => {
  emit('close');
};

const handleImageError = (eventId) => {
  console.error(`Fout bij laden van afbeelding voor event ID: ${eventId}`);
};

const getImageSrc = (base64Image) => {
  const prefix = base64Image.startsWith('/9j') ? 'data:image/jpeg;base64,' : 'data:image/png;base64,';
  return `${prefix}${base64Image}`;
};

onMounted(fetchEvents);
</script>

<style scoped>
.event-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.event-modal-content {
  background: #fff;
  width: 90vw;
  max-width: 1000px;
  max-height: 80vh;
  border-radius: 10px;
  padding: 24px;
  position: relative;
  overflow-y: auto;
}

.close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #333;
  transition: color 0.2s ease;
}

.close-button:hover {
  color: #ff0000;
}

.event-overview-container {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
}

.loading-message {
  text-align: center;
  padding: 2rem;
  font-size: 1.2rem;
  color: #666;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  justify-content: center;
  max-width: 100%;
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
}

.event-card h2 {
  margin-bottom: 8px;
  font-size: 1.2rem;
}

.event-card p {
  margin-bottom: 16px;
  font-size: 0.9rem;
}

.event-image {
  width: 100%;
  max-width: 250px;
  height: auto;
  border-radius: 5px;
  margin-bottom: 10px;
}

.no-image {
  width: 100%;
  max-width: 250px;
  height: 120px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  margin-bottom: 10px;
  color: #666;
  font-size: 0.9rem;
}

.details-button {
  background-color: black;
  color: white;
  border: none;
  padding: 10px 15px;
  margin-top: auto;
  border-radius: 5px;
  cursor: pointer;
}

.details-button:hover {
  background-color: grey;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

.modal-fade-enter-to,
.modal-fade-leave-from {
  opacity: 1;
}

@media (max-width: 768px) {
  .event-modal-content {
    width: 95vw;
    max-height: 90vh;
    padding: 16px;
  }

  .event-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .event-card {
    padding: 12px;
  }

  .event-image,
  .no-image {
    max-width: 200px;
    height: auto;
  }

  .no-image {
    height: 100px;
  }
}

@media (max-width: 480px) {
  .event-modal-content {
    width: 100vw;
    max-height: 100vh;
    border-radius: 0;
    padding: 12px;
  }

  .close-button {
    font-size: 1.8rem;
    top: 0.5rem;
    right: 0.5rem;
  }

  .event-card h2 {
    font-size: 1rem;
  }

  .event-card p {
    font-size: 0.85rem;
  }

  .event-image,
  .no-image {
    max-width: 180px;
  }

  .no-image {
    height: 90px;
  }
}
</style>
