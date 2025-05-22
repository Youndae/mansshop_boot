import React, { useRef, useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';

import { getSearchId } from '../services/memberService';
import { PATTERNS } from '../../../common/constants/patterns';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import { buildQueryString } from '../../../common/utils/queryStringUtils';

import DefaultButton from '../../../common/components/DefaultButton';

/*
    아이디 찾기 페이지
    로그인한 사용자인 경우 메인페이지로 강제 이동
 */
function SearchId() {
	const loginStatus = useSelector(state => state.member.loginStatus);
	const [data, setData] = useState({
        username: '',
        userPhone: '',
        userEmail: '',
        emailSuffix: '',
    });
    const [phoneStatus, setPhoneStatus] = useState(true);
    const [emailStatus, setEmailStatus] = useState(false);
    const [overlapStatus, setOverlapStatus] = useState('');
    const [searchId, setSearchId] = useState('');

    const nameElem = useRef(null);
    const phoneElem = useRef(null);
    const emailElem = useRef(null);

    const navigate = useNavigate();
	
	const emailPattern = PATTERNS.EMAIL;
	const phonePattern = PATTERNS.PHONE;

	useEffect(() => {
		if(loginStatus)
			navigate('/');
	}, [loginStatus]);

	// input 입력 이벤트
	const handleOnChange = (e) => {
		const { name, value } = e.target;
		setData({
			...data,
			[name]: value,
		});
	}

	//userName 입력 여부 확인. 입력하지 않았다면 focus
	const checkUserName = () => {
		const name = data.username;

		if(name === ''){
			setOverlapStatus('name');
			nameElem.current.focus();
		}else
			return true;
	}

	//이름과 연락처 기반 검색 submit 이벤트
	const handleSearchPhoneSubmit = () => {
		if(checkUserName()){
			if(data.userPhone === '' || !phonePattern.test(data.userPhone)){
				setOverlapStatus('phone');
				phoneElem.current.focus();
			}else
				submitRequest('phone');
		}
	}

	//이름과 이메일 기반 검색 submit 이벤트
	const handleSearchEmailSubmit = () => {
		if(checkUserName()){

			if(data.userEmail === '' || data.emailSuffix === '' || !emailPattern.test(getEmail())){
				setOverlapStatus('email');
				emailElem.current.focus();
			}else
				submitRequest('email');
		}
	}

	//이메일 조합
	const getEmail = () => data.userEmail + '@' + data.emailSuffix;

	//아이디 검색 요청 처리
	const submitRequest = async (type) => {
		const value = type === 'phone' ? data.userPhone : getEmail();

		try{
			const res = await getSearchId(data.username, type, value);

			if(res.data.message === RESPONSE_MESSAGE.OK){
				setOverlapStatus('found');
				setSearchId(res.data.userId);
			}else if(res.data.message === RESPONSE_MESSAGE.NOT_FOUND)
				setOverlapStatus('not found');
			
		}catch(err){
			console.log(err);
		}
		
	}

	//연락처, 이메일 기반 선택 radio 버튼 이벤트
	const handleRadioChange = (e) => {
		const name = e.target.name;

		if(name === 'phone'){
			setPhoneStatus(true);
			setEmailStatus(false);
		}else{
			setPhoneStatus(false);
			setEmailStatus(true);
		}
	}

	// 비밀번호 찾기 페이지로 이동
	const handleSearchPassword = () => {
		navigate('/search-pw');
	}

	return (
        <div className="content login-content">
            <div className="login-header">
                <h1>아이디 찾기</h1>
            </div>
            <div className="search-id-radio isOpen-radio">
                <label className="radio-label">휴대폰 번호로 검색</label>
                <input className="radio-input" type={'radio'} name={'phone'} onChange={handleRadioChange} checked={phoneStatus}/>
                <label className="radio-label">이메일로 검색</label>
                <input className="radio-input" type={'radio'} name={'email'} onChange={handleRadioChange} checked={emailStatus}/>
            </div>
            <div className="search-id-form">
                <SearchPhone
                    data={data}
                    status={phoneStatus}
                    onChange={handleOnChange}
                    searchStatus={overlapStatus}
                    handleSubmit={handleSearchPhoneSubmit}
                    searchId={searchId}
                    nameElem={nameElem}
                    phoneElem={phoneElem}
                    handleSearchPassword={handleSearchPassword}
                />
                <SearchEmail
                    data={data}
                    status={emailStatus}
                    onChange={handleOnChange}
                    searchStatus={overlapStatus}
                    handleSubmit={handleSearchEmailSubmit}
                    searchId={searchId}
                    nameElem={nameElem}
                    emailElem={emailElem}
                    handleSearchPassword={handleSearchPassword}
                />
            </div>
        </div>
    )
}

function SearchPhone(props) {
    const { 
		data, 
		status, 
		onChange, 
		searchStatus, 
		handleSubmit, 
		searchId, 
		nameElem, 
		phoneElem, 
		handleSearchPassword 
	} = props;

    if(status){
        return (
            <>
                <div className="form-group">
                    <label>이름</label>
                    <input type="text" className="form-control" name={'username'} onChange={onChange} value={data.username} ref={nameElem}/>
                </div>
                <div className="form-group">
                    <label>휴대폰 번호</label>
                    <input type="text" className="form-control" name={'userPhone'} onChange={onChange} value={data.userPhone} ref={phoneElem} placeholder={'-를 제외한 숫자만 입력'}/>
                </div>
                <SearchOverlap
                    status={searchStatus}
                    searchId={searchId}
                />
                <div className="login-form-btn-area">
                    <div className="search-info">
                        <DefaultButton
                            className={'search-info-btn'}
                            onClick={handleSubmit}
                            btnText={'아이디 찾기'}
                        />
                        <DefaultButton
                            className={'search-info-btn'}
                            onClick={handleSearchPassword}
                            btnText={'비밀번호 찾기'}
                        />
                    </div>
                </div>
            </>
        )
    }

	return null;
}

function SearchEmail(props) {
    const { 
		data, 
		status, 
		onChange, 
		searchStatus, 
		handleSubmit, 
		searchId, 
		nameElem, 
		emailElem, 
		handleSearchPassword 
	} = props;

    if(status){
        return (
            <>
                <div className="form-group">
                    <label>이름</label>
                    <input type="text" className="form-control" name={'username'} onChange={onChange} value={data.username} ref={nameElem}/>
                </div>
                <div className="form-group">
                    <label>이메일</label>
                    <input type="text" className="form-control" name={'userEmail'} onChange={onChange} value={data.userEmail} ref={emailElem}/>
                    <span>@</span>
                    <input type={'text'} name={'emailSuffix'} onChange={onChange} value={data.emailSuffix}/>
                </div>
                <SearchOverlap
                    status={searchStatus}
                    searchId={searchId}
                />
                <div className="login-form-btn-area">
                    <div className="login-btn">
                        <DefaultButton
                            className={'search-info-btn'}
                            onClick={handleSubmit}
                            btnText={'아이디 찾기'}
                        />
                        <DefaultButton
                            className={'search-info-btn'}
                            onClick={handleSearchPassword}
                            btnText={'비밀번호 찾기'}
                        />
                    </div>
                </div>
            </>
        )
    }

	return null;
}

function SearchOverlap(props) {
    const { status, searchId } = props;

	let className = '';
	let text = '';

	if(status === 'not found'){
		className = 'not-found-overlap';
		text = '일치하는 정보가 없습니다.';
	}else if(status === 'found'){
		className = 'found-id';
		text = `회원님의 아이디는 ${searchId} 입니다.`;
	}else if(status === 'name'){
		className = 'not-found-overlap';
		text = '이름을 입력해주세요';
	}else if(status === 'phone'){
		className = 'not-found-overlap';
		text = '휴대폰 번호를 입력해주세요';
	}else if(status === 'email'){
		className = 'not-found-overlap';
		text = '이메일을 입력해주세요';
	}

	return (
		<div className="search-id-overlap">
			<span className={className}>{text}</span>
		</div>
	)
}

export default SearchId;