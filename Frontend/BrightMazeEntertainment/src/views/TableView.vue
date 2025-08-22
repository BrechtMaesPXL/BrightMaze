<script setup>
import TableComponent from '../components/TableComponent.vue'
import { RouterLink, useRoute } from 'vue-router'
import { computed, watch, onMounted, ref } from 'vue'
import { useFireStore } from '../stores/fires'

const fireStore = useFireStore()
const route = useRoute()
const pageInput = ref(1)

onMounted(() => {
  fireStore.fetchFires()
})

watch(
  () => fireStore.searchQuery,
  (newVal) => {
    fireStore.fetchFires(1, fireStore.pageSize)
  },
)

watch(
  () => fireStore.page,
  (newPage) => {
    pageInput.value = newPage
  },
)

const isTablePage = computed(() => route.path === '/table' || route.path === '/insights')
const totalPages = computed(() => Math.ceil(fireStore.total / fireStore.pageSize))

const pageSize = computed({
  get: () => fireStore.pageSize,
  set: (value) => fireStore.setPageSize(value),
})

function goToPage() {
  const page = parseInt(pageInput.value)
  if (page >= 1 && page <= totalPages.value) {
    fireStore.setPage(page)
  }
}
</script>

<template>
  <div class="table-view">
    <div class="header">
      <h2
      style="color: orange"
      >Overview wildfires</h2>

      <div class="controls">
        <v-select
          v-model="pageSize"
          :items="[5, 10, 15, 25, 50, 100, 150, 300]"
          label="Items per page"
          density="compact"
          style="min-width: 150px"
        ></v-select>
      </div>
      <RouterLink v-if="!isTablePage" to="/table">
        <v-btn color="primary" variant="outlined" text-color="white" class="see-all-btn">
          See All
        </v-btn>
      </RouterLink>
    </div>

    <div v-if="fireStore.isLoading" class="loading">Loading wildfires...</div>

    <div v-if="fireStore.error" class="error">
      {{ fireStore.error }}
    </div>

    <TableComponent v-else :fires="fireStore.filteredFires" />
    <div class="pagination">
      <v-btn
        style="background-color: orange; color: white"
        :disabled="fireStore.page === 1" @click="fireStore.setPage(fireStore.page - 1)">
        Previous
      </v-btn>
      <div class="page-control">
        <span>Page</span>
        <v-text-field
          v-model="pageInput"
          type="number"
          density="compact"
          variant="outlined"
          hide-details
          style="max-width: 60px; margin: 0 8px"
          min="1"
          :max="totalPages"
          @keyup.enter="goToPage"
        ></v-text-field>
        <span>of {{ totalPages }}</span>
      </div>
      <v-btn
        style="background-color: orange; color: white"
        :disabled="fireStore.page * fireStore.pageSize >= fireStore.total"
        @click="fireStore.setPage(fireStore.page + 1)"
      >
        Next
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
  justify-content: center;
  color: orange;
  margin-bottom: 15px;
}

.loading,
.error {
  padding: 20px;
  text-align: center;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.controls {
  display: flex;
  align-items: center;
  color: orange;
  margin-right: 15px;
}
.table-view {
  display: flex;
  flex-direction: column;
  max-height: 100vh;
}
.page-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.v-text-field {
  max-width: 100px;
}
</style>
