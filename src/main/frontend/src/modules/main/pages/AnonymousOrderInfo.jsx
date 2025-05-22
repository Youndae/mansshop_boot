import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSelector } from 'react-redux';

import DefaultButton from '../../../common/components/DefaultButton';

import '../../../styles/mypage.css';

/*
    비회원의 주문 목록 조회 접근 이전
    받는사람과 연락처를 받는 페이지
    회원이 접근하는 경우 mypage의 주문 조회로 강제 이동
 */
function AnonymousOrderInfo() {
	const loginStatus = useSelector((state) => state.member.loginStatus);
	const [inputData, setInputData] = useState({
		recipient: '',
		phone: '',
	});

	const navigate = useNavigate();

	useEffect(() => {
		if(loginStatus)
			navigate('/my-page/order');
	}, [loginStatus]);

	const handleInputChange = (e) => {
		const { name, value } = e.target;
		setInputData({
			...inputData,
			[name]: value,
		});
	};

	const handleSubmit = () => {
		navigate(
			'/order/detail',
			{
				state: {
					recipient: inputData.recipient,
					phone: inputData.phone
				}
			}
		);
	}

	return (
        <div className="order-info-input">
            <div className="order-info-input-content">
                <div className="order-info-input-header">
                    <h1>주문 내역 조회</h1>
                </div>
                <div className="order-info-content-input">
                    <div>
                        <label>주문자</label>
                        <input type="text" name={'recipient'} onChange={handleInputChange} value={inputData.recipient}/>
                    </div>
                    <div>
                        <label>연락처</label>
                        <input type="text" name={'phone'} placeholder={'-를 빼고 숫자만 입력'} onChange={handleInputChange} value={inputData.phone}/>
                    </div>
                </div>
                <div className="order-info-btn">
                    <DefaultButton onClick={handleSubmit} btnText={'주문 조회'} />
                </div>
            </div>
        </div>
    )	
}

export default AnonymousOrderInfo;