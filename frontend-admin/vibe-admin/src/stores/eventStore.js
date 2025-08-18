import { defineStore } from 'pinia'
import axios from 'axios'
import { API_URL_events } from "@/config/api.js";

export const useEventStore = defineStore('events', {
  state: () => ({
    events: [],
    selectedEvent: null
  }),
  actions: {
    async fetchEvents() {
      try {
        const response = await axios.get(API_URL_events);

        this.events = response.data.map(item => {
          const event = item.event;

          return {
            id: event.id,
            eventName: event.eventName,
            startDate: event.startDate,
            endDate: event.endDate,
            location: event.location,
            eventDescription: event.eventDescription,
            base64Image: item.base64Image
          };
        });

      } catch (error) {
        console.error('Fout bij ophalen van events:', error);
      }
    },

    setSelectedEvent(event) {
        this.selectedEvent = event;
    },
    getSelectedEvent() {
        return this.selectedEvent;
    },
    async addEvent(eventData, file) {
      try {
        const formData = new FormData();
        formData.append("event", new Blob([JSON.stringify(eventData)], { type: "application/json" }));
          console.log("Adding event with data:", eventData, "and file:", file);
          formData.append("file", file);

        const response = await axios.post(
          API_URL_events,
          formData,
          {
            headers: { 'Content-Type': 'multipart/form-data' }
          }
        )

        await this.fetchEvents()
        return response.data
      } catch (error) {
        console.error('Fout bij toevoegen evenement:', error)
        throw error
      }
    },
    async deleteEvent(id) {
      try {
        const response = await axios.delete(
          API_URL_events+`/${id}`
        )

        await this.fetchEvents()
        return response.data
      } catch (error) {
        console.error('Fout bij verwijderen evenement:', error)
        throw error
      }
    },
    async updateEvent(id, eventData, file = null) {
      try {
        const formData = new FormData();
        formData.append('event', new Blob([JSON.stringify(eventData)], {
          type: 'application/json'
        }));


        if (file !== undefined) {
          formData.append('file', file);
        }

        const response = await axios.put(
          API_URL_events +`/${id}`,
          formData,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        );

        await this.fetchEvents();
        return response.data;
      } catch (error) {
        console.error('Fout bij updaten evenement:', error);
        throw error;
      }
    }
  }
})
