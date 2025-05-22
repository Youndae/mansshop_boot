import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import { getProductDetail } from '../../services/adminProductService';
import { numberComma } from '../../../../common/utils/formatNumberComma';

import AdminSideNav from '../../components/AdminSideNav';
import DefaultButton from '../../../../common/components/DefaultButton';
import ImageForm from '../../../../common/components/ImageForm';

//상품 정보 페이지
function AdminProductDetail() {
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

    const navigate = useNavigate();

	useEffect(() => {
		const getDetail = async() => {
			try {
				const res = await getProductDetail(productId);

				const dataContent = res.data;
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
			} catch(err) {
				console.log(err);
			}
		}

		getDetail();
	}, [productId]);
	

	//수정 버튼 이벤트
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
                        <DefaultButton
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
                            <ImageForm
                                imageName={firstThumbnail}
                            />
                        </div>
                        <div className="detail-thumbnail">
                            <h3>썸네일</h3>
                            {thumbnail.map((image, index) => {
                                return (
                                    <ImageForm
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
                                    <ImageForm
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