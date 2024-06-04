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
    baseURL: '/api/',
    headers: default_header,
    withCredentials: true,
})

export const checkUserStatus = async () => {
    return await axiosDefault.get(`member/check-login`)
        .catch(err => {
            console.error('loginCheck Error : ', err);
        })
}

export const getAuthorization = () => {
    return window.localStorage.getItem('Authorization');
}

export const axiosDisplay = axios.create({
    baseURL: '/api',
    withCredentials: true,
    responseType: "blob"
})

axiosDisplay.interceptors.request.use(
    (config) => {
        const accessToken = getToken();

        config.headers['Content-Type'] = 'application/json';
        config.headers['Authorization'] = `${accessToken}`;

        console.log(`axios display interceptor request config : ${accessToken}`)

        return config;
    },
    (error) => {
        console.log('axios interceptor Error : ', error);
    }
)

export const axiosDefault = axios.create({
    baseURL: '/api',
    withCredentials: true,
})

axiosDefault.interceptors.request.use(
    (config) => {
        const accessToken = getToken();

        config.headers['Content-Type'] = 'application/json';
        config.headers['Authorization'] = `${accessToken}`;

        console.log(`axiosDefault interceptor request config : ${accessToken}`);

        return config;
    },
    (error) => {
        console.log('axios interceptor Error : ', error);
    }
)

axiosDefault.interceptors.response.use(
    (res) => {
        console.log('axiosDefault interceptor response then res : ', res);
        return res;
    },
    async (err) => {
        if(err.response.status === 401){
            // err.config._retry = true;
            console.log('axios default response status is 401');

            return axiosDefault.get(`reissue`)
                .then(res => {
                    console.log('token Test response success : ', res);
                    window.localStorage.removeItem('Authorization');

                    const authorization = res.headers['authorization'];
                    console.log('authorization : ', authorization);
                    window.localStorage.setItem('Authorization', authorization);

                    return axiosDefault(err.config);
                })
                .catch(error => {
                    console.log('token test error : ', error);
                })
        }
    }
)

const getToken = () => {
    return window.localStorage.getItem('Authorization');
};