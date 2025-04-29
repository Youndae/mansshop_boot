import React, {useState, useEffect} from 'react';
import {useNavigate, useLocation} from 'react-router-dom';
import { useDispatch, useSelector } from "react-redux";
import { login } from '../../../features/member/memberSlice';


import {axiosDefault, errorHandling} from "../../../modules/customAxios";
import DefaultBtn from "../../ui/DefaultBtn";

import "../../css/member.css";

const loginFailMessage = 'BadCredentialsException';

/*
    로그인 페이지
    로컬 로그인, OAuth2 로그인 ( Google, Naver, kakao),
    아이디 찾기, 비밀번호 찾기, 회원가입 버튼
 */
function Login() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [userData, setUserData] = useState({
        userId: '',
        userPw: '',
    });
    const [loginRequestStatus, setLoginRequestStatus] = useState('');

    const navigate = useNavigate();
    const dispatch = useDispatch();
    const { state } = useLocation();

    //로그인된 사용자가 접근하는 경우 메인 페이지로 이동
    useEffect(() => {
        if(loginStatus)
            navigate('/');
    }, [loginStatus]);

    //여기서는 에러 핸들링이 BadCredentials를 제외하고는 없는데
    // 403 발생 시 Overlap Status를 수정해 출력하도록 해야 하므로 axiosDefault로 처리.
    const handleSubmit = async (e) => {
        e.preventDefault();

        if(userData.userId === ''){
            setLoginRequestStatus('id');
        }else if(userData.userPw === '') {
            setLoginRequestStatus('pw');
        }else{
            await axiosDefault.post(`member/login`, {
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

                    dispatch(login(res.data));

                    navigate(state);
                })
                .catch(err => {
                    const errStatus = err.response.status;
                    const errMessage = err.response.data.errorMessage;
                    //BadCredentialsException인 경우 403 반환으로 일치하지 않는 정보를 출력하기 위한 상태값 수정
                    if(errStatus === 403 && errMessage === loginFailMessage){
                        setLoginRequestStatus('fail');
                    }else {
                        errorHandling(err);
                    }
                })
        }
    }

    // 입력 이벤트
    const handleChange = (e) => {
        setUserData({
            ...userData,
            [e.target.name]: e.target.value,
        });
    }

    //회원가입 버튼 이벤트
    const handleJoin = () => {
        navigate('/join');
    }

    //아이디 찾기 버튼 이벤트
    const handleSearchId = () => {
        navigate('/search-id');
    }

    //비밀번호 찾기 버튼 이벤트
    const handleSearchPw = () => {
        navigate('/search-pw');
    }

    //OAuth2 로그인 버튼 이벤트
    //href 요청으로 인해 이전 페이지 이동을 state로 처리할 수 없으므로
    //sessionStorage에 담아뒀다가 oAuth 컴포넌트에서 꺼내서 처리.
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
                <LoginOverlap
                    status={loginRequestStatus}
                />
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

function LoginOverlap(props) {
    const { status } = props;
    let text = '';
    if(status === 'id'){
        text = '아이디를 입력해주세요';
    }else if(status === 'pw') {
        text = '비밀번호를 입력해주세요';
    }else if(status === 'fail') {
        text = '아이디 또는 비밀번호가 일치하지 않습니다.';
    }

    return (
        <div className="login-overlap">
            <span>{text}</span>
        </div>
    )
}

export default Login;