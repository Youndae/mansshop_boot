import React, {useEffect} from 'react';
import {useNavigate} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";

/*
    OAuth2 로그인의 경우 href 요청으로 처리되므로 응답을 받을 수 없음.
    href 요청이 전달되고 정상 처리되고 나면 백엔드에서 이 컴포넌트 라우팅 경로로 redirect 요청을 보냄.
    이때 임시 토큰이 응답 쿠키에 담겨 전달되고 해당 임시 토큰을 통해 정식 토큰 발급 요청을 보냄.
    토큰이 정상적으로 발급되면 AccessToken을 localStorage에 저장.
    이전 페이지 이동을 위해 sessionStorage에 저장된 prev를 꺼내 페이지 이동.

    이 페이지에서는 따로 UI를 보여주거나 할 것 없이 OAuth2 로그인의 마무리가 목적.
 */
function Oauth() {
    const navigate = useNavigate();

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