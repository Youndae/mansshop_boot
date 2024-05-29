import axios from "axios";

const default_header = {
    'Content-Type' : 'application/json',
    'Authorization' : `${localStorage.getItem('Authorization')}`,
}

export const imageDisplayAxios = axios.create({
    baseURL: '/api/',
    headers: default_header,
    withCredentials: true,
    responseType: 'blob',
});

export const defaultAxios = axios.create({
    baseURL: 'http://localhost:8080/api',
    headers: default_header,
    withCredentials: true,
})