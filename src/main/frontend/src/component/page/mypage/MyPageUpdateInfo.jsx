import React, {useEffect, useRef, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import DefaultBtn from "../../ui/DefaultBtn";
import Overlap from "../../ui/Overlap";

function MyPageUpdateInfo() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
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

    const dispatch = useDispatch();

    const nicknameElem = useRef(null);
    const phoneElem = useRef(null);
    const mailElem = useRef(null);

    const emailPattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
    const phonePattern = /^01(?:0|1|6|9)([0-9]{3,4})([0-9]{4})$/;

    const responseDuplicatedMessage = 'duplicated';
    const responseNoDuplicatesMessage = 'No duplicates';

    useEffect(() => {
        getUserInfo();
    }, []);

    const getUserInfo = async () => {

        await axiosInstance.get(`my-page/info`)
            .then(res => {
                console.log('myPage get info res : ', res);
                const contentData = res.data.content;

                setUserData({
                    nickname: contentData.nickname,
                    phone: contentData.phone,
                    email: contentData.mailPrefix,
                });

                setEmailProvider(contentData.mailType);
                setEmailSuffix(contentData.mailSuffix);


                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('productQnA error : ', err);
            })
    }

    const handleOnChange = (e) => {
        setUserData({
            ...userData,
            [e.target.name]: e.target.value,
        });

        const targetName = e.target.name;

        if(targetName === 'nickname') {
            setNicknameCheckInfo(false);
        }else if(targetName === 'phone') {
            const phoneValue = e.target.value;
            if(!phonePattern.test(phoneValue))
                setPhoneCheck('invalid');
            else
                setPhoneCheck('valid');
        }
    }

    const handleNicknameCheck = async () => {
        if(userData.nickname === '')
            setNicknameCheck('empty');
        else{
            await axiosInstance.get(`member/check-nickname?nickname=${userData.nickname}`)
                .then(res => {
                    const responseMessage = res.data.message;

                    if(responseMessage === responseDuplicatedMessage){
                        setNicknameCheckInfo(false);
                        setNicknameCheck('duplication');
                    }else if(responseMessage === responseNoDuplicatesMessage){
                        setNicknameCheckInfo(true);
                        setNicknameCheck('valid');
                    }

                })
                .catch(() => {
                    setNicknameCheck('err');
                })
        }
    }

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

    const handleEmailSuffixChange = (e) => {
        const val = e.target.value;

        setEmailCheck('');

        setEmailSuffix(val);
    }

    const handleSubmit = async () => {
        const userEmail = userData.email + '@' + emailSuffix;

        console.log('userEmail : ', userEmail);

        if(!nicknameCheckInfo && userData.nickname !== '') {
            setNicknameCheck('notDuplicateCheck');
            nicknameElem.current.focus();
        }else if(!emailPattern.test(userEmail)) {
            setEmailCheck('invalid');
            mailElem.current.focus();
        }else if(!phonePattern.test(userData.phone)){
            setPhoneCheck('invalid');
            phoneElem.current.focus();
        }else {

            await axiosInstance.patch(`my-page/info`, {
                nickname: userData.nickname,
                phone: userData.phone,
                mail: userEmail,
            })
                .then(res => {
                    if(checkResponseMessageOk(res)) {
                        alert('정보 수정이 완료되었습니다.');
                        getUserInfo();
                    }
                })
                .catch(err => {
                    console.error('join error ', err);
                    alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요');
                })

        }
    }

    console.log('mailProvider Type : ', emailProvider);

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
                            <DefaultBtn className={'nickname-check-btn'} onClick={handleNicknameCheck} btnText={'중복체크'} />
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
                            <UserNameOverlap
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
                <DefaultBtn className={'info-submit-btn'} onClick={handleSubmit} btnText={'수정'}/>
            </div>
        </div>
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

function EmailProvider (props) {
    const { providerStatus, handleInputChange, emailSuffix } = props;

    if(providerStatus === 'none'){
        return (
            <input type={'text'} name={'email-suffix-input'} value={emailSuffix} onChange={handleInputChange}/>
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

export default MyPageUpdateInfo;