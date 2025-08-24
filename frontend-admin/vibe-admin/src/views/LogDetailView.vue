<template>
  <HeaderComponent />

  <v-btn
    class="go-back-btn"
    icon="mdi-arrow-left"
    @click="goBack"
  ></v-btn>

  <v-container class="log-container">
    <v-card class="pa-4" outlined>
      <div v-if="loading" class="loading-wrapper">
        <v-progress-circular indeterminate color="primary" size="64" />
      </div>
      <template v-else>
        <pre v-if="logs" class="log-content">{{ logs }}</pre>
        <div v-else class="no-logs-wrapper">
          <v-icon size="64" color="grey">mdi-text-box-remove-outline</v-icon>
          <h2 class="text-h6 mt-4">No log content found</h2>
        </div>
      </template>
    </v-card>
  </v-container>
</template>


<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userLogsStore } from '@/stores/logStore'
import HeaderComponent from '@/components/HeaderComponent.vue'

const router = useRouter()
const route = useRoute()
const logStore = userLogsStore()

const logId = computed(() => route.params.id)

const logs = ref('')
const loading = ref(true)

async function fetchLog() {
  try {
    const result = await logStore.getLogById(logId.value)
    logs.value = result
  } catch (error) {
    logs.value = 'Error loading log. ' + error
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLog()
})

const goBack = () => {
  router.push('/logs')
}
</script>
<style scoped>
.log-container {
  height: 80vh;  /* 80% of viewport height */
  width: 60vw;   /* 60% of viewport width */
  max-width: 1200px; /* Optional max-width */
  margin: 20px auto;
}

.v-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.log-content {
  flex: 1;
  font-family: monospace;
  white-space: pre-wrap;
  overflow: auto;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 4px;
}

.no-logs-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #6c757d;
}

.loading-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.go-back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 999;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
</style>
