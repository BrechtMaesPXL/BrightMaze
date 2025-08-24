import { defineStore } from 'pinia'
import axios from 'axios'
import VueCookies from 'vue-cookies'

import { API_URL_ADMIN } from "@/config/api.js";

export const useUserStore = defineStore('user', () => {

  const login = async (email, password) => {
    try {
      const user = {
        email: email,
        password: password
      }
      const response = await axios.post(API_URL_ADMIN + 'login', user);


      if (response.status === 200) {
        VueCookies.set('userToken', JSON.stringify(response.data.token));
        localStorage.setItem('token', JSON.stringify(response.data.token));

      }

      return response.status
    } catch (error) {
      return error.response.status
    }
  }
  const register = async (email, firstname, lastname, password) => {
    try {
      const user = {
        email: email,
        firstName: firstname,
        lastName: lastname,
        password: password
      }
      const response = await axios.post( API_URL_ADMIN + 'register', user)
      return response.status
    } catch (error) {
      return error.response.status
    }


  }


  return { login, register };
});
