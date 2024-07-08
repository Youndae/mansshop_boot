import React, {useEffect, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {setMemberObject} from "../../../modules/loginModule";
import {numberComma} from "../../../modules/numberCommaModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import DefaultBtn from "../../ui/DefaultBtn";
import Image from "../../ui/Image";

/*
        useParams로 productId를 받는다.

        분류, 상품명, 옵션 정보 테이블(사이즈, 컬러, 재고, 공개여부), 가격, 공개여부, 판매량을 보여준다.
        상단에는 상품 수정 및 공개 || 비공개 처리 버튼을 배치한다.
        가장 최근 판매일도 넣을까???

     */
function AdminProductDetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { productId } = useParams();

    const [productData, setProductData] = useState({
        classification: '',
        productName: '',
        price: '',
        isOpen: false,
        sales: 0,
        discount: 0,
        discountPrice: 0,
    });
    const [optionList, setOptionList] = useState([]);
    const [firstThumbnail, setFirstThumbnail] = useState('');
    const [thumbnail, setThumbnail] = useState([]);
    const [infoImage, setInfoImage] = useState([]);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getDetail(productId);
    }, [productId]);

    const getDetail = async (productId) => {

        await axiosInstance.get(`admin/product/detail/${productId}`)
            .then(res => {
                const dataContent = res.data.content;
                const disCountPrice = dataContent.price * (1 - dataContent.discount / 100);

                setProductData({
                    classification: dataContent.classification,
                    productName: dataContent.productName,
                    price: dataContent.price,
                    isOpen: dataContent.isOpen,
                    sales: dataContent.sales,
                    discount: dataContent.discount,
                    discountPrice: disCountPrice,
                })

                setOptionList(dataContent.optionList);
                setFirstThumbnail(dataContent.firstThumbnail);
                setThumbnail(dataContent.thumbnailList);
                setInfoImage(dataContent.infoImageList);

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleUpdateBtnOnClick = () => {
        navigate(`/admin/product/update/${productId}`);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>상품 정보</h1>
                </div>
                <div className="admin-content-content">
                    <div className="admin-detail-header">
                        <h2>{productData.productName}</h2>
                        <DefaultBtn
                            btnText={'수정'}
                            onClick={handleUpdateBtnOnClick}
                        />
                    </div>
                    <div className="admin-detail-content">
                        <div>
                            <p>분류 : {productData.classification}</p>
                            <p>가격 : {numberComma(productData.price)}</p>
                            <p>공개 여부 : {productData.isOpen ? '공개' : '비공개'}</p>
                            <p>판매량 : {numberComma(productData.sales)}</p>
                            <p>할인율 : {productData.discount}% (판매가 : {numberComma(productData.discountPrice)})</p>
                        </div>
                        <div className="option-test">
                            {optionList.map((data, index) => {
                                return (
                                    <div key={index} className="option-detail">
                                        <span>사이즈 : {data.size}</span>
                                        <span>컬러 : {data.color}</span>
                                        <span>재고 : {data.optionStock}</span>
                                        <span>옵션 공개 여부 : {data.optionIsOpen ? '공개' : '비공개'}</span>
                                    </div>
                                )
                            })}
                        </div>
                        <div className="detail-first-thumbnail">
                            <h3>대표 썸네일</h3>
                            <Image
                                imageName={firstThumbnail}
                            />
                        </div>
                        <div className="detail-thumbnail">
                            <h3>썸네일</h3>
                            {thumbnail.map((image, index) => {
                                return (
                                    <Image
                                        key={index}
                                        imageName={image}
                                    />
                                )
                            })}
                        </div>
                        <div className="detail-info-image">
                            <h3>상세 정보 이미지</h3>
                            {infoImage.map((image, index) => {
                                return (
                                    <Image
                                        key={index}
                                        imageName={image}
                                    />
                                )
                            })}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )

}

export default AdminProductDetail;