<template>
    <v-container>
      <v-alert v-if="errorMessage" type="error">{{ errorMessage }}</v-alert>

      <v-row justify="center">
        <v-col cols="12" sm="8" md="6">
          <v-card>
            <v-card-title class="headline"><h1>Login</h1></v-card-title>
            <v-card-text>
              <v-form @submit.prevent="login" >
                <v-text-field v-model="formData.email" label="Email" placeholder="Email" type="email" required></v-text-field>
                <v-text-field v-model="formData.password" label="Password" type="password" required></v-text-field>
                <v-btn type="submit">Login</v-btn>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>

<style scoped>


.v-btn, .v-card{
  border-radius: 4px
}

.v-btn{
  color: #00f0ff;
  background-color: #ff0099 !important;
  letter-spacing: 1px;

  box-shadow: 0 0 15px #ff33cc, 0 0 30px #ff0099;

}
.v-card{
  background-color: rgba(0, 0, 0, 0.85);
}
.v-text-field{
  color: #ff0099;
}
.v-card__title{
  text-transform: uppercase
}

.headline{
  text-align: center;
  margin: 1vw;
  color: #ff0099 !important;
  text-shadow: 0 0 8px #ff0099, 0 0 16px #ff0099, 0 0 24px #932f68;

}


</style>

<script>
  import axios from 'axios';

  const apiUrl = 'http://127.0.0.1:8000';

  export default {
    data() {
      return {
        formData: {
          email: localStorage.getItem('email') || '',
          password: '',
        },
        errorMessage: '',
      };
    },
    methods: {
      login() {
        axios.post(`${apiUrl}/users/login`, this.formData)
          .then(response => {

            localStorage.clear();
            localStorage.setItem('email', this.formData.email);
            localStorage.setItem('userId', response.data.user_id);
            localStorage.setItem('token', response.data.access_token);  // 🔐 BELANGRIJK

            console.log('Login succesvol, token opgeslagen:', response.data.access_token);


            this.$router.push({ name: 'home' });
          })
          .catch(error => {
            console.error('Login error:', error);
            this.errorMessage = error.response?.data?.detail || 'Login mislukt.';
          });
      },
    },
  };
</script>

