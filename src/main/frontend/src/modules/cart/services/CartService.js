import { CartApi } from '../api/CartApi';
import { RESPONSE_MESSAGE } from '../../../common/constants/responseMessageType';
import { CART_SELECT_TYPE } from '../constants/CartSelectType';
import { setSelectCartDetail } from '../utils/cartUtils';

export const getCartList = async() => 
	await CartApi.getCartList();

export const updateCartCount = async(type, name, callback) => {
	try {
		const apiCall = type === 'increase' ? CartApi.increaseCartCount : CartApi.decreaseCartCount;

		const res = await apiCall(name);
		if(res.data.message === RESPONSE_MESSAGE.OK)
			callback?.();
	} catch (error) {
		console.log(error);
	}
}

export const deleteSelctCartProduct = async(type, value, callback) => {
	let selectValue = [];

	if(type === CART_SELECT_TYPE.SELECT) 
		selectValue.push(Number(value));
	else if(type === CART_SELECT_TYPE.SELECT_ALL) 
		selectValue = setSelectCartDetail(value);

	try {
		const res = await CartApi.deleteSelectCartProduct(selectValue);
		if(res.data.message === RESPONSE_MESSAGE.OK)
			callback?.();
	} catch (error) {
		console.log(error);
	}
}

export const deleteAllCartProduct = async() => 
	await CartApi.deleteAllCartProduct();

export const getOrderProductInfo = async(detailIds) => 
	await CartApi.getOrderProductInfo(detailIds);