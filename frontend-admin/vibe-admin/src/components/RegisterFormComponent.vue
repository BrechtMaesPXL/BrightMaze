<template>
  <v-form ref="form">
    <v-card class="mx-auto" max-width="600">
      <v-card-title class="bg-primary text-white">
        <h2>Admin Registratie</h2>
      </v-card-title>

      <v-card-text>
        <!-- Error Alert -->
        <v-alert v-if="error" type="error" class="mb-4">{{ errorMessage }}</v-alert>

        <!-- Email Field -->
        <v-text-field
          v-model="username"
          label="E-mail*"
          placeholder="Voer je e-mailadres in"
          :rules="[
            v => !!v || 'E-mail is verplicht',
            v => /.+@.+\..+/.test(v) || 'E-mail moet geldig zijn'
          ]"
          outlined
          class="mb-4"
          type="email"
        ></v-text-field>

        <!-- Firstname Field -->
        <v-text-field
          v-model="firstname"
          label="Voornaam*"
          placeholder="Voornaam"
          :rules="[
            v => !!v || 'Voornaam is verplicht',
            v => (v && v.length >= 3) || 'Voornaam moet minstens 3 karakters zijn',
            v => (v && v.length <= 21) || 'Voornaam mag maximaal 21 karakters zijn'
          ]"
          outlined
          class="mb-4"
        ></v-text-field>

        <!-- Lastname Field -->
        <v-text-field
          v-model="lastname"
          label="Achternaam*"
          placeholder="Achternaam"
          :rules="[
            v => !!v || 'Achternaam is verplicht',
            v => (v && v.length >= 3) || 'Achternaam moet minstens 3 karakters zijn',
            v => (v && v.length <= 21) || 'Achternaam mag maximaal 21 karakters zijn'
          ]"
          outlined
          class="mb-4"
        ></v-text-field>

        <!-- Password Field -->
        <v-text-field
          v-model="password"
          label="Wachtwoord*"
          placeholder="Voer je wachtwoord in"
          :rules="[
            v => !!v || 'Wachtwoord is verplicht',
            v => (v && v.length >= 6) || 'Wachtwoord moet minstens 6 karakters zijn'
          ]"
          :append-icon="visible ? 'mdi-eye' : 'mdi-eye-off'"
          :type="visible ? 'text' : 'password'"
          @click:append="visible = !visible"
          outlined
          class="mb-4"
        ></v-text-field>

        <!-- Action Buttons -->
        <v-card-actions class="justify-end">
          <v-btn
            color="primary"
            @click="submitForm"
            :loading="loading"
          >
            Registreer
          </v-btn>
          <v-btn
            color="error"
            @click="resetForm"
          >
            Wissen
          </v-btn>
        </v-card-actions>
      </v-card-text>
    </v-card>

    <div class="snackbar-container">
      <v-snackbar
        v-model="snackbar"
        color="success"
        timeout="3000"
        elevation="2"
        rounded
      >
        Registratie succesvol!
      </v-snackbar>
    </div>
  </v-form>
</template>

<script>
import { useUserStore } from '@/stores/userStore'

export default {
  data() {
    return {
      username: null,
      firstname: null,
      lastname: null,
      password: null,
      visible: false,
      error: false,
      errorMessage: '',
      loading: false,
      snackbar: false,
    }
  },
  methods: {
    async submitForm() {
      const valid = this.$refs.form.validate();
      if (!valid) return;

      this.loading = true;
      this.error = false;

      try {
        const responseStatus = await useUserStore().register(
          this.username,
          this.firstname,
          this.lastname,
          this.password,
        );

        if (responseStatus === 200) {
          this.error = false;
          this.snackbar = true;
          this.resetForm();
        } else if (responseStatus === 401) {
          this.errorMessage = 'Registratie mislukt';
          this.error = true;
        } else {
          this.errorMessage = 'Er is iets misgegaan, probeer het later opnieuw';
          this.error = true;
        }
      } catch (e) {
        this.errorMessage = 'Er is iets misgegaan, probeer het later opnieuw';
        this.error = true;
      } finally {
        this.loading = false;
      }
    },
    resetForm() {
      this.username = null;
      this.firstname = null;
      this.lastname = null;
      this.password = null;
      this.error = false;
      this.$refs.form.resetValidation();
    }
  }
}
</script>

<style scoped>
.v-card {
  margin-top: 2rem;
  border-radius: 10px;
  color: white;
  transition: box-shadow 0.3s ease;
  margin-bottom: 2rem;
  background-color: rgb(0,85,169) !important;
}

.v-card:hover {
  box-shadow: 0 10px 20px rgba(0,0,0,0.2) !important;
}

.v-card-title {
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
}

.v-card-text {
  margin-top: 10px;
}

.snackbar-container {
  display: flex;
  justify-content: center;
  margin-top: 1rem;
}

:deep(.v-text-field__slot input),
:deep(.v-label),
:deep(.v-icon) {
  color: rgba(255, 255, 255, 0.6) !important;
}

:deep(.v-input__slot) {
  border-color: rgba(255, 255, 255, 0.3) !important;
}

:deep(.v-input--is-focused .v-text-field__slot input),
:deep(.v-input--is-focused .v-label),
:deep(.v-input--is-focused .v-icon) {
  color: white !important;
}

:deep(.v-input--is-focused .v-input__slot) {
  border-color: white !important;
}

.v-btn {
  margin-left: 8px;
  background-color: white;
}
</style>
