import { OrderApi } from '../api/orderApi';

export const postPayment = async (val) => 
	await OrderApi.postPayment(val);

export const postOrderData = async ({
	orderData, 
	userAddress, 
	orderProduct, 
	deliveryFee, 
	totalPrice, 
	paymentType, 
	orderType
}) => {
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

	const body = {
		recipient: orderData.recipient,
		phone: orderData.phone,
		orderMemo: orderData.orderMemo,
		address: addr,
		orderProduct: productArr,
		deliveryFee: deliveryFee,
		totalPrice: totalPrice,
		paymentType: paymentType,
		orderType: orderType,
	}

	return await OrderApi.postOrderData(body);
}

export const orderDataValidate = async ({orderProduct, totalPrice}) => {
	let productArr = [];
	for(let i = 0; i < orderProduct.length; i++) {
		productArr.push({
			productId: orderProduct[i].productId,
			optionId: orderProduct[i].optionId,
			productName: orderProduct[i].productName,
			size: orderProduct[i].size,
			color: orderProduct[i].color,
			count: orderProduct[i].count,
			price: orderProduct[i].price,
		})
	}

	const body = {
		orderData: productArr,
		totalPrice: totalPrice,
	}

	return await OrderApi.validateOrderData(body);
}