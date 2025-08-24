<template>
  <main>
    <HeaderComponent />

    <!-- Logout knop -->
    <v-btn class="logout-button" color="red" @click="logout" icon>
      <v-icon>mdi-logout</v-icon>
    </v-btn>

    <!-- Menu -->
    <v-container class="menu-wrapper">
      <div class="menu">
        <router-link
          v-for="item in buttons"
          :key="item.label"
          :to="item.link"
          class="menu-link"
        >
          <v-btn class="menu-btn" block>
            <v-icon class="menu-icon" start>{{ item.icon }}</v-icon>
            {{ item.label }}
          </v-btn>
        </router-link>
      </div>
    </v-container>
  </main>
</template>

<script setup>
import { useRouter } from "vue-router";
import HeaderComponent from "@/components/HeaderComponent.vue";
import VueCookies from "vue-cookies";

const router = useRouter();

const logout = () => {
  VueCookies.remove("userToken");
  router.push("/");
};

const buttons = [
  { label: "Events", icon: "mdi-calendar", link: "/events" },
  { label: "Logs", icon: "mdi-file-document", link: "/logs" },
  { label: "Instellingen", icon: "mdi-cog", link: "/instellingen" },
  { label: "Create Admins", icon: "mdi-account-plus", link: "/register" },
];
</script>

<style scoped>
.logout-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  background-color: white;
  color: red;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.menu-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
}

.menu {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
}

.menu-link {
  text-decoration: none;
  width: 100%;
  max-width: 400px;
}

.menu-btn {
  background: linear-gradient(to right, #1e1e1e, #333);
  color: white;
  font-size: 22px;
  height: 75px;
  width: 100%;
  border-radius: 20px;
  text-transform: none;
  transition: transform 0.2s, background-color 0.3s ease;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  letter-spacing: 0.5px;
}

.menu-btn:hover {
  background-color: #444;
  transform: translateY(-3px);
}

.menu-icon {
  margin-right: 16px;
  font-size: 30px;
}
</style>
