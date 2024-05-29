import React, {useState} from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

import "../../css/member.css";

function Login() {
    const [userData, setUserData] = useState({
        userId: '',
        userPw: '',
    });

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        console.log('userId : ', userData.userId);
        console.log('userPw : ', userData.userPw);

        await axios.post(`/api/login`, {
            userId: userData.userId,
            userPw: userData.userPw,
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
            , withCredentials: true
        })
            .then(res => {
                console.log("login res : ", res);
                const authorization = res.headers.get('authorization');
                window.localStorage.setItem('Authorization', authorization);
                // navigate('/member/mypage');
                // navigate('/');
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

        console.log('handleOAuth client name : ', oAuthClient);

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
                        <button className={'login-btn'} onClick={handleSubmit}>Login</button>
                    </div>
                    <div className="join-search-area">
                        <button className={'join-btn'} onClick={handleJoin}>회원가입</button>
                        <button className={'search-id-btn'} onClick={handleSearchId}>아이디 찾기</button>
                        <button className={'search-pw-btn'} onClick={handleSearchPw}>비밀번호 찾기</button>
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