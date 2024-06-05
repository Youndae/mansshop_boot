import axios from "axios";

let tokenStealingAlertStatus = true;

export const axiosInstance = axios.create({
    baseURL: '/api',
    withCredentials: true,
})

axiosInstance.interceptors.request.use(
    (config) => {
        const accessToken = getToken();

        config.headers['Authorization'] = `${accessToken}`;

        return config;
    },
    (error) => {
        console.log('axios interceptor Error : ', error);
    }
)

axiosInstance.interceptors.response.use(
    (res) => {
        return res;
    },
    async (err) => {
        if(err.response.status === 401){
            // err.config._retry = true;
            console.log('axios default response status is 401');

            return axiosInstance.get(`reissue`)
                .then(res => {
                    window.localStorage.removeItem('Authorization');

                    const authorization = res.headers['authorization'];
                    window.localStorage.setItem('Authorization', authorization);

                    return axiosInstance(err.config);
                })
                .catch(error => {
                    console.log('token test error : ', error);
                })
        }else if(err.response.status === 800){
            if(tokenStealingAlertStatus){
                tokenStealingAlertStatus = false;
                window.localStorage.removeItem('Authorization');
                alert('로그인 정보에 문제가 발생해 로그아웃됩니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
                window.location.href='/';
            }
        }
    }
)

const getToken = () => {
    return window.localStorage.getItem('Authorization');
};