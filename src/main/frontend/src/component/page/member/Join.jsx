import React, {useRef, useState, useEffect} from 'react';
import { useSelector } from "react-redux";
import {useNavigate} from "react-router-dom";

import {axiosDefault, axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";

import Overlap from "../../ui/Overlap";
import DefaultBtn from "../../ui/DefaultBtn";

/*
    로컬 회원가입 페이지
 */
function Join() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
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
    const [pwCheck, setPwCheck] = useState('');
    const [verifyPw, setVerifyPw] = useState('');
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

    const idPattern = /^[A-Za-z0-9]{5,15}$/;
    const pwPattern = /^(?=.*[a-zA-Z])(?=.*[!@#$%^&*+=-])(?=.*[0-9]).{8,16}$/;
    const emailPattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
    const phonePattern = /^01(?:0|1|6|9)([0-9]{3,4})([0-9]{4})$/;

    const responseDuplicatedMessage = 'duplicated';
    const responseNoDuplicatesMessage = 'No duplicates';

    useEffect(() => {
        if(loginStatus)
            navigate('/');
    }, [loginStatus]);

    // input 입력 이벤트
    const handleOnChange = (e) => {
        setUserData({
            ...userData,
            [e.target.name]: e.target.value,
        });

        const targetName = e.target.name;

        if(targetName === 'userId'){
            //사용자 아이디가 변경되는 경우 checkInfo를 false로 설정
            setCheckInfo({
                ...checkInfo,
                idCheckInfo: false,
            });
        }else if(targetName === 'userPw'){
            const pwValue = e.target.value;

            //비밀번호 길이 및 패턴 검증
            if(pwValue.length < 8)
                setPwCheck('short');
            else if(pwPattern.test(pwValue) === false)
                setPwCheck('invalid');
            else if(pwValue.length > 8 && pwPattern.test(pwValue)) {
                setPwCheck('valid');
            }

            //검증에 통과했다면 사용할 수 있는 상태로 checkInfo 수정
            if(pwValue === userData.checkPassword){
                setCheckInfo({
                    ...checkInfo,
                    'pwCheckInfo': true,
                });
                setVerifyPw('valid');
            }else{ // 검증에 실패했다면 사용할 수 없는 상태로 checkInfo 수정
                setCheckInfo({
                    ...checkInfo,
                    'pwCheckInfo': false,
                });
                setVerifyPw('invalid');
            }
        }else if(targetName === 'checkPassword'){
            const pwValue = e.target.value;
            //비밀번호 재입력 검증.
            //비밀번호 input과 일치 여부를 검증
            if(userData.userPw === pwValue) {
                setVerifyPw('valid');
                setCheckInfo({
                    ...checkInfo,
                    'pwCheckInfo': true,
                });
            }else if(userData.userPw !== pwValue) {
                setVerifyPw('invalid');
                setCheckInfo({
                    ...checkInfo,
                    'pwCheckInfo': false,
                });
            }
        }else if(targetName === 'userName'){
            //사용자 이름 검증 및 처리
            const nameValue = e.target.value;
            if(nameValue !== '')
                setNameCheck('');
        }else if(targetName === 'nickname') {
            //닉네임 입력값 처리
            //닉네임 중복 여부 확인 이후 다시 수정되는 경우를 감안해 상태값 수정
            setCheckInfo({
                ...checkInfo,
                nicknameCheckInfo: false,
            })
        }else if(targetName === 'phone') {
            //연락처 입력값 처리 및 검증
            const phoneValue = e.target.value;
            if(!phonePattern.test(phoneValue))
                setPhoneCheck('invalid');
            else
                setPhoneCheck('valid');
        }
    }

    //submit 이벤트
    const handleJoinSubmit = async () => {
        const userEmail = userData.email + '@' + emailSuffix;

        if(!checkInfo.idCheckInfo){
            //아이디 중복 체크 요청 overlap 출력 및 focus
            setIdCheck('notDuplicateCheck');
            idElem.current.focus();
        }else if(!checkInfo.pwCheckInfo){
            //비밀번호 확인 요청 overlap 출력 및 focus
            pwElem.current.focus();
        }else if(userData.userName === ''){
            //사용자 이름 입력 요청 overlap 출력 및 focus
            setNameCheck('empty');
            nameElem.current.focus();
        }else if(userData.nickname !== '' && !checkInfo.nicknameCheckInfo) {
            //닉네임 중복 여부 확인이 안된 상태라면 overlap 출력 및 focus
            setNicknameCheck('notDuplicateCheck');
            nicknameElem.current.focus();
        }else if(!emailPattern.test(userEmail)) {
            //이메일 값이 정상이 아니라면 overlap 출력 및 focus
            setEmailCheck('invalid');
            mailElem.current.focus();
        }else if(!phonePattern.test(userData.phone)){
            //연락처 값이 정상이 아니라면 Overlap 출력 및 focus
            setPhoneCheck('invalid');
            phoneElem.current.focus();
        }else {
            const userBirth = birth.year + '/' + birth.month + '/' + birth.day;

            await axiosDefault.post(`member/join`, {
                userId: userData.userId,
                userPw: userData.userPw,
                userName: userData.userName,
                nickname: userData.nickname,
                phone: userData.phone,
                birth: userBirth,
                userEmail: userEmail,
            })
                .then(res => {
                    if(checkResponseMessageOk(res)) {
                        navigate('/login');
                    }
                })
                .catch(() => {
                    alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
                })

        }
    }

    //아이디 중복 체크 버튼 이벤트
    const handleIdCheck = async () => {
        //입력값이 없다면
        if(userData.userId === '')
            setIdCheck('empty');
        else if(!idPattern.test(userData.userId)) //패턴 검증이 실패했다면
            setIdCheck('invalid');
        else {
            //패턴이 정상이라면 중복 체크 요청
            await axiosInstance.get(`member/check-id?userId=${userData.userId}`)
                .then(res => {
                    const responseMessage = res.data.message;

                    if(responseMessage === responseDuplicatedMessage){ // 결과가 중복이라면
                        setCheckInfo({
                            ...checkInfo,
                            'idCheckInfo': false,
                        });
                        setIdCheck('duplication');
                    }else if(responseMessage === responseNoDuplicatesMessage){ // 결과가 중복이 아니라면
                        setCheckInfo({
                            ...checkInfo,
                            'idCheckInfo': true,
                        });
                        setIdCheck('valid');
                    }
                })
                .catch(() => {
                    setIdCheck('err');
                })
        }
    }

    //닉네임 중복 체크 버튼 이벤트
    const handleNicknameCheck = async () => {
        //닉네임 값이 입력되지 않았다면
        if(userData.nickname === '')
            setNicknameCheck('empty');
        else{
            //입력된 상태라면 중복 체크 요청
            await axiosInstance.get(`member/check-nickname?nickname=${userData.nickname}`)
                .then(res => {
                    const responseMessage = res.data.message;

                    if(responseMessage === responseDuplicatedMessage){ // 결과가 중복이라면
                        setCheckInfo({
                            ...checkInfo,
                            'nicknameCheckInfo': false,
                        });
                        setNicknameCheck('duplication');
                    }else if(responseMessage === responseNoDuplicatesMessage){ // 결과가 중복이 아니라면
                        setCheckInfo({
                            ...checkInfo,
                            'nicknameCheckInfo': true,
                        });
                        setNicknameCheck('valid');
                    }

                })
                .catch(() => {
                    setNicknameCheck('err');
                })
        }
    }

    //이메일 Suffix select box 이벤트
    const handleEmailSelectOnChange = (e) => {
        const val = e.target.value;
        setEmailProvider(val);
        let suffix = '';

        if(val === 'naver')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_NAVER;
        else if(val === 'daum')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_DAUM;
        else if(val === 'google')
            suffix = process.env.REACT_APP_EMAIL_SUFFIX_GOOGLE;

        setEmailSuffix(suffix);
    }

    // 이메일 직접 입력 선택 시 input 입력 이벤트
    const handleEmailSuffixChange = (e) => {
        const val = e.target.value;

        setEmailCheck('');

        setEmailSuffix(val);
    }

    //생년월일 select box 이벤트
    const handleBirthOnChange = (e) => {
        const changeType = e.target.name;

        setBirth({
            ...birth,
            [e.target.name]: e.target.value,
        });

        if(changeType !== 'day'){
            const year = changeType === 'year' ? e.target.value : birth.year;
            const month = changeType === 'month' ? e.target.value : birth.month;
            const lastDay = new Date(new Date(year, month, 1) - 86400000).getDate();

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
                                <DefaultBtn onClick={handleIdCheck} btnText={'중복체크'} />
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
                                <DefaultBtn onClick={handleNicknameCheck} btnText={'중복체크'} />
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
                <DefaultBtn className={'join-btn'} onClick={handleJoinSubmit} btnText={'가입'}/>
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

    let overlapText = '';

    if(checkValue === 'invalid')
        overlapText = '유효하지 않은 이메일 주소입니다.';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function IdOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';
    if(checkValue === 'empty')
        overlapText = '아이디를 입력하세요';
    else if(checkValue === 'invalid')
        overlapText = '영문자와 숫자를 사용한 5 ~ 15 자리만 가능합니다';
    else if(checkValue === 'duplication')
        overlapText = '이미 사용중인 아이디입니다';
    else if(checkValue === 'valid')
        overlapText = '사용 가능한 아이디입니다';
    else if(checkValue === 'err')
        overlapText = '오류가 발생했습니다. 문제가 계속되면 문의해주세요';
    else if(checkValue === 'notDuplicateCheck')
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
    if(checkValue === 'empty')
        overlapText = '비밀번호를 입력하세요';
    else if(checkValue === 'invalid')
        overlapText = '비밀번호는 영어, 특수문자, 숫자가 포함되어야 합니다.';
    else if(checkValue === 'valid')
        overlapText = '사용가능한 비밀번호입니다';
    else if(checkValue === 'short')
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
    if(checkValue === 'invalid' || checkValue === 'empty')
        overlapText = '비밀번호가 일치하지 않습니다.';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function UserNameOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';
    if(checkValue === 'empty')
        overlapText = '이름을 입력해주세요';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

function NicknameOverlap(props) {
    const { checkValue } = props;

    let overlapText = '';

    if(checkValue === 'empty')
        overlapText = '닉네임을 입력하세요';
    else if(checkValue === 'duplication')
        overlapText = '이미 사용중인 닉네임입니다';
    else if(checkValue === 'valid')
        overlapText = '사용 가능한 닉네임입니다';
    else if(checkValue === 'err')
        overlapText = '오류가 발생했습니다. 문제가 계속되면 문의해주세요';
    else if(checkValue === 'notDuplicateCheck')
        overlapText = '닉네임 중복 체크를 해주세요';

    return (
        <Overlap
            overlapText={overlapText}
        />
    )
}

const lodash = require('lodash');

function BirthSelect(props) {
    const { onChange, birth, lastDay } = props;

    const today = new Date();
    const year = today.getFullYear();
    const startYear = year - 100;

    const yearArr = lodash.range(year, startYear - 1, -1);
    const monthArr = lodash.range(1, 13);
    const dayArr = lodash.range(1, lastDay + 1);

    return (
        <>
            <select name={'year'} onChange={onChange} value={birth.year}>
                {yearArr.map((year, index) => {
                    return (
                        <BirthOption
                            data={year}
                            key={index}
                        />
                    )
                })}
            </select>년
            <select name={'month'} onChange={onChange} value={birth.month}>
                {monthArr.map((month, index) => {
                    return (
                        <BirthOption
                            data={month}
                            key={index}
                        />
                    )
                })}
            </select>월
            <select name={'day'} onChange={onChange} value={birth.day}>
                {dayArr.map((day, index) => {
                    return (
                        <BirthOption
                            data={day}
                            key={index}
                        />
                    )
                })}
            </select>일
        </>
    )

}

function BirthOption(props) {
    const { data } = props;

    return (<option value={data}>{data}</option>)
}

export default Join;