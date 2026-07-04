import axios from 'axios';

// Base URL can be configured via environment variables.
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Generic error handler
const handleError = (error: any) => {
  if (error.response) {
    // The request was made and the server responded with a status code
    // that falls out of the range of 2xx
    return Promise.reject(error.response.data || new Error(`Error: ${error.response.status}`));
  } else if (error.request) {
    // The request was made but no response was received
    return Promise.reject(new Error('No response received from the server. Please check your network connection.'));
  } else {
    // Something happened in setting up the request that triggered an Error
    return Promise.reject(new Error(error.message));
  }
};

export const api = {
  createMeeting: (data: any) => apiClient.post('/meetings', data).catch(handleError),
  getMeeting: (id: string) => apiClient.get(`/meetings/${id}`).catch(handleError),
  addScheduleRule: (meetingId: string, data: any) => apiClient.post(`/schedule-rules`, data).catch(handleError),
  addPreferenceRule: (meetingId: string, data: any) => apiClient.post(`/preference-rules`, data).catch(handleError),
  negotiateMeeting: (meetingId: string, data: any) => apiClient.post(`/meetings/${meetingId}/negotiate`, data).catch(handleError),
};
