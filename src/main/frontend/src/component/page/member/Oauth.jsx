import React, {useEffect} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";

/*
    OAuth2 로그인의 경우 href 요청으로 인해 응답 body를 받을 수 없어서
    해당 컴포넌트를 거쳐 쿠키로 임시 토큰을 받은 뒤 이 임시 토큰으로 정식 토큰 발급 요청을 보낸다.
 */
function Oauth() {
    const [params] = useSearchParams();
    const navigate = useNavigate();
    // 추가적인 정보를 받기 위해 userType을 new, old 로 받아 조건에 따라 처리하도록 했었으나
    // 실제 운영하는 서비스의 경우 해당 Authorization 서버에서 정보를 받을 수 있으므로 생략.
    // const userType = params.get('type');

    useEffect(() => {
        tokenRequest();
    }, []);

    const tokenRequest = async () => {

        await axiosInstance.get('member/oAuth/token')
            .then(res => {
                const authorization = res.headers.get('authorization');
                window.localStorage.setItem('Authorization', authorization);

                const prevUrl = window.sessionStorage.getItem('prev');
                navigate(prevUrl);
            })
    }

    return null;
}

export default Oauth;