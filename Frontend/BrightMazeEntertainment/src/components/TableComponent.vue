<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  fires: {
    type: Array,
    default: () => [],
  },
});

const sortConfig = ref({
  key: '',
  direction: 'asc',
});

const hoveredColumn = ref('');

const headers = [
  { title: 'Name', key: 'name' },
  { title: 'Year', key: 'year' },
  { title: 'State', key: 'state' },
  { title: 'County', key: 'city' },
  { title: 'Size (ha)', key: 'size' },
];

const getSizeColor = (size) => {
  if (size > 400000) return '#ffcccc';
  if (size > 100000) return '#fff3cc';
  return '#e6ffe6';
};

const sortedFires = computed(() => {
  if (!sortConfig.value.key) return props.fires;

  return [...props.fires].sort((a, b) => {
    const aValue = a[sortConfig.value.key];
    const bValue = b[sortConfig.value.key];

    if (typeof aValue === 'number' && typeof bValue === 'number') {
      return sortConfig.value.direction === 'asc' ? aValue - bValue : bValue - aValue;
    } else {
      return sortConfig.value.direction === 'asc'
        ? String(aValue).localeCompare(String(bValue))
        : String(bValue).localeCompare(String(aValue));
    }
  });
});

const handleSort = (key) => {
  if (sortConfig.value.key === key) {
    sortConfig.value.direction = sortConfig.value.direction === 'asc' ? 'desc' : 'asc';
  } else {
    sortConfig.value.key = key;
    sortConfig.value.direction = 'asc';
  }
};
</script>

<template>
  <div class="table-container">
    <v-table class="styled-table" fixed-header>
      <thead>
        <tr>
          <th v-for="header in headers" :key="header.key" class="table-header" @mouseenter="hoveredColumn = header.key"
            @mouseleave="hoveredColumn = ''" @click="handleSort(header.key)">
            {{ header.title }}
            <span v-if="hoveredColumn === header.key || sortConfig.key === header.key">
              {{ sortConfig.key === header.key ? (sortConfig.direction === 'asc' ? '↑' : '↓') : '⇅' }}
            </span>
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="fire in sortedFires" :key="fire.name" class="table-row"
          :style="{ backgroundColor: getSizeColor(fire.size) }">
          <td>{{ fire.name }}</td>
          <td>{{ fire.year }}</td>
          <td>{{ fire.state }}</td>
          <td>{{ fire.city }}</td>
          <td>{{ fire.size }}</td>
        </tr>
      </tbody>
    </v-table>
  </div>
</template>

<style scoped>
html,
body {
  height: 100%;
  margin: 0;
}

.table-container {
  height: 100vh;
  width: 100vw;
  overflow: auto;
}

.styled-table {
  width: 100%;
  height: 100%;
}

.styled-table thead th {
  background-color: orange !important;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.table-container {
  overflow-y: auto;
  max-height: 75%;
  position: relative;
}

.table-header {
  color: white;
  font-weight: bold;
  padding: 12px;
  text-align: left;
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s ease;
}

.table-header:hover {
  background-color: #ffa500;
}

.table-header span {
  margin-left: 5px;
  font-size: 14px;
  opacity: 0.7;
}
</style>
