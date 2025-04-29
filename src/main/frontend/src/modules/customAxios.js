import axios from "axios";

// 401에 대한 처리만 하는 기본 Axios
// 403, 800 미발생이 보장되거나, 따로 관리해야 하는 요청에서만 사용
export const axiosDefault = axios.create({
    baseURL: '/api',
    withCredentials: true,
})

axiosDefault.interceptors.request.use(
    (config) => {
        const accessToken = getToken();

        config.headers['Authorization'] = `${accessToken}`;

        return config;
    }
)

axiosDefault.interceptors.response.use(
    (res) => {
        return res;
    },
    async (err) => {
        const errorStatus = err.response.status;

        if(errorStatus === 401){
            //토큰 만료 응답
            // err.config._retry = true;

            return axiosDefault.get(`reissue`)
                .then(res => {
                    window.localStorage.removeItem('Authorization');

                    const authorization = res.headers['authorization'];
                    window.localStorage.setItem('Authorization', authorization);

                    return axiosInstance(err.config);
                })
        }
    }
)

// 401, 800, 403에 대응하는 Axios
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
       await errorHandling(err);
    }
)

export const getToken = () => window.localStorage.getItem('Authorization');


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
    if(errorStatus === 401){
        //토큰 만료 응답
        // err.config._retry = true;

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
    }else if(errorStatus === 403 || errorMessage === 'AccessDeniedException') {
        window.location.href='/error';
    }
}