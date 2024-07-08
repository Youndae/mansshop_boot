import React, {useRef, useState} from 'react';
import {useNavigate} from "react-router-dom";

import {axiosDefault} from "../../../modules/customAxios";

import DefaultBtn from "../../ui/DefaultBtn";

function SearchId() {
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

    const emailPattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
    const phonePattern = /^01(?:0|1|6|9)([0-9]{3,4})([0-9]{4})$/;


    const handleOnChange = (e) => {
        setData({
            ...data,
            [e.target.name]: e.target.value,
        });

    }

    const checkUserName = () => {
        const name = data.username;

        if(name === ''){
            setOverlapStatus('name');
            nameElem.current.focus();
        }else {
            return true;
        }
    }

    const handleSearchPhoneSubmit = () => {
        if(checkUserName()){
            if(data.userPhone === '' || !phonePattern.test(data.userPhone)){
                setOverlapStatus('phone');
                phoneElem.current.focus();
            }else {
                const queryParam = `&userPhone=${data.userPhone}`;
                submitRequest(queryParam);
            }
        }
    }

    const handleSearchEmailSubmit = () => {
        if(checkUserName){
            const email = `${data.userEmail}@${data.emailSuffix}`;
            if(data.userEmail === '' || data.emailSuffix === '' || !emailPattern.test(email)){
                setOverlapStatus('mail');
                emailElem.current.focus();
            }else {
                const queryParam = `&userEmail=${email}`;
                submitRequest(queryParam);
            }
        }
    }

    const submitRequest = async (queryParam) => {
        const url = `member/search-id?userName=${data.username}${queryParam}`;

        await axiosDefault.get(url)
            .then(res => {
                if(res.data.message === 'not found'){
                    setOverlapStatus('not found');
                }else if(res.data.message === 'OK') {
                    setOverlapStatus('found');
                    setSearchId(res.data.userId);
                }

            })
    }

    const handleRadioOnChange = (e) => {
        const elementName = e.target.name;

        if(elementName === 'phone') {
            setPhoneStatus(true);
            setEmailStatus(false);
        }else if(elementName === 'email'){
            setPhoneStatus(false);
            setEmailStatus(true);
        }
    }

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
                <input className="radio-input" type={'radio'} name={'phone'} onChange={handleRadioOnChange} checked={phoneStatus}/>
                <label className="radio-label">이메일로 검색</label>
                <input className="radio-input" type={'radio'} name={'email'} onChange={handleRadioOnChange} checked={emailStatus}/>
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
    const { data, status, onChange, searchStatus, handleSubmit, searchId, nameElem, phoneElem, handleSearchPassword } = props;

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
                        <DefaultBtn
                            className={'search-info-btn'}
                            onClick={handleSubmit}
                            btnText={'아이디 찾기'}
                        />
                        <DefaultBtn
                            className={'search-info-btn'}
                            onClick={handleSearchPassword}
                            btnText={'비밀번호 찾기'}
                        />
                    </div>
                </div>
            </>
        )
    }else {
        return null;
    }
}

function SearchEmail(props) {
    const { data, status, onChange, searchStatus, handleSubmit, searchId, nameElem, emailElem, handleSearchPassword } = props;

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
                        <DefaultBtn
                            className={'search-info-btn'}
                            onClick={handleSubmit}
                            btnText={'아이디 찾기'}
                        />
                        <DefaultBtn
                            className={'search-info-btn'}
                            onClick={handleSearchPassword}
                            btnText={'비밀번호 찾기'}
                        />
                    </div>
                </div>
            </>
        )
    }else {
        return null;
    }
}

function SearchOverlap(props) {
    const { status, searchId } = props;

    if(status === 'not found'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>일치하는 정보가 없습니다.</span>
            </div>
        )
    }else if(status === 'found') {
        return (
            <div className="search-id-overlap">
                <span className={'found-id'}>회원님의 아이디는 {searchId} 입니다.</span>
            </div>
        )
    }else if(status === 'name'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>이름을 입력해주세요</span>
            </div>
        )
    }else if(status === 'phone') {
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>휴대폰 번호를 입력해주세요</span>
            </div>
        )
    }else if(status === 'mail') {
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>이메일을 입력해주세요</span>
            </div>
        )
    }
}

export default SearchId;