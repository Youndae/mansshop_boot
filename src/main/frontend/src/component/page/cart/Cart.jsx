import React, {useEffect, useState} from 'react';
import {axiosInstance} from "../../../modules/customAxios";
import {useDispatch, useSelector} from "react-redux";
import {setMemberObject} from "../../../modules/loginModule";

import '../../css/cart.css';
import {useNavigate} from "react-router-dom";

import {numberComma} from "../../../modules/numberCommaModule";

function Cart() {
    const [cartData, setCartData] = useState([]);
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const dispatch = useDispatch();
    const [selectValue, setSelectValue] = useState([{
        productId: '',
        cartDetailId: '',
        size: '',
        color: '',
        count: '',
        price: '',
        status: true,
    }]);
    const [totalPrice, setTotalPrice] = useState(0);

    const navigate = useNavigate();

    useEffect(() => {
        getCartData();
    }, []);

    const getCartData = async () => {
        await axiosInstance.get(`cart/`, {
            headers: {
                'Content-Type':  'application/json',
            }
        })
            .then(res => {
                setCartData(res.data.content);

                console.log('getCart Axios res : ', res);

                const data = res.data.content;
                if(data !== undefined ){
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
                                price: data[i].price,
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

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.log('cart error : ', err);
            })
    }

    const handleCountUp = async (e) => {
        countRequest('cart/count-up', e.target.name);
    }

    const handleCountDown = async (e) => {
        const cartInput = e.target.parentElement.previousSibling.value;

        if(cartInput > 1)
            countRequest('cart/count-down', e.target.name);
    }

    const handleRemoveProduct = (e) => {
        const arr = [];
        arr.push(Number(e.target.name));
        removeRequest(`cart/select`, arr);
    }

    const handleSelectRemove = () => {
        const selectProduct = setSelectCartDetail();
        removeRequest(`cart/select`, selectProduct);
    }

    const handleAllRemove = () => {
        removeRequest(`cart/all`, null);
    }

    const handleSelectOrder = () => {
        let resultArr = [];
        for(let i = 0; i < selectValue.length; i++)
            if(selectValue[i].status)
                resultArr.push(getOrderObject(i))

        navigateOrder(resultArr);
    }

    const handleAllOrder = () => {
        let resultArr = [];
        for(let i = 0; i < cartData.length; i++)
            resultArr.push(getOrderObject(i))

        navigateOrder(resultArr);
    }

    const getOrderObject = (idx) => {
        return {
            productId: cartData[idx].productId,
            optionId: cartData[idx].optionId,
            productName: cartData[idx].productName,
            size: cartData[idx].size,
            color: cartData[idx].color,
            count: cartData[idx].count,
            price: cartData[idx].price
        }
    }

    const navigateOrder = (data) => {
        navigate('/productOrder', {state : {orderProduct: data, orderType: 'cart', totalPrice: totalPrice}});
    }

    const countRequest = async (reqUrl, data) => {
        await axiosInstance.patch(`${reqUrl}/${data}`, {}, {
            headers: {'Content-Type': 'application/json'}
        })
            .then(res => {
                if(res.data.message === 'success')
                    getCartData();
            })
            .catch(err => {
                console.error('count axios error : ', err);
            })
    }

    const removeRequest = async (reqUrl, reqData) => {
        if(reqData === null){
            await axiosInstance.delete(`${reqUrl}`)
                .then(res => {
                    if(res.data.message === 'success') {
                        setTotalPrice(0);
                        getCartData();
                    }
                })
                .catch(err => {
                    console.error('delete axios error : ', err);
                })
        }else {
            await axiosInstance.delete(`${reqUrl}`, {
                data : {
                    ...reqData,
                },
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(res => {
                    if(res.data.message === 'success')
                        getCartData();
                })
                .catch(err => {
                    console.error('delete axios error : ', err);
                })
        }
    }

    const setSelectCartDetail = () => {
        let arr = [...selectValue];
        let resultArr = [];
        for(let i = 0; i < arr.length; i++){
            if(arr[i].status)
                resultArr.push(Number(arr[i].cartDetailId));
        }

        return resultArr;
    }

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
                    <button type={'button'} className={'select-productOrder-btn'} onClick={handleSelectOrder}>선택 상품 주문</button>
                    <button type={'button'} className={'all-productOrder-btn'} onClick={handleAllOrder}>전체 상품 주문</button>
                    <button type={'button'} className={'select-delete-btn'} onClick={handleSelectRemove}>선택 상품 삭제</button>
                    <button type={'button'} className={'all-delete-btn'} onClick={handleAllRemove}>전체 상품 삭제</button>
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

    if(data === undefined){
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
                                <CartImage
                                    imageName={cart.productThumbnail}
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
                                        <p className={'cart-option-price'}>{numberComma(cart.price)} 원</p>
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

function CartImage(props) {
    const { imageName } = props;
    const [thumb, setThumb] = useState('');

    useEffect(() => {
        getDisplayImage(imageName);
    }, [imageName]);

    const getDisplayImage = async (imageName) => {
        await axiosInstance.get(`main/display/${imageName}`, {
            headers: {
                'Content-Type': 'application/json',
            },
            responseType: 'blob',
        })
            .then(res => {
                const url = window.URL.createObjectURL(
                    new Blob ([res.data], { type: res.headers['content-type']})
                );

                setThumb(url);
            })
            .catch(err => {
                console.log('display axios err : ', err);
            })
    }

    return (
        <>
            <img src={thumb} alt={''} className={'cart-thumbnail'}/>
        </>
    )
}

export default Cart;