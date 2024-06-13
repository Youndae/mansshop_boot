import React, {useEffect, useState} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";
import {useSelector} from "react-redux";

import '../../css/mypage.css';
import DefaultBtn from "../../ui/DefaultBtn";

function NonMemberOrderInfo() {
    const [params] = useSearchParams();
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const page = params.get('page') === null ? 1 : params.get('page');
    const term = params.get('term') === null ? 3 : params.get('term');
    const [inputData, setInputData] = useState({
        recipient: '',
        phone: '',
    });

    const navigate = useNavigate();

    useEffect(() => {
        if(loginStatus)
            navigate('/my-page/order');
    }, [term, page]);


    const handleInputOnChange = (e) => {
        setInputData({
            ...inputData,
            [e.target.name] : e.target.value,
        })
    }

    const handleSubmit = () => {
        navigate('/order/detail', {state: { recipient : inputData.recipient, phone: inputData.phone}});
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
                        <input type="text" name={'recipient'} onChange={handleInputOnChange} value={inputData.recipient}/>
                    </div>
                    <div>
                        <label>연락처</label>
                        <input type="text" name={'phone'} placeholder={'-를 빼고 숫자만 입력'} onChange={handleInputOnChange} value={inputData.phone}/>
                    </div>
                </div>
                <div className="order-info-btn">
                    <DefaultBtn onClick={handleSubmit} btnText={'주문 조회'} />
                </div>
            </div>
        </div>
    )
}

export default NonMemberOrderInfo;