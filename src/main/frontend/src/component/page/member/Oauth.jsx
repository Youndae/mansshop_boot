import React, {useEffect} from 'react';
import {axiosInstance} from "../../../modules/customAxios";
import {useNavigate, useSearchParams} from "react-router-dom";

function Oauth() {
    const [params] = useSearchParams();
    const navigate = useNavigate();
    const userType = params.get('type');

    useEffect(() => {
        tokenRequest();
    }, []);

    const tokenRequest = async () => {


        await axiosInstance.get('oAuth/token')
            .then(res => {
                const authorization = res.headers.get('authorization');
                window.localStorage.setItem('Authorization', authorization);

                const prevUrl = window.sessionStorage.getItem('prev');

                if(userType === 'new'){
                    navigate('/oAuth/profile')
                }else {
                    navigate(prevUrl)
                }


            })
            .catch(err => {
                console.log('token request error : ', err);
            })
    }

    return null;
}

export default Oauth;