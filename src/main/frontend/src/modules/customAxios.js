import axios from "axios";

// let tokenStealingAlertStatus = true;

export const axiosDefault = axios.create({
    baseURL: '/api',
    withCredentials: true,
})

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
        console.log('axios interceptor response');
       await errorHandling(err);
    }
)

const getToken = () => {

    const token = window.localStorage.getItem('Authorization');
    console.log('getToken : ', token);

    return token;
};


export const checkResponseMessageOk = (res) => {

    if(res.data.message === 'OK')
        return true;
    else{
        alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
        return false;
    }

}

export const errorHandling = (err) => {
    const errorStatus = err.response.status;
    const errorMessage = err.response.data.errorMessage;
    console.log('error response : ', err.response);
    if(errorStatus === 401){
        //토큰 만료 응답

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
    }else if(errorStatus === 800){
        //토큰 탈취 응답

        window.localStorage.removeItem('Authorization');
        alert('로그인 정보에 문제가 발생해 로그아웃됩니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
        window.location.href='/';
        /*if(tokenStealingAlertStatus){
            tokenStealingAlertStatus = false;
            window.localStorage.removeItem('Authorization');
            alert('로그인 정보에 문제가 발생해 로그아웃됩니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
            window.location.href='/';
        }*/
    }else if(errorStatus === 403 && errorMessage === 'AccessDeniedException') {
        window.location.href='/error';
    }
}