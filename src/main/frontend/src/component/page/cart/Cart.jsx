import React, {useEffect, useState} from 'react';
import {useNavigate} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {numberComma} from "../../../modules/numberCommaModule";

import Image from "../../ui/Image";
import DefaultBtn from "../../ui/DefaultBtn";

import '../../css/cart.css';

/*
    장바구니 페이지
    선택 상품 주문, 전체 상품 주문, 선택 상품 삭제, 전체 상품 삭제, 상품별 수량 제어 기능
 */
function Cart() {
    const [cartData, setCartData] = useState([]);
    const [selectValue, setSelectValue] = useState([{
        productId: '',
        cartDetailId: '',
        size: '',
        color: '',
        count: '',
        originPrice: '',
        price: '',
        discount: 0,
        status: true,
    }]);
    const [totalPrice, setTotalPrice] = useState(0);

    const navigate = useNavigate();

    useEffect(() => {
        getCartData();
    }, []);

    //장바구니 데이터 조회
    const getCartData = async () => {
        await axiosInstance.get(`cart/`)
            .then(res => {
                const data = res.data;
                setCartData(data);

                if(data.length !== 0){
                    if(selectValue[0].cartDetailId === '') {
                        let selectArr = [];
                        for (let i = 0; i < data.length; i++) {
                            selectArr.push({
                                productId: data[i].productId,
                                cartDetailId: data[i].cartDetailId,
                                optionId: data[i].optionId,
                                size: data[i].size,
                                color: data[i].color,
                                count: data[i].count,
                                originPrice: data[i].originPrice,
                                price: data[i].price,
                                discount: data[i].discount,
                                status: true
                            });
                        }

                        setSelectValue(selectArr);
                    }
                    let totalPrice = 0;
                    for(let i = 0; i < data.length; i++)
                        totalPrice += data[i].price;

                    setTotalPrice(totalPrice);
                }
            })
    }

    //상품 수량 증가 이벤트
    const handleCountUp = async (e) => {
        countRequest('cart/count-up', e.target.name);
    }

    //상품 수량 감소 이벤트
    const handleCountDown = async (e) => {
        const cartInput = e.target.parentElement.previousSibling.value;

        if(cartInput > 1)
            countRequest('cart/count-down', e.target.name);
    }

    //상품 제거 이벤트 ( 상품 Element 중 x 버튼을 통한 제거 )
    const handleRemoveProduct = (e) => {
        const arr = [];
        arr.push(Number(e.target.name));
        removeRequest(`cart/select`, arr);
    }

    //선택 상품 제거 버튼 이벤트
    const handleSelectRemove = () => {
        const selectProduct = setSelectCartDetail();
        removeRequest(`cart/select`, selectProduct);
    }

    //전체 상품 제거 버튼 이벤트
    const handleAllRemove = () => {
        removeRequest(`cart/all`, null);
    }

    //선택 상품 주문 버튼 이벤트
    const handleSelectOrder = () => {
        let detailIds = [];
        for(let i = 0; i < selectValue.length; i++)
            if(selectValue[i].status)
                detailIds.push(selectValue[i].cartDetailId)

        getOrderData(detailIds);
    }

    //전체 상품 주문 버튼 이벤트
    const handleAllOrder = () => {
        let detailIds = [];
        for(let i = 0; i < cartData.length; i++)
            detailIds.push(selectValue[i].cartDetailId)

        getOrderData(detailIds);
    }

    //주문 데이터 조회
    //요청 시점에 따라 할인율 등 변경 사항을 제대로 반영하기 위해 주문 페이지로 넘길 데이터 사전 조회
    const getOrderData = async (detailIds) => {
        await axiosInstance.post(`order/cart`, detailIds)
            .then(res => {
                const dataList = res.data.orderData;
                const orderTotalPrice = res.data.totalPrice;

                getOrderObject(dataList);

                navigate('/productOrder', {state : {orderProduct: dataList, orderType: 'cart', totalPrice: orderTotalPrice}});
            })
    }

    //주문 페이지에 넘길 객체 생성
    const getOrderObject = (data) => {
        return {
            productId: data.productId,
            optionId: data.optionId,
            productName: data.productName,
            size: data.size,
            color: data.color,
            count: data.count,
            price: data.price
        }
    }

    //상품 수량 증감 요청
    const countRequest = async (reqUrl, data) => {
        await axiosInstance.patch(`${reqUrl}/${data}`, {})
            .then(res => {
                if(checkResponseMessageOk(res))
                    getCartData();
            })
    }

    //상품 제거 요청
    const removeRequest = async (reqUrl, reqData) => {
        if(reqData === null){
            await axiosInstance.delete(`${reqUrl}`)
                .then(res => {
                    if(checkResponseMessageOk(res)) {
                        setTotalPrice(0);
                        getCartData();
                    }
                })
        }else {
            await axiosInstance.delete(`${reqUrl}`, {
                data : reqData,
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(res => {
                    if(checkResponseMessageOk(res))
                        getCartData();
                })
        }
    }

    //선택된 상품 데이터 배열 반환
    const setSelectCartDetail = () => {
        let arr = [...selectValue];
        let resultArr = [];
        for(let i = 0; i < arr.length; i++){
            if(arr[i].status)
                resultArr.push(Number(arr[i].cartDetailId));
        }

        return resultArr;
    }

    //상품 checkbox 체크 해제 이벤트
    const handleDisableCheckBox = (e) => {
        const idx = e.target.value;
        const arr = [...selectValue];
        arr[idx] = {
            ...arr[idx],
            status: false,
        }

        setSelectValue(arr);
        setTotalPrice(totalPrice - Number(arr[idx].price));
    }

    //상품 checkbox 체크 이벤트
    const handleSelectCheckBox = (e) => {
        const idx = e.target.value;
        const arr = [...selectValue];
        arr[idx] = {
            ...arr[idx],
            status: true,
        }

        setSelectValue(arr);
        setTotalPrice(totalPrice + Number(arr[idx].price));
    }


    return (
        <div className="cart">
            <div className="cart-content">
                <div className="cart-header">
                    <h1>장바구니</h1>
                </div>
                <div className="cart-order-btn-content">
                    <DefaultBtn className={'select-productOrder-btn'} onClick={handleSelectOrder} btnText={'선택 상품 주문'} />
                    <DefaultBtn className={'all-productOrder-btn'} onClick={handleAllOrder} btnText={'전체 상품 주문'} />
                    <DefaultBtn className={'select-delete-btn'} onClick={handleSelectRemove} btnText={'선택 상품 삭제'} />
                    <DefaultBtn className={'all-delete-btn'} onClick={handleAllRemove} btnText={'전체 상품 삭제'} />
                </div>
                <CartDetail
                    data={cartData}
                    handleCountUp={handleCountUp}
                    handleCountDown={handleCountDown}
                    handleRemoveProduct={handleRemoveProduct}
                    handleDisableCheckBox={handleDisableCheckBox}
                    handleSelectCheckBox={handleSelectCheckBox}
                    selectStatus={selectValue}
                />
                <TotalPrice
                    totalPrice={totalPrice}
                />
            </div>
        </div>
    )
}

function TotalPrice(props) {
    const { totalPrice } = props;

    if(totalPrice === 0){
        return null;
    }else {
        return (
            <div className="cart-total-price">
                <span className="total-price">총 {numberComma(totalPrice)} 원</span>
            </div>
        )
    }
}

function CartDetail(props) {
    const { data, handleCountUp, handleCountDown, handleRemoveProduct, handleDisableCheckBox, handleSelectCheckBox, selectStatus } = props;

    if(data.length === 0){
        return (
            <div className="content-data">
                <h3>장바구니에 담긴 상품이 없습니다.</h3>
            </div>
        )
    }else {
        return (
            <div className="content-data">
                {data.map((cart, index) => {
                    const productSize = `사이즈 : ${cart.size}`;
                    const productColor = `컬러 : ${cart.color}`;
                    let productOption = '';

                    if (cart.size == null) {
                        if (cart.color != null)
                            productOption = productColor;
                    } else {
                        if (cart.color == null)
                            productOption = productSize;
                        else
                            productOption = `${productSize} ${productColor}`;
                    }

                    let discountText = '';
                    let originPriceText = '';
                    if(cart.discount !== 0){
                        discountText = `-${cart.discount}%`;
                        originPriceText = `${numberComma(cart.originPrice)}`;
                    }


                    return (
                        <div key={index} className="cart-data">
                            <div className="cart-data-header">
                                <SelectBoxInput
                                    status={selectStatus[index]}
                                    handleDisableCheckBox={handleDisableCheckBox}
                                    handleSelectCheckBox={handleSelectCheckBox}
                                    statusIdx={index}
                                />
                                <span className={'product-name'}>{cart.productName}</span>
                                <img src={`${process.env.PUBLIC_URL}/image/del.jpg`} name={cart.cartDetailId}
                                     onClick={handleRemoveProduct}/>
                            </div>
                            <div className="cart-data-content">
                                <Image
                                    imageName={cart.productThumbnail}
                                    className={'cart-thumbnail'}
                                />
                                <div className="cart-info">
                                    <span className="productOption">{productOption}</span>

                                    <div className="cart-input-content">
                                        <input type={'text'} className={'cart-input'} value={cart.count}
                                               readOnly={true}/>
                                        <div className="cart-count">
                                            <img src={`${process.env.PUBLIC_URL}/image/up.jpg`} name={cart.cartDetailId}
                                                 onClick={handleCountUp}/>
                                            <img src={`${process.env.PUBLIC_URL}/image/down.jpg`}
                                                 name={cart.cartDetailId} onClick={handleCountDown}/>
                                        </div>
                                    </div>
                                    <div className="cart-price">
                                        <p className={'cart-option-price'}><p className={'cart-option-price-discount'} style={{textDecoration: 'line-through'}}>{originPriceText}</p><p className={'cart-option-price-discount'}>{discountText}</p>{numberComma(cart.price)} 원</p>
                                    </div>
                                </div>
                            </div>

                        </div>
                    )
                })}
            </div>
        )
    }
}

function SelectBoxInput(props) {
    const { status, handleDisableCheckBox, handleSelectCheckBox, statusIdx } = props;

    if(status.status){
        return (
            <>
                <input type={'checkbox'} value={statusIdx} checked={true} onChange={handleDisableCheckBox}/>
            </>
        )
    }else {
        return (
            <>
                <input type={'checkbox'} value={statusIdx} checked={false} onChange={handleSelectCheckBox}/>
            </>
        )
    }
}

export default Cart;