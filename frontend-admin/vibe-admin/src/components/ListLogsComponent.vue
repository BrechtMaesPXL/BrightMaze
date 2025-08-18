<template>
  <v-container>
    <v-data-table
      :headers="headers"
      :items="logStore.logs"
      item-value="fileName"
      fixed-header
      height="400"
      :items-per-page="5"
      class="log-table"
    >
      <template v-slot:item="{ item }">
        <tr class="log-row" @click="openLog($event, { item })">
          <td style="text-align: left">{{ item.fileName }}</td>
          <td style="text-align: left">{{ item.serviceName }}</td>
          <td style="text-align: left">{{ formatDate(item.dateCreated) }}</td>
        </tr>
      </template>
    </v-data-table>
  </v-container>
</template>

<script>
import { defineComponent } from "vue";
import { userLogsStore } from "@/stores/logStore";

export default defineComponent({
  name: "LogViewer",
  setup() {
    const logStore = userLogsStore();
    return { logStore };
  },
  data() {
    return {
      headers: [
        { title: "Log File", key: "fileName" },
        { title: "Service Name", key: "serviceName" },
        { title: "Date Created", key: "dateCreated" },
      ],
    };
  },
  methods: {
    async loadLogs() {
      await this.logStore.getAllLogs();
    },
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    openLog(event, { item }) {
      this.$router.push({
        name: "logsDetail",
        params: { id: item.id },
      });
    },
  },
  mounted() {
    this.loadLogs();
  },
});
</script>

<style scoped>
.log-table ::v-deep .v-data-table__tr {
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.log-table ::v-deep .v-data-table__tr:hover {
  background-color: #f5f5f5;
}

.log-row:hover {
  background-color: #f5f5f5;
}
</style>
