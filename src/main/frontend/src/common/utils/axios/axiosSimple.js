import axios from 'axios';
import { requestInterceptor, simpleResponseInterceptor } from './axiosInterceptors';

// AccessDenied, TokenStealing(800)이 절대 발생할 수 없다는 보장이 있으면서,
// 403에 대한 처리를 직접 작성하는 경우 사용되는 axios
// ex) login에서 403(BadCredentials) 발생 시 오류로 분류할게 아니라 잘못 입력되었다고 overlap 출력을 해야 하는 경우
export const axiosSimple = axios.create({
    baseURL: '/api',
    withCredentials: true,
});

axiosSimple.interceptors.request.use(requestInterceptor);
axiosSimple.interceptors.response.use(
    res => res,
    err => simpleResponseInterceptor(err)
);