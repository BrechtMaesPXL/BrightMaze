<script setup>
import PieComponents from "@/components/graphs/PieComponents.vue";
import DropdownStatesComponent from "@/components/DropdownStatesComponent.vue";
import FirePreventionChecklist from "@/components/FirePreventionChecklist.vue";
import router from "@/router/index.js";
import HomeButtonComponent from "@/components/Menu/HomeButtonComponent.vue";


</script>

<template>
  <div class="page-wrapper">

      <v-row align="center" justify="start">
        <v-col cols="auto">
          <HomeButtonComponent/>
        </v-col>
        <v-col class="dropdown-col" cols="auto">
          <DropdownStatesComponent />
        </v-col>
      </v-row>

      <v-row class="custom-gap" justify="space-between" align="center" >
        <v-col cols="5">
          <FirePreventionChecklist />

        </v-col>
        <v-col cols="5" class="pie-fire-row">
          <PieComponents />

        </v-col>
      </v-row>
    </div>
</template>

<style scoped>

.custom-gap {
  gap: 16px;

  margin-left: 50px;
}

.dropdown-col {
  display: flex;
  margin-left: 300px;
  justify-content: center;
  align-items: center;
  width: 50%;
  margin-top: 35px;
}

.pie-fire-row {
  margin-top: 20px;
  margin-right: 50px;
}
</style>
