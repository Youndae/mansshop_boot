import { buildQueryString } from '../../../common/utils/queryStringUtils';
import { AdminProductApi } from '../api/adminProductApi';

export const getProductList = async(page, keyword) => {
    const queryString = buildQueryString({ page, keyword });
    return await AdminProductApi.getProductList(queryString);
}

export const getProductClassificationList = async() => 
	await AdminProductApi.getProductClassificationList();

const setAdminProductFormData = (
	productData,
	optionList,
	firstThumbnail,
	thumbnail,
	infoImage
) => {
	const formData = new FormData();

	formData.append('classification', productData.classification);
    formData.append('productName', productData.productName);
    formData.append('price', productData.price);
    formData.append('isOpen', productData.isOpen);
    formData.append('discount', productData.discount);
    for(let i = 0; i < optionList.length; i++) {
        formData.append('optionList[' + i + '].optionId', optionList[i].optionId);
        formData.append('optionList[' + i + '].size', optionList[i].size);
        formData.append('optionList[' + i + '].color', optionList[i].color);
        formData.append('optionList[' + i + '].optionStock', optionList[i].optionStock);
        formData.append('optionList[' + i + '].optionIsOpen', optionList[i].optionIsOpen);
    }

    thumbnail.forEach(file => formData.append('thumbnail', file));
    infoImage.forEach(file => formData.append('infoImage', file));

    if(firstThumbnail !== '')
        formData.append('firstThumbnail', firstThumbnail);

    return formData;
}


export const postProduct = async(
	productData, 
	optionList, 
	newFirstThumbnail, 
	newThumbnail, 
	newInfoImage
) => {
	const formData = setAdminProductFormData(
		productData,
		optionList,
		newFirstThumbnail,
		newThumbnail,
		newInfoImage
	);

	return await AdminProductApi.postProduct(formData);
}

export const getPatchProductData = async(productId) =>
	await AdminProductApi.getPatchProductData(productId);

export const patchProduct = async(
	productId,
	productData, 
	optionList, 
	newFirstThumbnail, 
	newThumbnail, 
	newInfoImage, 
	deleteFirstThumbnail, 
	deleteOption, 
	deleteThumbnail, 
	deleteInfoImage
) => {
	const formData = setAdminProductFormData(
		productData,
		optionList,
		newFirstThumbnail,
		newThumbnail,
		newInfoImage
	);

	//삭제되어야 할 대표 썸네일이 존재하는 경우 formData에 추가
	if(deleteFirstThumbnail !== '')
		formData.append('deleteFirstThumbnail', deleteFirstThumbnail);

	//삭제될 옵션, 썸네일, 정보 이미지 리스트를 formData에 추가
	deleteOption.forEach(deleteOptionId => formData.append('deleteOptionList', deleteOptionId));
	deleteThumbnail.forEach(file => formData.append('deleteThumbnail', file));
	deleteInfoImage.forEach(file => formData.append('deleteInfoImage', file));

	return await AdminProductApi.patchProduct(productId, formData);
}

export const getProductDetail = async(productId) =>
	await AdminProductApi.getProductDetail(productId);

export const getProductStockList = async(page, keyword) => {
	const queryString = buildQueryString({ page, keyword });
	return await AdminProductApi.getProductStockList(queryString);
}

export const getProductListByClassification = async(classificationName) =>
	await AdminProductApi.getProductListByClassification(classificationName);

export const setProductDiscount = async(productData, discount) =>{
	const productArr = [];
	productData.forEach(data => productArr.push(data.productId));

	const body = {
		productIdList: productArr,
		discount: discount,
	}

	return await AdminProductApi.setProductDiscount(body);
}

export const getDiscountProductList = async(page, keyword) => {
	const queryString = buildQueryString({ page, keyword });
	return await AdminProductApi.getDiscountProductList(queryString);
}

