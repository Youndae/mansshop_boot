import React, {useEffect} from 'react';
import {defaultAxios} from "../../../module/customAxios";
import {useNavigate, useSearchParams} from "react-router-dom";

function Oauth() {
    const [params] = useSearchParams();
    const navigate = useNavigate();
    const userType = params.get('type');

    useEffect(() => {
        tokenRequest();
    }, []);

    const tokenRequest = async () => {


        await defaultAxios.get('oAuth/token')
            .then(res => {
                const authorization = res.headers.get('authorization');
                window.localStorage.setItem('Authorization', authorization);

                const prevUrl = window.sessionStorage.getItem('prev');
                // navigate(prevUrl);

                if(userType === 'new'){
                    //navigate('/oAuth/profile')
                }else {
                    //navigate(prev)
                }


            })
            .catch(err => {
                console.log('token request error : ', err);
            })
    }

    return null;
}

export default Oauth;