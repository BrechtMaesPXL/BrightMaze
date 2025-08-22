<template>
    <v-container>
      <v-alert v-if="errorMessage" type="error">{{ errorMessage }}</v-alert>

      <v-row justify="center">
        <v-col cols="12" sm="8" md="6">
          <v-card>
            <v-card-title class="headline"><h1>Register</h1></v-card-title>
            <v-card-text>
              <v-form @submit.prevent="handleRegistration">
                <v-text-field v-model="newProfile.username" label="Username" required></v-text-field>
                <v-text-field v-model="newProfile.email" label="Email" type="email" required></v-text-field>
                <v-text-field v-model="newProfile.password" label="Password" type="password" required></v-text-field>
                <v-text-field v-model="newProfile.passwordVerification" label="Password Verification" type="password" required></v-text-field>
                <v-btn type="submit">Register</v-btn>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </template>

  <style scoped>


    .headline{
      text-align: center;
      margin: 1vw;
      color: #e98616;
    }
    .v-btn{
      color: white;
      background-color: #e98616;
    }
    .v-card{
      background-color: rgba(0, 0, 0, 0.75);
    }
    .v-text-field{
      color: #e98616;
    }
  </style>

  <script>

import axios from 'axios';
const apiUrl = 'http://127.0.0.1:8000';

export default{
    data(){
        return{
            newProfile: {
                username: '',
                email: '',
                password: '',
            },
            passwordVerification: '',
            errorMessage: '',
        }
    },
    methods: {
      handleRegistration() {
      if (this.newProfile.password === this.newProfile.passwordVerification) {
            this.register();
        } else {
            console.log("Passwords are not the same")
            this.errorMessage = "Passwords are not the same"
        }

    },
    register() {
      axios.post(`${apiUrl}/users`, this.newProfile)
      .then((response) => {
        console.log("profile is being saved", response.data)
        const email = this.newProfile.email
        localStorage.setItem('email', email)
        this.$router.go()
      })
      .catch((error) => {
        if (error.response && error.response.status === 400) {
              this.errorMessage = error.response.data.message;
            } else {
              this.errorMessage = 'There was an error during registration. Please try again later :)';
            }
      })
    }
  }
}




</script>
