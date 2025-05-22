import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { 
	getCartList, 
	updateCartCount,
	deleteSelctCartProduct,
	deleteAllCartProduct,
	getOrderProductInfo
} from '../services/CartService';
import { 
	setSelectCartDetail, 
	setAllCartDetail,
	setCheckBoxStatus
} from '../utils/cartUtils';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import { CART_SELECT_TYPE } from '../constants/CartSelectType';
import { numberComma } from '../../../common/utils/formatNumberComma';
import { getProductOption } from '../../../common/utils/productOptionUtils';

import ImageForm from '../../../common/components/ImageForm';
import DefaultButton from '../../../common/components/DefaultButton';

import '../../../styles/cart.css';

const CHECK_BOX_TYPE = {
	CHECK: 'check',
	DISABLE: 'disable'
}

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

	const getCart = async() => {
		try {
			const res = await getCartList();
			const data = res.data;

			setCartData(data);

			if(data.length !== 0) {
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
		} catch (error) {
			console.log(error);
		}
	}

	useEffect(() => {
		getCart();
	}, []);

	// 상품 수량 증가 이벤트
	const handleIncrease = (e) => {
		updateCartCount('increase', e.target.name, getCart);
	}

	// 상품 수량 감소 이벤트
	const handleDecrease = (e) => {
		const cartInput = e.target.parentElement.previousSibling.value;

		if(cartInput > 1)
			updateCartCount('decrease', e.target.name, getCart);
	}

	// 상품 제거 이벤트 ( 상품 Element에서 x 버튼을 통한 제거 제어 )
	const handleRemoveProduct = (e) => {
		deleteSelctCartProduct(CART_SELECT_TYPE.SELECT, e.target.name, getCart);
	}

	// 선택 상품 제거 버튼 이벤트
	const handleSelectRemove = () => {
		deleteSelctCartProduct(CART_SELECT_TYPE.SELECT_ALL, selectValue, getCart);
	}

	// 전체 상품 제거 버튼 이벤트
	const handleAllRemove = async () => {
		if(window.confirm('전체 상품을 삭제하시겠습니까?')) {
			try {
				const res = await deleteAllCartProduct();
				if(res.data.message === RESPONSE_MESSAGE.OK) {
					setTotalPrice(0);
					getCart();
				}
			} catch (error) {
				console.log(error);
				alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
			}
		}
	}

	// 선택 상품 주문 버튼 이벤트
	const handleSelectOrder = () => {
		const detailIds = setSelectCartDetail(selectValue);
		getOrderData(detailIds);
	}

	// 전체 상품 주문 버튼 이벤트
	const handleAllOrder = () => {
		const detailIds = setAllCartDetail(selectValue);
		getOrderData(detailIds);
	}
	
	// 주문 데이터 조회
	// 주문 페이지로 넘어가기 전 변경사항 적용을 위해 데이터 조회 후 페이지로 이동
	const getOrderData = async(detailIds) => {
		try {
			// response => res.data
			const res = await getOrderProductInfo(detailIds);
			console.log('order Data res : ', res);
			navigate(
				'/payment',
				{
					state: {
						orderProduct: res.data.orderData,
						orderType: 'cart',
						totalPrice: res.data.totalPrice,
					}
				}
			)
		} catch (error) {
			console.log(error);
		}
	}

	// 상품 check box 체크 이벤트
	const handleSelectCheckBox = (e) => {
		handleCheckBox(CHECK_BOX_TYPE.CHECK, e);
	}

	// 상품 check box 해제 이벤트
	const handleDisableCheckBox = (e) => {
		handleCheckBox(CHECK_BOX_TYPE.DISABLE, e);
	}

	// 상품 check box 이벤트 공통 처리
	const handleCheckBox = (type, e) => {
		const idx = e.target.value;
		const arr = setCheckBoxStatus({idx, selectValue});
		setSelectValue(arr);

		if(type === CHECK_BOX_TYPE.CHECK)
			setTotalPrice(totalPrice + Number(arr[idx].price));
		else
			setTotalPrice(totalPrice - Number(arr[idx].price));
	}

	return (
        <div className="cart">
            <div className="cart-content">
                <div className="cart-header">
                    <h1>장바구니</h1>
                </div>
                <div className="cart-order-btn-content">
                    <DefaultButton className={'select-productOrder-btn'} onClick={handleSelectOrder} btnText={'선택 상품 주문'} />
                    <DefaultButton className={'all-productOrder-btn'} onClick={handleAllOrder} btnText={'전체 상품 주문'} />
                    <DefaultButton className={'select-delete-btn'} onClick={handleSelectRemove} btnText={'선택 상품 삭제'} />
                    <DefaultButton className={'all-delete-btn'} onClick={handleAllRemove} btnText={'전체 상품 삭제'} />
                </div>
                <CartDetail
                    data={cartData}
					handleIncrease={handleIncrease}
					handleDecrease={handleDecrease}
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

	if(totalPrice !== 0){
		return (
            <div className="cart-total-price">
                <span className="total-price">총 {numberComma(totalPrice)} 원</span>
            </div>
        )
	}
}

function CartDetail(props) {
    const { data, 
		handleIncrease, 
		handleDecrease, 
		handleRemoveProduct, 
		handleDisableCheckBox,
		handleSelectCheckBox, 
		selectStatus 
	} = props;

	if(data.length === 0){
		return (
            <div className="content-data">
                <h3>장바구니에 담긴 상품이 없습니다.</h3>
            </div>
        )
	}

	const getDiscountTexts = (discount, originPrice) => {
		if(discount === 0)
			return { discountText: '', originPriceText: '' };

		return {
			discountText: `-${discount}%`,
			originPriceText: numberComma(originPrice),
		}
	}

	return (
		<div className="content-data">
			{data.map((cart, index) => {
				const { discountText, originPriceText } = getDiscountTexts(cart.discount, cart.originPrice);
				const productOption = getProductOption(cart);

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
								 onClick={handleRemoveProduct} alt="삭제"/>
						</div>
						<div className="cart-data-content">
							<ImageForm
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
											 onClick={handleIncrease} alt="상품 수량 증가"/>
										<img src={`${process.env.PUBLIC_URL}/image/down.jpg`}
											 name={cart.cartDetailId} onClick={handleDecrease} alt="상품 수량 감소"/>
									</div>
								</div>
								<div className="cart-price">
									<p className={'cart-option-price'}>
										{originPriceText && (
											<small className={'cart-option-price-discount'} style={{textDecoration: 'line-through'}}>{originPriceText}</small>
										)}
										{discountText && (
											<strong className={'cart-option-price-discount'}>{discountText}</strong>
										)}
										{numberComma(cart.price)} 원
									</p>
								</div>
							</div>
						</div>
					</div>
				)
			})}
		</div>
	)
}

function SelectBoxInput(props) {
    const { status, handleDisableCheckBox, handleSelectCheckBox, statusIdx } = props;

	const isChecked = status.status;
	const handleChange = isChecked ? handleDisableCheckBox : handleSelectCheckBox;

	return (
		<input type={'checkbox'} value={statusIdx} checked={isChecked} onChange={handleChange}/>
	)
}

export default Cart;