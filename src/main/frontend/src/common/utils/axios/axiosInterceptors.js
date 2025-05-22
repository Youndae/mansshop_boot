import { getToken, setToken, removeToken } from './tokenUtils';
import { axiosEnhanced } from './axiosEnhanced';

// 공통 Request Interceptor
export const requestInterceptor = (config) => {
    const token = getToken();
    if (token)
        config.headers['Authorization'] = token;
    return config;
};

// 401에 대한 처리만 하는 interceptor
export const simpleResponseInterceptor = async (error) => {
    if(error.response?.status === 401) {
        error.config._retry = true;

        const res = await axiosEnhanced.get('/reissue');
        const token = res.headers['authorization'];
        setToken(token);
        error.config.headers['Authorization'] = token;

        return axiosEnhanced(error.config);
    }

    return Promise.reject(error);
};

// 401, 403, 800 에 대한 처리만 하는 interceptor
export const enhancedResponseInterceptor = async (error) => {
    const status = error.response?.status;
    const message = error.response?.data?.errorMessage;

    if(status === 800) {
        removeToken();
        alert('로그인 정보에 문제가 발생해 로그아웃됩니다.\n문제가 계속된다면 관리자에게 문제해주세요.');
        window.location.href = '/';
    } else if(status === 403 || message === 'AccessDeniedException') {
        window.location.href = '/error';
    } else {
        return simpleResponseInterceptor(error);
    }

    return Promise.reject(error);
};

