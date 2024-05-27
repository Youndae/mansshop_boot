import axios from "axios";

const default_header = {
    'Content-Type' : 'application/json',
}

export const imageDisplayAxios = axios.create({
    headers: default_header,
    withCredentials: true,
    responseType: 'blob',
});