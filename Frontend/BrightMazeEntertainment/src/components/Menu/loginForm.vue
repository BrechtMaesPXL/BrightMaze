<template>
  <div class="fill-height">
    <div class="login-container">
      <form class="login-form" @submit.prevent="submitForm">
        <v-alert v-if="error" type="error">{{ errorMessage }}</v-alert>
        <div class="label">E-mail</div>
        <v-text-field
          density="compact"
          placeholder="Voer je e-mailadres in"
          v-model="username"
          :rules="[required]"
          class="text-field"
          type="email"
          hide-details
        ></v-text-field>

        <div class="label">Password</div>
        <v-text-field
          v-model="password"
          density="compact"
          placeholder="Voer je wachtwoord in"
          :rules="[required]"
          :append-icon="visible ? 'mdi-eye' : 'mdi-eye-off'"
          :type="visible ? 'text' : 'password'"
          @click:append="visible = !visible"

          class="text-field password"
          hide-details
        >
        </v-text-field>

        <v-btn class="login-btn" size="small" variant="tonal" type="submit">
          Log In
        </v-btn>
      </form>
    </div>
  </div>
</template>

<script>
import { useUserStore } from '@/stores/userStore'

export default {
  data() {
    return {
      username: null,
      password:null,
      visible: false,
      error: false,
      errorMessage: ''
    }
  },
  methods: {
    async submitForm() {
      const responseStatus = await useUserStore().login(this.username, this.password)
      if (responseStatus === 200) {
        this.error = false
        this.$router.push('/home')
      } else if (responseStatus === 401) {
        this.errorMessage = 'Email of wachtwoord is onjuist'
        this.error = true
      } else {
        this.errorMessage = 'Er is iets misgegaan, probeer het later opnieuw'
        this.error = true
      }
    }
  }
}
</script>
import { mdiEyeOffOutline } from '@mdi/js';

<style scoped>
.fill-height {
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-container {
  text-align: center;
  width: 100%;
  max-width: 450px;
}

.login-form {
  padding: 2rem;
  border-radius: 8px;
  color: azure;
}

.label {
  margin-top: 1rem;
  font-weight: bold;
  text-align: center;
}

.text-field {
  border-radius: 10px;
  margin-bottom: 15px;
  background-color: azure;
  text-align: center;
  color: #2c2e33;
}

.password {
  margin-bottom: 20px;
}

.login-btn {
  margin-bottom: 2rem;
  max-width: 150px;
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  background: #2c2e33;
  color: azure;
}


</style>
