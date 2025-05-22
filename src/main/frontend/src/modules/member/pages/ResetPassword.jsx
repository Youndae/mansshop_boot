import React, { useState, useRef, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import { postResetPassword } from '../services/memberService';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import usePasswordValidator from '../../../common/hooks/usePasswordValidator';

import DefaultButton from '../../../common/components/DefaultButton';

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
        userPw: '',
        checkPassword: '',
    });

    const passwordElem = useRef(null);
    const checkElem = useRef(null);

    const navigate = useNavigate();

	const {
		pwCheck,
		verifyPw,
		pwCheckInfo,
		validateAndSyncPassword,
	} = usePasswordValidator();

    const userData = {
                        userId: state.userId,
                        certification: state.certification,
                    };

	useEffect(() => {
		if(state === undefined || state.certification === undefined)
			navigate('/');
	}, [state]);

	//비밀번호와 비밀번호 확인 입력 이벤트
	const handleOnChange = (e) => {
		const { name, value } = e.target;
		setPassword({
			...password,
			[name]: value,
		});

		validateAndSyncPassword(name, value, password);
	}

	// 비밀번호 수정 요청.
	// 완료되면 로그인 페이지로 이동
	const handleSubmit = async () => {
		if(pwCheck === 'valid' && password.checkPassword !== '' && verifyPw === 'valid'){
			await handleResetPassword();
		}else 
			passwordElem.current.focus();
	}

	const handleResetPassword = async () => {
		try {
			const res = await postResetPassword(userData.userId, userData.certification, password.userPw);

			if(res.data.message === RESPONSE_MESSAGE.OK){
				alert('비밀번호가 성공적으로 변경되었습니다.');
				navigate('/login');
			}else {
				alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
			}
		}catch(err){
			console.log(err);
			alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
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
                    <DefaultButton
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

    if(check === 'short') 
        text = '비밀번호는 8자리 이상이어야 합니다';
    else if(check === 'invalid') 
        text = '비밀번호는 영어, 특수문자, 숫자가 포함되어야 합니다';
    else if(check === 'empty') 
        text = '비밀번호를 입력하세요';
    else if(!status) 
        text = '비밀번호가 일치하지 않습니다';
    else if(check === 'valid') 
        text = '사용가능한 비밀번호 입니다';
    
    return (
        <span className={'reset-pw-overlap'}>{text}</span>
    )
}

export default ResetPassword;
