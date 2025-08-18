<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
        <div class="modal-content">
          <button class="close-button" @click="closeModal">✕</button>
          <div class="event-details-container">
            <div class="event-details-content">
              <div class="event-text">
                <h1>{{ event.eventName }}</h1>
                <p><strong>Startdatum:</strong> {{ formatDate(event.startDate) }}</p>
                <p><strong>Einddatum:</strong> {{ formatDate(event.endDate) }}</p>
                <p><strong>Locatie:</strong> {{ event.location }}</p>
                <p><strong>Beschrijving:</strong> {{ event.eventDescription }}</p>

                <button class="back-button" @click="closeModal">Back to Events</button>

                <button
                  v-if="isRouteEnabled"
                  class="route-button"
                  @click="toggleRoute"
                  :disabled="!currentBuilding || !event.location || isLoadingLocation"
                >
                  {{ isLoadingLocation ? 'Locatie laden...' : showRoute ? 'Sluit route' : 'Bekijk route' }}
                </button>
              </div>

              <div v-if="imageSrc" class="event-image-container">
                <img :src="imageSrc" alt="Event afbeelding" class="event-image" />
              </div>
              <div v-else class="no-image">Geen afbeelding beschikbaar</div>
            </div>

            <!-- Routeweergave -->
            <div v-if="showRoute">
              <div v-if="currentBuilding && event.location && currentBuilding !== event.location" class="route-container">
                <RouteComponent :start="currentBuilding" :end="event.location" />
              </div>
              <div v-else-if="currentBuilding === event.location" class="no-route">
                U bevindt zich al bij dit gebouw.
              </div>
              <div v-else class="no-route">
                Kan geen route tonen: locatiegegevens ontbreken of ongeldig.
              </div>
            </div>

          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useLocationStore } from './../../stores/currentBuildingStore.js';
import { useRouteSettingsStore } from './../../stores/RouteSettingsStore.js';
import RouteComponent from './RouteComponent.vue';

const locationStore = useLocationStore();
const routeSettingsStore = useRouteSettingsStore();

const { currentBuilding, isLoading: isLoadingLocation } = storeToRefs(locationStore);
const { isRouteEnabled } = storeToRefs(routeSettingsStore);

const showRoute = ref(false);

onMounted(async () => {
  await locationStore.fetchCurrentBuilding();
  await routeSettingsStore.fetchRouteSettings();

  console.log('Route settings direct na ophalen:', isRouteEnabled.value);
});

watch(isRouteEnabled, (newVal) => {
  console.log('Route setting opgehaald (watch):', newVal);
});

const props = defineProps({
  isOpen: Boolean,
  event: Object,
});

const emit = defineEmits(['update:isOpen']);

const closeModal = () => {
  emit('update:isOpen', false);
  showRoute.value = false;
};

const toggleRoute = () => {
  showRoute.value = !showRoute.value;
};

const formatDate = (dateString) => {
  if (!dateString) return 'Onbekend';
  const date = new Date(dateString);
  const options = {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  };
  return date.toLocaleString('nl-BE', options).replace(',', '');
};

const imageSrc = computed(() => props.event?.imageUrl || null);
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.modal-content {
  background: #fff;
  width: min(90vw, 800px);
  max-height: 75vh;
  border-radius: 10px;
  padding: 16px;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #333;
  transition: color 0.3s ease;
  z-index: 10;
}

.close-button:hover {
  color: #ff0000;
}

.event-details-container {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.event-details-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  width: 100%;
  gap: 20px;
}

.event-text {
  flex: 1;
  margin-right: 20px;
}

.event-image-container {
  max-width: 40%;
  flex-shrink: 0;
  margin-top: 60px;
}

.event-image {
  width: 100%;
  height: auto;
  border-radius: 6px;
  object-fit: cover;
}

.no-image,
.no-route {
  width: 100%;
  height: 200px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  color: #666;
  font-size: 0.9rem;
  margin-top: 20px;
}

h1 {
  margin-bottom: 10px;
}

p {
  margin-bottom: 15px;
}

.back-button,
.route-button {
  background-color: black;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 5px;
  cursor: pointer;
  margin-top: 10px;
  margin-right: 10px;
}

.route-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.back-button:hover,
.route-button:hover:not(:disabled) {
  background-color: gray;
}

.route-container {
  margin-top: 20px;
  width: 100%;
  height: 350px;
  border-radius: 10px;
  overflow: hidden;
}

.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
</style>
