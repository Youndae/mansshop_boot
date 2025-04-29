import React, {useEffect, useRef, useState} from 'react';
import {useNavigate} from "react-router-dom";

import {axiosDefault, axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";

import DefaultBtn from "../../ui/DefaultBtn";



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

    const emailPattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;

    useEffect(() => {
        //모두 입력한 뒤 요청 후 인증번호 입력 시간 처리
        let interval;
        if(certificationStatus) {
            let interval = setInterval(() => {
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
        setData({
            ...data,
            [e.target.name]: e.target.value,
        });

    }

    //사용자 이름 입력 여부 확인. 입력하지 않았다면 focus
    const checkUserName = () => {
        const name = data.username;

        if(name === ''){
            setOverlapStatus('name');
            nameElem.current.focus();
        }else {
            return true;
        }
    }

    //비밀번호 찾기 요청
    //아이디, 이름, 이메일을 입력했다면
    //요청을 전달하고 정상 응답을 받게 되면 인증번호 입력 폼이 출력.
    //인증번호 입력 폼 상태값을 변경해 출력하도록 처리하게 되고
    //interval이 동작.
    const handleSubmit = async () => {
        if(checkUserName()) {
            const email = `${data.email}@${data.mailSuffix}`;

            if(data.userId === ''){
                setOverlapStatus('id');
                userIdElem.current.focus();
            }else if(data.email === '' || data.mailSuffix === ''){
                setOverlapStatus('email');
                emailElem.current.focus();
            }else if(!emailPattern.test(email)) {
                setOverlapStatus('email invalid');
                emailElem.current.focus();
            }else {
                await axiosInstance.get(`member/search-pw?id=${data.userId}&name=${data.username}&email=${email}`)
                    .then(res => {
                        if(checkResponseMessageOk(res)){
                            //인증번호 입력 Element 상태 변경
                            setCertificationStatus(true);
                            setOverlapStatus('');
                        }else if(res.data.message === 'not found'){
                            setOverlapStatus(res.data.message);
                        }
                    })
            }
        }
    }

    //인증번호 입력 이벤트
    const handleCertification = (e) => {
        setCertification(e.target.value);
    }

    //인증번호 입력 시간
    const certificationTime = () => {
        const minutes = Math.floor(timer / 60);
        const seconds = timer % 60;

        return `${minutes}:${seconds < 10 ? `0${seconds}` : seconds}`;
    }

    //인증번호 submit 이벤트
    const handleCertificationSubmit = async () => {
        if(timer !== 0) {
            await axiosDefault.post(`member/certification`, {
                userId: data.userId,
                certification: certification,
            })
                .then(res => {
                    if(checkResponseMessageOk(res)){
                        //resetPw 이동
                        //state로 아이디와 certification 전달
                        navigate('/reset-pw', {state : {
                                userId: data.userId,
                                certification: certification
                            }});
                    }else if(res.data.message === 'ERROR') {
                        alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
                    }else if(res.data.message === 'FAIL') {
                        alert('인증번호가 일치하지 않습니다.');
                    }
                })
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
                            <span>{certificationTime()}</span>
                            <DefaultBtn
                                btnText={'시간 연장'}
                                onClick={handleTimerReset}
                                className={'certification-timer-btn'}
                            />
                        </div>
                        <div className="login-form-btn-area">
                            <div className="login-btn">
                                <DefaultBtn
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
                                <DefaultBtn
                                    className={'search-info-btn'}
                                    onClick={handleSearchId}
                                    btnText={'아이디 찾기'}
                                />
                                <DefaultBtn
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

    if(status === 'id'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>아이디를 입력하세요.</span>
            </div>
        )
    }else if(status === 'name'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>이름을 입력하세요.</span>
            </div>
        )
    }else if(status === 'email'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>이메일을 입력하세요.</span>
            </div>
        )
    }else if(status === 'email invalid'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>유효하지 않은 이메일 주소입니다.</span>
            </div>
        )
    }else if(status === 'not found'){
        return (
            <div className="search-id-overlap">
                <span className={'not-found-overlap'}>일치하는 정보가 없습니다.</span>
            </div>
        )
    }
}

export default SearchPw;