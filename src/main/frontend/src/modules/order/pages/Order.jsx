import React, {useEffect, useRef, useState} from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import DaumPostcode from 'react-daum-postcode';

import { getProductOption} from "../../../common/utils/productOptionUtils";
import { postPayment, postOrderData, orderDataValidate } from '../services/orderService';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import { numberComma } from '../../../common/utils/formatNumberComma';
import { PATTERNS, REGEX } from '../../../common/constants/patterns';

import DefaultButton from '../../../common/components/DefaultButton';

import '../../../styles/order.css';

/*
    주문 페이지
    장바구니 및 상품 페이지에서 주문 버튼 클릭 시 상품 정보를 먼저 조회한 뒤 state에 담아 주문 페이지를 호출하는 형태.
    그래서 주문 페이지에서는 별도의 상품 데이터 요청을 처리하지 않음.

    주소 입력에는 kakao 우편번호 서비스 API를 사용,
    카드 결제는 I'mport 결제 API를 사용.
*/
function Order(){
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
    const [addressOverlap, setAddressOverlap] = useState(true);
    const [recipientOverlap, setRecipientOverlap] = useState(true);
    const [phoneOverlap, setPhoneOverlap] = useState(''); // empty, inValid

    const recipientRef = useRef(null);
    const phoneRef = useRef(null);

    const navigate = useNavigate();

	useEffect(() => {
		if(state !== null) {
            setOrderProduct(state.orderProduct);
            setTotalPrice(state.totalPrice);
            setOrderType(state.orderType);

            if(state.totalPrice >= 100000)
                setDeliveryFee(0);

            const jquery = document.createElement("script");
            jquery.src = "http://code.jquery.com/jquery-1.12.4.min.js";
            const iamport = document.createElement('script');
            iamport.src = "http://cdn.iamport.kr/js/iamport.payment-1.1.7.js";
            document.head.appendChild(jquery);
            document.head.appendChild(iamport);

            return () => {
                document.head.removeChild(jquery);
                document.head.removeChild(iamport);
            }
        }else {
            navigate('/error');
        }
	}, [state]);

	// 결제 처리
	const requestPay = () => {
        const { IMP } = window;
        IMP.init('');

        const price = totalPrice;
        const name = orderData.recipient;
        const phone = orderData.phone.replaceAll(REGEX.PHONE, "$1-$2-$3");
        const payAddress = `${userAddress.address} ${userAddress.detail}`;
        const postCode = userAddress.postCode;
        let orderName = orderProduct[0].productName;
        if(orderProduct.length > 1)
            orderName = `${orderProduct[0].productName} 외 ${orderProduct.length - 1}건`;

        IMP.request_pay({
            pg: 'danal_tpay',
            pay_method: 'card',
            merchant_uid: new Date().getTime(),
            name: orderName,
            amount: price,
            buyer_email: '',
            buyer_name: name,
            buyer_tel: phone,
            buyer_addr: payAddress,
            buyer_postcode: postCode,
        }, async (res) => {
            try{
                const { data } = await postPayment(res.imp_uid);

                if(res.paid_amount === data.response.amount) {
                    alert('결제 완료');
                    await requestOrder();
                }else {
                    alert('결제 실패');
                }
            }catch (err) {
                alert('결제 오류');
            }
        })
    }

	// 주문 데이터 처리 요청
	const requestOrder = async () => {
        console.log('request Order');
		try {
			const res = await postOrderData({
				orderData, 
				userAddress, 
				orderProduct, 
				deliveryFee, 
				totalPrice, 
				paymentType, 
				orderType
			});

			if(res.data.message === RESPONSE_MESSAGE.OK) {
				alert('주문이 완료되었습니다.');
				navigate('/');
			}
		}catch (err) {
            console.log('requestOrder error');
            console.log(err);
            console.log('errorMessage : ', err.response.data.errorMessage);
            if(err.response.status === 441 || (err.response.status === 500 && err.response.data.errorMessage === 'DBConnectionError')) {
                alert('결제가 완료 되었으나 데이터 처리에 문제가 발생했습니다.\n빠르게 조치하겠습니다.\n불편을드려 죄송합니다.');
            }else{
                alert('오류가 발생했습니다.\n관리자에게 문의해주세요.');
            }
		}
	}

	// 주문 데이터 검증 요청
	const validateOrderData = async() => {
		try {

            console.log('orderProduct : ', orderProduct);

			const res = await orderDataValidate({
				orderProduct,
				totalPrice,
			});

			if(res.data.message === RESPONSE_MESSAGE.OK) {
				if(paymentType === 'card')
					requestPay();
				else
					await requestOrder();
			}else {
				alert('주문 세션이 만료되었습니다.\n다시 시도해주세요.');
				navigate('/');
			}
		}catch (err) {
			if(err.status === 440) {
				alert('주문 세션이 만료되었습니다.\n다시 시도해주세요.');
				navigate('/');
			}else {
                console.log('validateOrderData error');
                console.log(err);
				alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
			}
		}
	}

	// 결제 버튼 이벤트
	// 주문 데이터를 서버에 전송 후 검증이 정상이라면 결제 API 호출
	const handleOrderSubmit = async() => {
		if(validateInputData()) 
			await validateOrderData();
	}

	// 입력 데이터 검증
	const validateInputData = () => {

		if(userAddress.postCode === '') {
            setAddressOverlap(false);
			return false;
        }else if(orderData.recipient === ''){
            setRecipientOverlap(false);
            recipientRef.current.focus();
			return false;
        }else if(orderData.phone === '') {
            setPhoneOverlap('empty');
            phoneRef.current.focus();
			return false;
        }else if(!PATTERNS.PHONE.test(orderData.phone)){
            setPhoneOverlap('inValid');
            phoneRef.current.focus();
			return false;
        }

		return true;
	}

	// 주소 검색 팝업 창 열기
	const handlePostCodeBtn = () => {
		setIsOpen(true);
	}

	// 결제 타입 Radio 이벤트
	const handleRadioSelect = (e) => {
		const value = e.target.value;

		if(value === 'card') {
			setRadioStatus({
				card: true,
				cash: false,
			});

			setPaymentType('card');
		}else if(value === 'cash') {
			setRadioStatus({
				card: false,
				cash: true,
			});

			setPaymentType('cash');
		}
	}

	//DaumPostCode style
	const postCodeStyle = {
		width: '360px',
		height: '480px',
		padding: '15% 45%',
	}

	// 주소 검색 이후 확인 버튼 이벤트
	const handlePostCodeComplete = (data) => {
		const { address, zonecode } = data;
		setUserAddress({
			postCode: zonecode,
			address: address,
			detail: '',
		});

		setIsOpen(false);
		setAddressOverlap(true);
	}

	//주소 검색 창 닫기 이벤트
	const handleClose = (state) => {
		if(state === 'FORCE_CLOSE') 
			setIsOpen(false);
		else if(state === 'COMPLETE_CLOSE')
			setIsOpen(false);
	}

	// 상세 주소 input 입력 이벤트
	const handleAddressDetail = (e) => {
		setUserAddress({
			...userAddress,
			detail: e.target.value,
		});
	}

	//주문 정보 input 입력 이벤트
	const handleOrderData = (e) => {
		const { name, value } = e.target;
		setOrderData({
			...orderData,
			[name]: value,
		});

		if(name === 'recipient') 
			setRecipientOverlap(true);
		else if(name === 'phone')
			setPhoneOverlap('');
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
                                <input type={'text'} name={'recipient'} onChange={handleOrderData} value={orderData.recipient} ref={recipientRef}/>
                            </div>
                            <RecipientOverlap
                                status={recipientOverlap}
                            />
                        </div>
                        <div className="form-content">
                            <div className="form-content-label">
                                <label>연락처</label>
                            </div>
                            <div className="form-content-input">
                                <input type={'text'} name={'phone'} value={orderData.phone} onChange={handleOrderData} placeholder={'-를 제외한 숫자만 입력'} ref={phoneRef}/>
                            </div>
                            <PhoneOverlap
                                status={phoneOverlap}
                            />
                        </div>
                        <div className="form-content">
                            <div className="form-content-label">
                                <label>배송지 주소</label>
                            </div>
                            <div className="form-content-input-postcode">
                                <input type={'text'} name={'postCode'} placeholder={'우편번호'} value={userAddress.postCode} readOnly/>
                                <DefaultButton onClick={handlePostCodeBtn} btnText={'우편번호 찾기'}/>
                            </div>
                            <div className={'form-content-input-address'}>
                                <input type={'text'} name={'address'} placeholder={'주소'}  value={userAddress.address} readOnly/>
                            </div>
                            <div className="form-content-input-detail-address">
                                <input type={'text'} name={'detailAddress'} value={userAddress.detail} onChange={handleAddressDetail} placeholder={'상세주소'}/>
                            </div>
                            <AddrOverlap
                                status={addressOverlap}
                            />
                            {isOpen && (
                                <div className={'form-postcode'}>
                                    <DaumPostcode
                                        style={postCodeStyle}
                                        onComplete={handlePostCodeComplete}
                                        onClose={handleClose}
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
                        <DefaultButton onClick={handleOrderSubmit} btnText={'결제하기'}/>
                    </div>
                </div>
            </div>
		)
	}
}


function AddrOverlap(props) {
    const { status } = props;

    if(!status){
        return (
            <OrderOverlap
                text={'주소를 입력해주세요'}
            />
        )
    }
}

function PhoneOverlap(props) {
    const { status } = props;

	let text = '';
	if(status === 'empty')
		text = '연락처를 입력해주세요';
	else if(status === 'inValid')
		text = '유효하지 않은 연락처입니다.';

	return (
		<OrderOverlap text={text} />
	)
}

function RecipientOverlap(props) {
    const { status } = props;

    if(!status){
        return (
            <OrderOverlap
                text={'받는사람을 입력해주세요'}
            />
        )
    }
}

function OrderOverlap(props) {
    const { text } = props;

    return (
        <span style={{color: "red"}}>{text}</span>
    )
}

function OrderTotalPrice(props) {
    const { orderPrice, deliveryFee } = props;

    let deliveryFeeText = '무료';
    let totalPrice = orderPrice;
    if(totalPrice < 100000) {
        deliveryFeeText = `${numberComma(deliveryFee)} 원`;
        totalPrice += deliveryFee;
    }

    return (
        <div className="order-price">
            <span className="delivery-fee">배송비 {deliveryFeeText}</span>
            <span className="total-price">총 주문 금액 : {numberComma(totalPrice)} 원</span>
        </div>
    )
}

function OrderTableBody(props) {
    const { data } = props;

    const optionText = getProductOption({size: data.size, color: data.color});

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