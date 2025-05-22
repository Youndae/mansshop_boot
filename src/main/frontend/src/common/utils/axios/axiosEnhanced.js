import axios from 'axios';
import { requestInterceptor, enhancedResponseInterceptor } from './axiosInterceptors';

// 대부분의 요청에 사용되는 axios
export const axiosEnhanced = axios.create({
    baseURL: '/api',
    withCredentials: true,
});

axiosEnhanced.interceptors.request.use(requestInterceptor);
axiosEnhanced.interceptors.response.use(
    res => res,
    enhancedResponseInterceptor
)