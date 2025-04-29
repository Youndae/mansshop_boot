import React, {useState, useRef, useEffect} from 'react';
import {useLocation, useNavigate} from "react-router-dom";

import {axiosDefault, checkResponseMessageOk} from "../../../modules/customAxios";

import DefaultBtn from "../../ui/DefaultBtn";

/*
    비밀번호 재설정 페이지
    인증 과정을 모두 거친 후 재설정할 수 있는 페이지.
    여기에서는 인증 과정에서 처리된 certification이 있어야만 처리되고
    수정 요청시에도 certification이 필요하기 때문에
    useEffect에서 state.certification이 없는 경우 메인페이지로 이동하도록 처리
 */
function ResetPassword() {
    const location = useLocation();
    const state = location.state;

    const [password, setPassword] = useState({
        password: '',
        checkPassword: '',
    });
    const [pwCheck, setPwCheck] = useState('');
    const [verifyPw, setVerifyPw] = useState(true);

    const passwordElem = useRef(null);
    const checkElem = useRef(null);

    const navigate = useNavigate();

    const pwPattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,16}$/;
    const userData = {
                        userId: state.userId,
                        certification: state.certification,
                    };

    useEffect(() => {
        if(state === undefined || state.certification === undefined)
            navigate('/');
    }, [state]);

    // 비밀번호와 재입력 input 입력 이벤트
    const handleOnChange = (e) => {
        const name = e.target.name;
        const value = e.target.value;
        setPassword({
            ...password,
            [e.target.name]: e.target.value,
        });

        if(name === 'password') {

            if(value.length < 8)
                setPwCheck('short');
            else if(!pwPattern.test(value)){
                setPwCheck('invalid');
            }else if(password.checkPassword !== '' && value !== password.checkPassword){
                setVerifyPw(false);
            }else if(value !== '' && value === password.checkPassword) {
                setVerifyPw(true);
                setPwCheck('valid');
            }else if(pwPattern.test(value)){
                setPwCheck('valid');
            }
        }else if(name === 'checkPassword') {
            if(value === password.password){
                setVerifyPw(true);
                setPwCheck('valid');
            }else {
                setVerifyPw(false);
            }
        }

    }

    //비밀번호 수정 요청. 완료되면 로그인 페이지로 이동
    const handleSubmit = async () => {

        if(pwCheck === 'valid' && password.checkPassword !== '' && verifyPw){
            await axiosDefault.patch(`member/reset-pw`, {
                userId: userData.userId,
                certification: userData.certification,
                userPw: password.password,
            })
                .then(res => {
                    if(checkResponseMessageOk(res)){
                        alert('비밀번호 변경이 완료되었습니다.');
                        navigate('/login');
                    }
                })
        }else if(password.password === ''){
            setPwCheck('empty');
            passwordElem.current.focus();
        }else if(password.checkPassword === ''){
            setPwCheck('empty');
            checkElem.current.focus();
        }else {
            passwordElem.current.focus();
        }
    }

    return (
        <div className="content login-content">
            <div className="login-header">
                <h1>비밀번호 재설정</h1>
            </div>
            <div className="search-id-form">
                <div className="form-group">
                    <label>비밀번호</label>
                    <input type={'password'} name={'password'} className={'form-control'} onChange={handleOnChange} value={password.password} ref={passwordElem}/>
                </div>
                <div className="form-group">
                    <label>비밀번호 확인</label>
                    <input type={'password'} name={'checkPassword'} className={'form-control'} onChange={handleOnChange} value={password.checkPassword} ref={checkElem}/>
                </div>
                <PwOverlap
                    check={pwCheck}
                    status={verifyPw}
                />
            </div>
            <div className="login-form-btn-area">
                <div className="login-btn">
                    <DefaultBtn
                        className={'reset-pw-btn'}
                        onClick={handleSubmit}
                        btnText={'재설정'}
                    />
                </div>
            </div>
        </div>
    )
}

function PwOverlap(props) {
    const { check, status } = props;

    let text = '';

    if(check === 'short') {
        text = '비밀번호는 8자리 이상이어야 합니다';
    }else if(check === 'invalid') {
        text = '비밀번호는 영어, 특수문자, 숫자가 포함되어야 합니다';
    }else if(check === 'empty') {
        text = '비밀번호를 입력하세요';
    }else if(!status) {
        text = '비밀번호가 일치하지 않습니다';
    }else if(check === 'valid') {
        text = '사용가능한 비밀번호 입니다';
    }

    return (
        <span className={'reset-pw-overlap'}>{text}</span>
    )
}

export default ResetPassword;