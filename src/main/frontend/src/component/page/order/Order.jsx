import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";

import DaumPostcode from "react-daum-postcode";

import '../../css/order.css';

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {numberComma} from "../../../modules/numberCommaModule";
import DefaultBtn from "../../ui/DefaultBtn";

function Order() {
    const location = useLocation();
    const state = location.state;
    const [orderProduct, setOrderProduct] = useState([]);
    const [totalPrice, setTotalPrice] = useState(0);
    const [orderType, setOrderType] = useState('');
    const [paymentType, setPaymentType] = useState('card');
    const [radioStatus, setRadioStatus] = useState({
        card: true,
        cash: false,
    })
    const [userAddress, setUserAddress] = useState({
        postCode: '',
        address: '',
        detail: '',
    })
    const [isOpen, setIsOpen] = useState(false);
    const [orderData, setOrderData] = useState({
        recipient: '',
        phone: '',
        orderMemo: '',
    });
    const [deliveryFee, setDeliveryFee] = useState(3500);

    const navigate = useNavigate();

    useEffect(() => {
        if(state !== null) {
            setOrderProduct(state.orderProduct);
            setTotalPrice(state.totalPrice);
            setOrderType(state.orderType);

            if(state.totalPrice >= 100000)
                setDeliveryFee(0);
        }
    }, [state]);

    const handlePostCodeBtn = (e) => {
        setIsOpen(true);
    }

    const handleOrderSubmit = async () => {
        const addr = `${userAddress.postCode} ${userAddress.address} ${userAddress.detail}`;
        let productArr = [];

        for(let i = 0; i < orderProduct.length; i++) {
            productArr.push({
                optionId: orderProduct[i].optionId,
                productName: orderProduct[i].productName,
                productId: orderProduct[i].productId,
                detailCount: orderProduct[i].count,
                detailPrice: orderProduct[i].price,
            })
        }

        await axiosInstance.post(`order/`, {
            recipient: orderData.recipient,
            phone: orderData.phone,
            orderMemo: orderData.orderMemo,
            address: addr,
            orderProduct: productArr,
            deliveryFee: deliveryFee,
            totalPrice: totalPrice,
            paymentType: paymentType,
            orderType: orderType,
        }, {
            headers: {'Content-Type': 'application/json'}
        })
            .then(res => {
                /*주문 결과 페이지 생성 후 연결*/
                if(checkResponseMessageOk(res))
                    navigate('/');

            })
            .catch(err => {
                console.error('productOrder axios error : ', err);
            })

    }

    const handleRadioSelect = (e) => {
        const name = e.target.value;

        if(name === 'card'){
            setRadioStatus({
                card: true,
                cash: false,
            })

            setPaymentType('card');
        }else if(name === 'cash'){
            setRadioStatus({
                card: false,
                cash: true,
            })
            setPaymentType('cash');
        }
    }

    const postCodeStyle = {
        width: '360px',
        height: '480px',
        padding: '15% 45%',
    }

    const handlePostCodeComplete = (data) => {
        const { address, zonecode } = data;
        setUserAddress({
            postCode: zonecode,
            address: address,
            detail: '',
        });

        setIsOpen(false);
    }

    const handleClosed = (state) => {
        if (state === 'FORCE_CLOSE') {
            setIsOpen(false);
        } else if (state === 'COMPLETE_CLOSE') {
            setIsOpen(false);
        }
    }

    const handleAddressDetail = (e) => {
        setUserAddress({
            ...userAddress,
            detail: e.target.value,
        })
    }

    const handleOrderData = (e) => {
        setOrderData({
            ...orderData,
            [e.target.name]: e.target.value,
        });
    }

    if(state === null)
        return null;
    else {
        return (
            <div className="order">
                <div className="order-header">
                    <h1>상품 결제</h1>
                </div>
                <div className="order-content">
                    <div className="order-form">
                        <div className="form-content">
                            <div className="form-content-label">
                                <label>수령인</label>
                            </div>
                            <div className="form-content-input">
                                <input type={'text'} name={'recipient'} onChange={handleOrderData} value={orderData.recipient}/>
                            </div>
                        </div>
                        <div className="form-content">
                            <div className="form-content-label">
                                <label>연락처</label>
                            </div>
                            <div className="form-content-input">
                                <input type={'text'} name={'phone'} value={orderData.phone} onChange={handleOrderData} placeholder={'-를 제외한 숫자만 입력'}/>
                            </div>
                        </div>
                        <div className="form-content">
                            <div className="form-content-label">
                                <label>배송지 주소</label>
                            </div>
                            <div className="form-content-input-postcode">
                                <input type={'text'} name={'postCode'} placeholder={'우편번호'} value={userAddress.postCode} readOnly/>
                                <DefaultBtn onClick={handlePostCodeBtn} btnText={'우편번호 찾기'}/>
                            </div>
                            <div className={'form-content-input-address'}>
                                <input type={'text'} name={'address'} placeholder={'주소'}  value={userAddress.address} readOnly/>
                            </div>
                            <div className="form-content-input-detail-address">
                                <input type={'text'} name={'detailAddress'} value={userAddress.detail} onChange={handleAddressDetail} placeholder={'상세주소'}/>
                            </div>
                            {isOpen && (
                                <div className={'form-postcode'}>
                                    <DaumPostcode
                                        style={postCodeStyle}
                                        onComplete={handlePostCodeComplete}
                                        onClose={handleClosed}
                                    />
                                </div>
                            )}
                        </div>
                        <div className="form-content">
                            <div className="form-content-label">
                                <label>배송 메모</label>
                            </div>
                            <div className="form-content-input">
                                <input type={'text'} name={'orderMemo'} value={orderData.orderMemo} onChange={handleOrderData}/>
                            </div>
                        </div>
                    </div>
                    <table className="order-table" border={1}>
                        <thead>
                        <tr>
                            <th>상품명</th>
                            <th>옵션</th>
                            <th>수량</th>
                            <th>가격</th>
                        </tr>
                        </thead>
                        <tbody>
                        {orderProduct.map((product, index) => {
                            return (
                                <OrderTableBody
                                    key={index}
                                    data={product}
                                />
                            )
                        })}
                        </tbody>
                    </table>
                    <OrderTotalPrice
                        orderPrice={totalPrice}
                        deliveryFee={deliveryFee}
                    />
                    <div className="order-payment">
                        <div className="form-content-label">
                            <label>결제 수단</label>
                        </div>
                        <div>
                            <input type={'radio'} value={'card'} checked={radioStatus.card} onChange={handleRadioSelect}/>신용카드
                            <input type={'radio'} value={'cash'} checked={radioStatus.cash} onChange={handleRadioSelect}/>무통장 입금
                        </div>
                    </div>
                    <div className="order-payment-btn">
                        <DefaultBtn onClick={handleOrderSubmit} btnText={'결제하기'}/>
                    </div>
                </div>
            </div>
        )
    }
}

function OrderTotalPrice(props) {
    const { orderPrice, deliveryFee } = props;

    let deliveryFeeText = '무료';
    let totalPrice = orderPrice;
    if(totalPrice < 100000)
        deliveryFeeText = `${numberComma(deliveryFee)} 원`;
    else
        totalPrice += deliveryFee;

    return (
        <div className="order-price">
            <span className="delivery-fee">배송비 {deliveryFeeText}</span>
            <span className="total-price">총 주문 금액 : {numberComma(totalPrice)} 원</span>
        </div>
    )
}

function OrderTableBody(props) {
    const { data } = props;
    let optionText = '';
    const sizeText = `사이즈 : ${data.size}`;
    const colorText = `색상 : ${data.color}`;

    if(data.size === null){
        if(data.color !== null) {
            optionText = colorText;
        }
    }else {
        if(data.color !== null) {
            optionText = `${sizeText} ${colorText}`;
        }else {
            optionText = sizeText;
        }
    }


    return (
        <tr>
            <td>{data.productName}</td>
            <td>{optionText}</td>
            <td>{data.count}</td>
            <td>{numberComma(data.price)} 원</td>
        </tr>
    )
}

export default Order;