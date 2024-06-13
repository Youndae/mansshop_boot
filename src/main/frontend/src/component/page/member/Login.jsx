import React, {useState} from 'react';
import {useNavigate, useLocation} from 'react-router-dom';
import { useDispatch } from "react-redux";

import "../../css/member.css";
import {axiosInstance} from "../../../modules/customAxios";
import {setMember} from "../../../modules/member";
import DefaultBtn from "../../ui/DefaultBtn";

function Login() {
    const [userData, setUserData] = useState({
        userId: '',
        userPw: '',
    });

    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { state } = useLocation();

    const handleSubmit = async (e) => {
        e.preventDefault();

        await axiosInstance.post(`member/login`, {
            userId: userData.userId,
            userPw: userData.userPw,
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        })
            .then(res => {
                const authorization = res.headers['authorization'];
                window.localStorage.setItem('Authorization', authorization);

                dispatch(setMember(res.data.userStatus));

                navigate(state);
            })
            .catch(err => {
                console.error('login err : ', err);
            })
    }

    const handleChange = (e) => {
        setUserData({
            ...userData,
            [e.target.name]: e.target.value,
        });
    }

    const handleJoin = () => {
        navigate('/join');
    }

    const handleSearchId = () => {
        navigate('/searchId');
    }

    const handleSearchPw = () => {
        navigate('/searchPw');
    }

    const handleOAuth = (e) => {
        const oAuthClient = e.target.name;

        window.sessionStorage.setItem('prev', state.toString());

        if(oAuthClient === 'google')
            window.location.href='http://localhost:8080/oauth2/authorization/google';
        else if(oAuthClient === 'naver')
            window.location.href='http://localhost:8080/oauth2/authorization/naver';
        else if(oAuthClient === 'kakao')
            window.location.href='http://localhost:8080/oauth2/authorization/kakao';
    }

    return (
        <div className="content login-content">
            <div className="login-header">
                <h1>로그인</h1>
            </div>
            <div className="login-form">
                <div className="form-group">
                    <input type="text" className="form-control" name={'userId'} placeholder={'아이디'} onChange={handleChange} value={userData.userId} autoFocus={true}/>
                </div>
                <div className="form-group">
                    <input type="password" className="form-control" name={'userPw'} placeholder={'비밀번호'} onChange={handleChange} value={userData.userPw}/>
                </div>
                <div className="login-form-btn-area">
                    <div className="login-btn">
                        <DefaultBtn className={'login-btn'} onClick={handleSubmit} btnText={'Login'}/>
                    </div>
                    <div className="join-search-area">
                        <DefaultBtn className={'join-btn'} onClick={handleJoin} btnText={'회원가입'}/>
                        <DefaultBtn className={'search-id-btn'} onClick={handleSearchId} btnText={'아이디 찾기'}/>
                        <DefaultBtn className={'search-pw-btn'} onClick={handleSearchPw} btnText={'비밀번호 찾기'}/>
                    </div>
                    <div className="oauth-area">
                        <div className="oauth-btn">
                            <div className="oauth-btn">
                                <label htmlFor={'oauth-google'}>
                                    <img src={`${process.env.PUBLIC_URL}/image/web_light_sq_ctn@1x.png`}/>
                                </label>
                            </div>
                            <div className="oauth-btn">
                            <label htmlFor={'oauth-naver'}>
                                <img src={`${process.env.PUBLIC_URL}/image/btnG_official.png`}/>
                            </label>
                            </div>
                            <div className="oauth-btn">
                                <label htmlFor={'oauth-kakao'}>
                                    <img src={`${process.env.PUBLIC_URL}/image/kakao_login_medium_narrow.png`}/>
                                </label>
                            </div>
                        </div>
                        <button id={'oauth-google'} name={'google'} onClick={handleOAuth} style={{display: 'none'}}/>
                        <button id={'oauth-naver'} name={'naver'} onClick={handleOAuth} style={{display: 'none'}}/>
                        <button id={'oauth-kakao'} name={'kakao'} onClick={handleOAuth} style={{display: 'none'}}/>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login;