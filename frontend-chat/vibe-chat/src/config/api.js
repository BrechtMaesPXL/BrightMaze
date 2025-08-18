import { baseApiUrl } from "mapbox-gl";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;


export const CHATBOT_API_URL = `${BASE_URL}post/api/chat`;
export const CREATE_SESSION_URL = `${BASE_URL}post/api/chat/create`;
export const EVENTS_URL = `${BASE_URL}post/api/events`;
export const SETTINGS_URL = `${BASE_URL}admin/api/settings`;
export const EVENT_DETAILS_URL = (eventId) => `${BASE_URL}post/api/events/${eventId}/withImage`;
export const LOCATION_API_URL = `${BASE_URL}admin/api/settings/current-location`;
