import React, {useEffect, useRef, useState} from "react";

import { getUserData, patchUserData } from '../../services/mypageMemberService';
import { getNicknameCheck } from '../../../member/services/memberService';

import { RESPONSE_MESSAGE } from "../../../../common/constants/responseMessageType";
import { PATTERNS } from "../../../../common/constants/patterns";


import MyPageSideNav from "../../components/MyPageSideNav";
import DefaultButton from "../../../../common/components/DefaultButton";
import Overlap from '../../../../common/components/Overlap'

const CHECK_STATUS = {
	INVALID: 'invalid',
	VALID: 'valid',
	DUPLICATED: 'duplicated',
	NOT_DUPLICATED: 'notDuplicateCheck',
	EMPTY: 'empty',
	ERROR: 'error',
}

/*
	회원 정보 수정
	현재는 토큰 기반으로 로그인 상태면 바로 접근이 가능한데
	어떻게 수정할지 고민 중.
	로컬 사용자의 경우 비밀번호를 재입력 하는 것으로 인증이 가능하지만,
	OAuth 사용자의 경우 저장된 비밀번호가 존재하지 않기 때문에 고민 중.
*/
function MyPageUpdateInfo() {
	const [userData, setUserData] = useState({
        nickname: '',
        phone: '',
        email: '',
    });
    const [nicknameCheck, setNicknameCheck] = useState('');
    const [phoneCheck, setPhoneCheck] = useState('');
    const [emailCheck, setEmailCheck] = useState('');
    const [emailProvider, setEmailProvider] = useState('');
    const [emailSuffix, setEmailSuffix] = useState(``);
    const [nicknameCheckInfo, setNicknameCheckInfo] = useState(false);

    const nicknameElem = useRef(null);
    const phoneElem = useRef(null);
    const mailElem = useRef(null);

	const getUserInfo = async() => {
		try {
			const res = await getUserData();
			const contentData = res.data;

			setUserData({
				nickname: contentData.nickname,
				phone: contentData.phone,
				email: contentData.mailPrefix,
			});

			setEmailProvider(contentData.mailType);
			setEmailSuffix(contentData.mailSuffix);
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getUserInfo();
	}, []);
	
	//input 입력 이벤트
	const handleOnChange = (e) => {
		const { name, value } = e.target;

		setUserData({
			...userData,
			[name]: value,
		});

		if(name === 'nickname')
			setNicknameCheckInfo(false);
	}

	//닉네임 중복 체크 요청 이벤트
	const handleNicknameCheck = async() => {
		try {
			const res = await getNicknameCheck(userData.nickname);

			const message = res.data.message;

			if(message === RESPONSE_MESSAGE.DUPLICATED){
				setNicknameCheckInfo(false);
				setNicknameCheck(CHECK_STATUS.DUPLICATED);
			}
			else if(message === RESPONSE_MESSAGE.NO_DUPLICATED){
				setNicknameCheckInfo(true);
				setNicknameCheck(CHECK_STATUS.VALID);
			}
		} catch (error) {
			console.log(error);
		}
	}
	
	//이메일 suffix select box 이벤트
	const handleEmailSelectOnChange = (e) => {
		const value = e.target.value;
		setEmailProvider(value);
		let suffix = '';

		if(value === 'naver')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_NAVER;
        else if(value === 'daum')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_DAUM;
        else if(value === 'google')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_GOOGLE;

        setEmailSuffix(suffix);
	}

	// 이메일 직접 입력 선택 시 input 이벤트
	const handleEmailSuffixChange = (e) => {
		setEmailCheck('');
		setEmailSuffix(e.target.value);
	}

	// 수정 요청 시 데이터 검증
	const validateData = (email) => {
		if(!nicknameCheckInfo && userData.nickname !== '') {
            setNicknameCheck(CHECK_STATUS.NOT_DUPLICATED);
            nicknameElem.current.focus();
			return false;
        }else if(!PATTERNS.EMAIL.test(email)) {
            setEmailCheck(CHECK_STATUS.INVALID);
            mailElem.current.focus();
			return false;
        }else if(!PATTERNS.PHONE.test(userData.phone)){
            setPhoneCheck(CHECK_STATUS.INVALID);
            phoneElem.current.focus();
			return false;
        }

		return true;
	}

	//수정 요청 이벤트
	const handleSubmit = async() => {
		const userEmail = userData.email + '@' + emailSuffix;
		if(validateData(userEmail)) {
			try {
				const res = await patchUserData(userData, userEmail);

				if(res.data.message === RESPONSE_MESSAGE.OK) {
					alert('수정되었습니다.');
					getUserInfo();
				}
			} catch (error) {
				console.log(error);
				alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
			}
		}
	}

	return (
        <div className="mypage">
            <MyPageSideNav />
            <div className="mypage-content">
                <div className="mypage-content-header">
                    <h1>정보 수정</h1>
                </div>
                <div className="mypage-user-info-content">
                    <div className="form-content">
                        <div>
                            <label>닉네임</label>
                        </div>
                        <div>
                            <input type={'text'} name={'nickname'} placeholder={'닉네임'} onChange={handleOnChange} ref={nicknameElem} value={userData.nickname} />
                            <DefaultButton className={'nickname-check-btn'} onClick={handleNicknameCheck} btnText={'중복체크'} />
                            <p className={'nickname-info'}>닉네임을 입력하지 않을 시 활동 내역에 대해 닉네임 대신 이름으로 처리됩니다.</p>
                            <NicknameOverlap
                                checkValue={nicknameCheck}
                            />
                        </div>
                    </div>
                    <div className="form-content">
                        <div>
                            <label>연락처</label>
                        </div>
                        <div>
                            <input type={'text'} name={'phone'} placeholder={'-를 제외한 숫자만 입력하세요'} onChange={handleOnChange} ref={phoneElem} value={userData.phone}/>
                            <PhoneOverlap
                                checkValue={phoneCheck}
                            />
                        </div>
                    </div>
                    <div className="form-content">
                        <div>
                            <label>이메일</label>
                        </div>
                        <div>
                            <input type={'text'} name={'email'} placeholder={'이메일'} onChange={handleOnChange} ref={mailElem} value={userData.email}/>
                            <span> @ </span>
                            <EmailProvider providerStatus={emailProvider} handleInputChange={handleEmailSuffixChange} emailSuffix={emailSuffix}/>
                            <select className={'email-select'} name={'email-suffix'} onChange={handleEmailSelectOnChange} value={emailProvider}>
                                <option value={'naver'}>네이버</option>
                                <option value={'daum'}>다음</option>
                                <option value={'google'}>구글</option>
                                <option value={'none'}>직접입력</option>
                            </select>
                            <EmailOverlap
                                checkValue={emailCheck}
                            />
                        </div>
                    </div>
                </div>
                <DefaultButton className={'info-submit-btn'} onClick={handleSubmit} btnText={'수정'}/>
            </div>
        </div>
    )
}

function NicknameOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';

    if(checkValue === CHECK_STATUS.EMPTY)
        overlapText = '닉네임을 입력하세요';
    else if(checkValue === CHECK_STATUS.DUPLICATED)
        overlapText = '이미 사용중인 닉네임입니다';
    else if(checkValue === CHECK_STATUS.VALID)
        overlapText = '사용 가능한 닉네임입니다';
    else if(checkValue === CHECK_STATUS.ERROR)
        overlapText = '오류가 발생했습니다. 문제가 계속되면 문의해주세요';
    else if(checkValue === CHECK_STATUS.NOT_DUPLICATED)
        overlapText = '닉네임 중복 체크를 해주세요';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function PhoneOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';
    if(checkValue === CHECK_STATUS.EMPTY)
        overlapText = '연락처를 입력해주세요';
    else if (checkValue === CHECK_STATUS.INVALID)
        overlapText = '유효한 연락처가 아닙니다.';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function EmailProvider (props) {
    const { providerStatus, handleInputChange, emailSuffix } = props;

	if(providerStatus !== 'none')
		return null;

	return (
		<input type={'text'} name={'email-suffix-input'} value={emailSuffix} onChange={handleInputChange}/>
	)
}

function EmailOverlap (props) {
    const { checkValue } = props;

    let overlapText = '';

    if(checkValue === CHECK_STATUS.INVALID)
        overlapText = '유효하지 않은 이메일 주소입니다.';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

export default MyPageUpdateInfo;