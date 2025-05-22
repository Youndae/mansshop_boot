import React, { useRef, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { getSearchPw, postCertification } from '../services/memberService';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import { PATTERNS } from '../../../common/constants/patterns';

import DefaultButton from '../../../common/components/DefaultButton';

// 비밀번호 찾기 페이지
function SearchPw() {
	const [data, setData] = useState({
        userId: '',
        username: '',
        email: '',
        mailSuffix: '',
    });
    const [overlapStatus, setOverlapStatus] = useState('');
    const [certificationStatus, setCertificationStatus] = useState(false);
    const [certification, setCertification] = useState('');
    const [timer, setTimer] = useState(300);

    const userIdElem = useRef(null);
    const nameElem = useRef(null);
    const emailElem = useRef(null);

    const navigate = useNavigate();

    const emailPattern = PATTERNS.EMAIL;

	useEffect(() => {
		let interval;

		if(certificationStatus){
			interval = setInterval(() => {
                setTimer((prevTimer) => {
                    if(prevTimer <= 1) {
                        clearInterval(interval);
                        setCertificationStatus(false);
                        return 0;
                    }
                    return prevTimer - 1;
                });
            }, 1000);
		}

		return () => clearInterval(interval);
	}, [certificationStatus]);

	//input 입력 이벤트
	const handleOnChange = (e) => {
		const { name, value } = e.target;
		setData({
			...data,
			[name]: value,
		});
	}

	// 사용자 이름 입력 여부 확인. 입력하지 않았다면 focus
	const checkUserName = () => {
		const name = data.username;

		if(name === ''){
			setOverlapStatus('name');
			nameElem.current.focus();
		}else
			return true;
	}

	const getEmail = () => data.email + '@' + data.mailSuffix;

	//비밀번호 찾기 요청
	//정상적으로 입력했다면 요청 전송
	//정상 응답이 반환되면 인증번호 입력 폼 출력 이후 interval 시작
	const handleSubmit = async () => {
		if(checkUserName() && validateData())
			await searchPwRequest();
		

	}

	const validateData = () => {
		if(data.userId === ''){
			setOverlapStatus('id');
			userIdElem.current.focus();
			return false;
		}else if(data.email === '' || data.mailSuffix === ''){
			setOverlapStatus('email');
			emailElem.current.focus();
			return false;
		}else if(!emailPattern.test(getEmail())){
			setOverlapStatus('email invalid');
			emailElem.current.focus();
			return false;
		}else
			return true;
	}

	const searchPwRequest = async () => {
		try {
			const res = await getSearchPw(data.userId, data.username, getEmail());
			const message = res.data.message;

			if(message === RESPONSE_MESSAGE.OK){
				setCertificationStatus(true);
				setOverlapStatus('');
			}else if(message === RESPONSE_MESSAGE.NOT_FOUND){
				setOverlapStatus(message);
			}

		}catch(err){
			console.log(err);
		}
	}

	// 인증번호 입력 이벤트
	const handleCertification = (e) => {
		setCertification(e.target.value);
	}

	// 인증번호 입력 시간
	const cerificationTime = () => {
		const minutes = Math.floor(timer / 60);
		const seconds = timer % 60;

		return `${minutes}:${seconds < 10 ? `0${seconds}` : seconds}`;
	}

	// 인증번호 submit 이벤트
	const handleCertificationSubmit = async () => {
		if(timer !== 0){
			try {
				const res = await postCertification(data.userId, certification);
				const message = res.data.message;

				if(message === RESPONSE_MESSAGE.OK){
					//resetPw로 이동
					//state로 아이디와 certification 전달
					navigate('/reset-pw', {
						state: {
							userId: data.userId,
							certification: certification,
						},
					});
				}else if(message === RESPONSE_MESSAGE.ERROR)
					alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
				else if(message === RESPONSE_MESSAGE.FAIL)
					alert('인증번호가 일치하지 않습니다.');
			}catch(err){
				console.log(err);
			}
		}
	}

	// 인증번호 시간 초기화 이벤트
	const handleTimerReset = () => {
		setTimer(300);
	}

	//아이디 찾기 페이지 이동
	const handleSearchId = () => {
		navigate('/search-id');
	}
	
	return (
        <div className="content login-content">
            <div className="login-header">
                <h1>비밀번호 찾기</h1>
            </div>
            <div className="search-id-form">
                <div className="form-group">
                    <label>아이디</label>
                    <input type={'text'} name={'userId'} className={'form-control'} onChange={handleOnChange} value={data.userId}/>
                </div>
                <div className="form-group">
                    <label>이름</label>
                    <input type={'text'} name={'username'} className={'form-control'} onChange={handleOnChange} value={data.username}/>
                </div>
                <div className="form-group">
                    <label>이메일</label>
                    <input type={'text'} name={'email'} className={'form-control'} onChange={handleOnChange} value={data.email}/>
                    <span>@</span>
                    <input type={'text'} name={'mailSuffix'} onChange={handleOnChange} value={data.mailSuffix}/>
                </div>
                <SearchOverlap
                    status={overlapStatus}
                />
                {certificationStatus && (
                    <>
                        <div className="certification-area">
                            <label>인증번호</label>
                            <input type={'text'} onChange={handleCertification} value={certification}/>
                            <span>{cerificationTime()}</span>
                            <DefaultButton
                                btnText={'시간 연장'}
                                onClick={handleTimerReset}
                                className={'certification-timer-btn'}
                            />
                        </div>
                        <div className="login-form-btn-area">
                            <div className="login-btn">
                                <DefaultButton
                                    className={'login-btn'}
                                    onClick={handleCertificationSubmit}
                                    btnText={'확인'}
                                />
                            </div>
                        </div>
                    </>
                )}
                {!certificationStatus && (
                    <div className="login-form-btn-area">
                        <div className="login-btn">
                            <div className="search-info">
                                <DefaultButton
                                    className={'search-info-btn'}
                                    onClick={handleSearchId}
                                    btnText={'아이디 찾기'}
                                />
                                <DefaultButton
                                    className={'search-info-btn'}
                                    onClick={handleSubmit}
                                    btnText={'비밀번호 찾기'}
                                />
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}

function SearchOverlap(props) {
    const { status } = props;

	let text = '';

	if(status === 'id')
		text = '아이디를 입력하세요.';
	else if(status === 'name')
		text = '이름을 입력하세요.';
	else if(status === 'email')
		text = '이메일을 입력하세요.';
	else if(status === 'email invalid')
		text = '유효하지 않은 이메일 주소입니다.';
	else if(status === 'not found')
		text = '일치하는 정보가 없습니다.';
	
	return (
		<div className="search-id-overlap">
			<span className={'not-found-overlap'}>{text}</span>
		</div>
	)		
}

export default SearchPw;