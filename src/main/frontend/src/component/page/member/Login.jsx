import React, {useState} from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

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
                navigate('/member/mypage');
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

    return (
        <>
            <div className="login-content">
                <h1>로그인</h1>
            </div>
            <div className="login-form">
                <div className="form-group">
                    <input type={'text'} name={'userId'} placeholder={'아이디'} onChange={handleChange} autoFocus={true}/>
                </div>
                <div className="form-group">
                    <input type={'password'} name={'userPw'} onChange={handleChange} placeholder={'비밀번호'}/>
                </div>
                <div className="login-form-btn-area">
                    <div className="login-btn-area">
                        <button class={'loginBtn'} onClick={handleSubmit}>Login</button>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Login;