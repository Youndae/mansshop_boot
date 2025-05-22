import React, { useRef, useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

import { 
	postJoin,
	getUserIdCheck,
	getNicknameCheck,
} from '../services/memberService';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import { PATTERNS } from '../../../common/constants/patterns';

import usePasswordValidator from '../../../common/hooks/usePasswordValidator';

import Overlap from '../../../common/components/Overlap';
import DefaultButton from '../../../common/components/DefaultButton';

const CheckResult = {
	valid: 'valid',
	invalid: 'invalid',
	short: 'short',
	duplicated: 'duplicated',
	notDuplicated: 'notDuplicateCheck',
	empty: 'empty',
	err: 'err',
}

function Register() {
	const loginStatus = useSelector(state => state.member.loginStatus);
	const [userData, setUserData] = useState({
        userId: '',
        userPw: '',
        checkPassword: '',
        userName: '',
        nickname: '',
        phone: '',
        email: '',
    });
    const [idCheck, setIdCheck] = useState('');
    const [nameCheck, setNameCheck] = useState('');
    const [nicknameCheck, setNicknameCheck] = useState('');
    const [phoneCheck, setPhoneCheck] = useState('');
    const [emailCheck, setEmailCheck] = useState('');
    const [emailProvider, setEmailProvider] = useState('naver');
    const [emailSuffix, setEmailSuffix] = useState(`${process.env.REACT_APP_EMAIL_SUFFIX_NAVER}`);
    const [birth, setBirth] = useState({
        year: 2024,
        month: 1,
        day: 1,
    });
    const [lastDay, setLastDay] = useState(31);
    const [checkInfo, setCheckInfo] = useState({
        idCheckInfo: false,
        pwCheckInfo: false,
        nicknameCheck: false,
    });

    const idElem = useRef(null);
    const pwElem = useRef(null);
    const pwCheckElem = useRef(null);
    const nameElem = useRef(null);
    const nicknameElem = useRef(null);
    const phoneElem = useRef(null);
    const mailElem = useRef(null);

    const navigate = useNavigate();

	const {
		pwCheck,
		verifyPw,
		pwCheckInfo,
		validateAndSyncPassword,
	} = usePasswordValidator();

    const idPattern = PATTERNS.USERID;
    const emailPattern = PATTERNS.EMAIL;
    const phonePattern = PATTERNS.PHONE;

	useEffect(() => {
		if(loginStatus)
			navigate('/');
	}, [loginStatus]);

	// input 입력 이벤트
	const handleOnChange = (e) => {
		const { name, value } = e.target;
		setUserData({
			...userData,
			[name]: value,
		});

		switch(name){
			case 'userId':
				resetIdCheck(value);
				break;
			case 'userPw':
				validateAndSyncPassword(name, value, userData);
				break;
			case 'checkPassword':
				validateAndSyncPassword(name, value, userData);
				break;
			case 'userName':
				validateUserName(value);
				break;
			case 'nickname':
				validateNickname(value);
				break;
			case 'phone':
				validatePhone(value);
				break;
			default:
				break;
		}
	}

	const resetIdCheck = () => {
		setCheckInfo({
			...checkInfo,
			idCheckInfo: false,
		});
	}

	// 사용자 이름 검증 및 처리
	const validateUserName = (value) => {
		// 미입력만 검증
		if(value !== '')
			setNameCheck('');
	}

	const validateNickname = (value) => {
		//수정이 발생하면 검증 상태값을 false로 변경
		setCheckInfo({
			...checkInfo,
			nicknameCheck: false,
		});
	}

	const validatePhone = (value) => {
		// 휴대폰 번호 입력값 처리 및 검증
		if(!phonePattern.test(value))
			setPhoneCheck(CheckResult.invalid);
		else
			setPhoneCheck(CheckResult.valid);
	}

	// submit 이벤트
	const handleJoinSubmit = async () => {
		const validation = validateJoin();
		if(!validation.result){
			invalidFocus(validation);
			return;
		}

		await handleJoin();
	}

	const getEmail = () => userData.email + '@' + emailSuffix;

	const validateJoin = () => {
		const userEmail = getEmail();

		if(!checkInfo.idCheckInfo){
			return {
				result: false,
				field: 'userId',
				checkValue: CheckResult.notDuplicated
			};
		}else if(!pwCheckInfo) {
			return {
				result: false,
				field: 'userPw',
				checkValue: CheckResult.invalid,
			};
		}else if(userData.userName === '') {
			return {
				result: false,
				field: 'userName',
				checkValue: CheckResult.empty,
			};
		}else if(userData.nickname !== '' && !checkInfo.nicknameCheck){
			return {
				result: false,
				field: 'nickname',
				checkValue: CheckResult.notDuplicated,
			};
		}else if(!emailPattern.test(userEmail)) {
			return {
				result: false,
				field: 'email',
				checkValue: CheckResult.invalid,
			};
		}else if(!phonePattern.test(userData.phone)) {
			return {
				result: false,
				field: 'phone',
				checkValue: CheckResult.invalid,
			};
		}else {
			return { result: true };
		}
	}
	
	
	const invalidFocus = ({ field, checkValue }) => {
		switch(field){
			case 'userId':
				setIdCheck(checkValue);
				idElem.current.focus();
				break;
			case 'userPw':
				pwElem.current.focus();
				break;
			case 'userName':
				setNameCheck(checkValue);
				nameElem.current.focus();
				break;
			case 'nickname':
				setNicknameCheck(checkValue);
				nicknameElem.current.focus();
				break;
			case 'email':
				setEmailCheck(checkValue);
				mailElem.current.focus();
				break;
			case 'phone':
				setPhoneCheck(checkValue);
				phoneElem.current.focus();
				break;
			default:
				break;
		}
	}

	const handleJoin = async () => {
		const userEmail = getEmail();
		const userBirth = birth.year + '/' + birth.month + '/' + birth.day;

		try{
			const res = await postJoin(userData, userEmail, userBirth);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				navigate('/login');
			else
				alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
		}catch(err){
			console.log(err);
			alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
		}
	}

	//아이디 중복 체크 버튼 이벤트
	const handleIdCheck = async () => {
		const userId = userData.userId;

		if(userId === '')
			setIdCheck(CheckResult.empty);
		else if(!idPattern.test(userId))
			setIdCheck(CheckResult.invalid);
		else {
			try {
				const res = await getUserIdCheck(userId);
				const responseMessage = res.data.message;

				if(responseMessage === RESPONSE_MESSAGE.DUPLICATED){
					setCheckInfo({
						...checkInfo,
						idCheckInfo: false,
					});
					setIdCheck(CheckResult.duplicated);
				}else if(responseMessage === RESPONSE_MESSAGE.NOT_DUPLICATED){
					setCheckInfo({
						...checkInfo,
						idCheckInfo: true,
					});
					setIdCheck(CheckResult.valid);
				}
			}catch(err){
				console.log(err);
				setIdCheck(CheckResult.err);
			}
		}
	}

	//닉네임 중복 체크 버튼 이벤트
	const handleNicknameCheck = async () => {
		if(userData.nickname === '')
			setNicknameCheck(CheckResult.empty);
		else {
			try {
				const res = await getNicknameCheck(userData.nickname);
				const responseMessage = res.data.message;

				if(responseMessage === RESPONSE_MESSAGE.DUPLICATED){
					setCheckInfo({
						...checkInfo,
						nicknameCheck: false,
					});
					setNicknameCheck(CheckResult.duplicated);
				}else if(responseMessage === RESPONSE_MESSAGE.NOT_DUPLICATED){
					setCheckInfo({
						...checkInfo,
						nicknameCheck: true,
					});
					setNicknameCheck(CheckResult.valid);
				}
			}catch(err){
				console.log(err);
				setNicknameCheck(CheckResult.err);
			}
		}
	}

	//이메일 suffix select box 이벤트
	const handleEmailSelectOnChange = (e) => {
		const { value } = e.target;
		setEmailProvider(value);
		let suffix;

		if(value === 'naver')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_NAVER;
        else if(value === 'daum')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_DAUM;
        else if(value === 'google')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_GOOGLE;

		setEmailSuffix(suffix);
	}

	// 이메일 직접 입력 선택 시 input 입력 이벤트
	const handleEmailSuffixChange = (e) => {
		const { value } = e.target;
		setEmailCheck('');
		setEmailSuffix(value);
	}

	// 생년월일 select box 이벤트
	const handleBirthOnChange = (e) => {
		const { name, value } = e.target;
		setBirth({
			...birth,
			[name]: value,
		});

		if(name !== 'day'){
			const year = name === 'year' ? value : birth.year;
			const month = name === 'month' ? value : birth.month;
			const lastDay = new Date(year, month, 0).getDate();
			
			setLastDay(lastDay);
		}
	}

	return (
        <div className="join">
            <div className="join-header">
                <h1>회원가입</h1>
            </div>
            <div className="join-content">
                <div className="join-form">
                    <div>
                        <div>
                            <div>
                                <label>아이디</label>
                            </div>
                            <div>
                                <input type={'text'} name={'userId'} placeholder={'아이디'} onChange={handleOnChange} ref={idElem}/>
                                <DefaultButton onClick={handleIdCheck} btnText={'중복체크'} />
                                <IdOverlap
                                    checkValue={idCheck}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>비밀번호</label>
                            </div>
                            <div>
                                <input type={'password'} name={'userPw'} placeholder={'비밀번호'} onChange={handleOnChange} ref={pwElem}/>
                                <PwOverlap
                                    checkValue={pwCheck}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>비밀번호 확인</label>
                            </div>
                            <div>
                                <input type={'password'} name={'checkPassword'} placeholder={'비밀번호 확인'} onChange={handleOnChange} ref={pwCheckElem}/>
                                <CheckPwOverlap
                                    checkValue={verifyPw}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>이름</label>
                            </div>
                            <div>
                                <input type={'text'} name={'userName'} placeholder={'이름'} onChange={handleOnChange} ref={nameElem}/>
                                <UserNameOverlap
                                    checkValue={nameCheck}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>닉네임</label>
                            </div>
                            <div>
                                <input type={'text'} name={'nickname'} placeholder={'닉네임'} onChange={handleOnChange} ref={nicknameElem} />
                                <DefaultButton onClick={handleNicknameCheck} btnText={'중복체크'} />
                                <p className={'nickname-info'}>닉네임을 입력하지 않을 시 활동 내역에 대해 닉네임 대신 이름으로 처리됩니다.</p>
                                <NicknameOverlap
                                    checkValue={nicknameCheck}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>연락처</label>
                            </div>
                            <div>
                                <input type={'text'} name={'phone'} placeholder={'-를 제외한 숫자만 입력하세요'} onChange={handleOnChange} ref={phoneElem}/>
                                <UserNameOverlap
                                    checkValue={phoneCheck}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>생년월일</label>
                            </div>
                            <div className="join-birth">
                                <BirthSelect
                                    onChange={handleBirthOnChange}
                                    birth={birth}
                                    lastDay={lastDay}
                                />
                            </div>
                        </div>
                        <div>
                            <div>
                                <label>이메일</label>
                            </div>
                            <div>
                                <input type={'text'} name={'email'} placeholder={'이메일'} onChange={handleOnChange} ref={mailElem}/>
                                <span> @ </span>
                                <EmailProvider providerStatus={emailProvider} handleInputChange={handleEmailSuffixChange}/>
                                <select className={'email-select'} name={'email-suffix'} onChange={handleEmailSelectOnChange} defaultValue={'naver'}>
                                    <option value={'naver'}>네이버</option>
                                    <option value={'daum'}>다음</option>
                                    <option value={'google'}>구글</option>
                                    <option value={''}>직접입력</option>
                                </select>
                                <EmailOverlap
                                    checkValue={emailCheck}
                                />
                            </div>
                        </div>
                    </div>
                </div>
                <DefaultButton className={'join-btn'} onClick={handleJoinSubmit} btnText={'가입'}/>
            </div>
        </div>
    )
}

function EmailProvider (props) {
    const { providerStatus, handleInputChange } = props;

    if(providerStatus === ''){
        return (
            <input type={'text'} name={'email-suffix-input'} onChange={handleInputChange}/>
        )
    }else{
        return null;
    }
}

function EmailOverlap (props) {
    const { checkValue } = props;

	const overlapText = checkValue === CheckResult.invalid ? '유효하지 않은 이메일 주소입니다.' : '';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function IdOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';
    if(checkValue === CheckResult.empty)
        overlapText = '아이디를 입력하세요';
    else if(checkValue === CheckResult.invalid)
        overlapText = '영문자와 숫자를 사용한 5 ~ 15 자리만 가능합니다';
    else if(checkValue === CheckResult.duplicated)
        overlapText = '이미 사용중인 아이디입니다';
    else if(checkValue === CheckResult.valid)
        overlapText = '사용 가능한 아이디입니다';
    else if(checkValue === CheckResult.err)
        overlapText = '오류가 발생했습니다. 문제가 계속되면 문의해주세요';
    else if(checkValue === CheckResult.notDuplicated)
        overlapText = '아이디 중복 체크를 해주세요';


    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function PwOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';
    if(checkValue === CheckResult.empty)
        overlapText = '비밀번호를 입력하세요';
    else if(checkValue === CheckResult.invalid)
        overlapText = '비밀번호는 영어, 특수문자, 숫자가 포함되어야 합니다.';
    else if(checkValue === CheckResult.valid)
        overlapText = '사용가능한 비밀번호입니다';
    else if(checkValue === CheckResult.short)
        overlapText = '비밀번호는 8자리 이상이어야 합니다'

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function CheckPwOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';
    if(checkValue === CheckResult.invalid || checkValue === CheckResult.empty)
        overlapText = '비밀번호가 일치하지 않습니다.';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function UserNameOverlap(props) {
    const { checkValue } = props;

	const overlapText = checkValue === CheckResult.empty ? '이름을 입력해주세요' : '';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function NicknameOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';

    if(checkValue === CheckResult.empty)
        overlapText = '닉네임을 입력하세요';
    else if(checkValue === CheckResult.duplicated)
        overlapText = '이미 사용중인 닉네임입니다';
    else if(checkValue === CheckResult.valid)
        overlapText = '사용 가능한 닉네임입니다';
    else if(checkValue === CheckResult.err)
        overlapText = '오류가 발생했습니다. 문제가 계속되면 문의해주세요';
    else if(checkValue === CheckResult.notDuplicated)
        overlapText = '닉네임 중복 체크를 해주세요';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function BirthSelect(props) {
    const { onChange, birth, lastDay } = props;

    const today = new Date();
    const currentYear = today.getFullYear();
    const startYear = currentYear - 100;

    const yearArr = Array.from(
        { length: currentYear - startYear + 1 },
        (_, i) => currentYear - i
    );
    const monthArr = Array.from(
        { length: 12 },
        (_, i) => i + 1
    );
    const dayArr = Array.from(
        { length: lastDay },
        (_, i) => i + 1
    );

    const selectRender = (name, options, value) => (
      <select name={name} onChange={onChange} value={value}>
          {options.map((num) => (
              <option key={num} value={num}>{num}</option>
          ))}
      </select>
    );

    return (
        <>
            {selectRender('year', yearArr, birth.year)}년
            {selectRender('month', monthArr, birth.month)}월
            {selectRender('day', dayArr, birth.day)}일
        </>
    )
}

export default Register;
