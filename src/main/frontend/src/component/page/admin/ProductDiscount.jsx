import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {numberComma} from "../../../modules/numberCommaModule";
import {setMemberObject} from "../../../modules/loginModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import DefaultBtn from "../../ui/DefaultBtn";


function ProductDiscount() {
    const loginStatus = useSelector((state) => state.member.loginStatus);

    const [classification, setClassification] = useState([]);
    const [product, setProduct] = useState([]);
    const [selectProductData, setSelectProductData] = useState([]);
    const [selectClassificationValue, setSelectClassificationValue] = useState('default');
    const [selectProductValue, setSelectProductValue] = useState('default');
    const [discount, setDiscount] = useState(0);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getClassification();
    }, []);

    const getClassification = async () => {

        await axiosInstance.get(`admin/product/classification`)
            .then(res => {
                setClassification(res.data.content);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const getSelectProduct = async (classificationName) => {

        await axiosInstance.get(`admin/product/discount/select/${classificationName}`)
            .then(res => {
                setProduct(res.data.content);
            })
    }

    const handleClassificationOnChange = (e) => {
        const value = e.target.value;

        setSelectClassificationValue(value);
        setSelectProductValue('default');

        getSelectProduct(value);
    }

    // value = index
    // product[index] 활용
    const handleProductOnChange = (e) => {
        const idx = e.target.value;
        const productData = product[idx];
        const productArr = [...selectProductData];
        productArr.push(productData);

        setSelectProductValue(idx);
        setSelectProductData(productArr);
    }

    const handleDiscountOnChange = (e) => {
        setDiscount(Number(e.target.value));
    }

    const handleDiscountSubmit = async () => {
        if(selectProductData.length === 0){
            alert('상품을 선택해주세요');
        }else {
            let productIdArr = [];
            selectProductData.forEach(data => productIdArr.push(data.productId));

            await axiosInstance.patch(`admin/product/discount`, {
                productIdList: productIdArr,
                discount : discount,
            }, {
                headers: {
                    'Content-Type': 'application/json',
                }
            })
                .then(res => {
                    if(checkResponseMessageOk(res))
                        navigate('/admin/product/discount');
                })
        }
    }

    const handleDeleteDiscountProduct = (e) => {
        const idx = e.target.value;
        const selectProductArr = [...selectProductData];
        selectProductArr.splice(idx, 1);
        setSelectProductData(selectProductArr);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>할인 설정</h1>
                    <DefaultBtn
                        btnText={'할인 적용'}
                        onClick={handleDiscountSubmit}
                        className={'discount-btn'}
                    />
                </div>
                <div className="admin-content-content">
                    <div className="discount-content-header">
                        <div className="discount-classification-select">
                            <label>상품 분류 : </label>
                            <select className={'discount-classification-select-box'} value={selectClassificationValue} onChange={handleClassificationOnChange}>
                                <option value={'default'} disabled={true}>상품 분류를 선택해주세요</option>
                                {classification.map((data, index) => {
                                    return (
                                        <option key={index} value={data}>{data}</option>
                                    )
                                })}
                            </select>
                        </div>
                        <div className="discount-product-select">
                            <label>상품 : </label>
                            <select className={'discount-product-select-box'} value={selectProductValue} onChange={handleProductOnChange}>
                                <option value={'default'} disabled={true}>상품 분류를 먼저 선택해주세요</option>
                                {product.map((data, index) => {
                                    return (
                                        <option key={data.productId} value={index}>{data.productName}</option>
                                    )
                                })}
                            </select>
                        </div>
                    </div>
                    <DiscountContent
                        data={selectProductData}
                        discount={discount}
                        handleDiscountOnChange={handleDiscountOnChange}
                        handleDeleteDiscountProduct={handleDeleteDiscountProduct}
                    />
                </div>
            </div>
        </div>
    )
}

function DiscountContent(props) {
    const { data, discount, handleDiscountOnChange, handleDeleteDiscountProduct } = props;

    return (
        <div className="discount-content-content">
            <div className="discount-input">
                <label>할인율 (%) : </label>
                <input type={'number'} value={discount} onChange={handleDiscountOnChange}/>
            </div>
            <div className="discount-product">
                {data.map((product, index) => {
                    const discountPrice = Number(Math.ceil(product.productPrice * (1 - (discount / 100))));
                    return (
                        <div key={index} className="discount-product-content">
                            <div className="discount-product-content-header">
                                <h3>{product.productName}</h3>
                                <DefaultBtn
                                    btnText={'삭제'}
                                    onClick={handleDeleteDiscountProduct}
                                    value={index}
                                />
                            </div>
                            <div className="discount-product-content-content">
                                <div className="form-group">
                                    <label className="discount-product-label">가격 : </label>
                                    <span className="discount-product-price">{numberComma(product.productPrice)}</span>
                                </div>
                                <div className="form-group">
                                    <label className="discount-product-label">할인 적용가 : </label>
                                    <span className="discount-product-price">{numberComma(discountPrice)}</span>
                                </div>
                            </div>
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default ProductDiscount;