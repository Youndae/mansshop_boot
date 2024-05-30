import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";

import dayjs from "dayjs";

import { defaultAxios } from "../../../module/customAxios";
import {productDetailPagingObject} from "../../../module/pagingModule";
import ProductDetailThumbnail from "../../ui/ProductDetailThumbnail";
import ProductDetailInfoImage from "../../ui/ProductDetailInfoImage";

import '../../css/productDetail.css';

/*
    바로구매, 장바구니, 관심상품 버튼 handling
    상품정보, 리뷰, QnA, 주문 정보 버튼 handling
    로그인 여부에 따른 QnA 작성 버튼 handling
    리뷰, QnA paging
    리뷰 QnA 처럼 답변 사이즈 조절.
 */
function ProductDetail() {
    const { productId } = useParams();
    const [productData, setProductData] = useState({
        productId: '',
        productName: '',
        productPrice: 0,
        productLikeStat: false,
    });
    const [productOption, setProductOption] = useState([]);
    const [thumbnail, setThumbnail] = useState([]);
    const [infoImage, setInfoImage] = useState([]);
    const [productReview, setProductReview] = useState([]);
    const [reviewPagingObject, setReviewPagingObject] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: 1,
        totalElements: 0,
    });
    const [productQnA, setProductQnA] = useState([]);
    const [productQnAPagingObject, setProductQnAPagingObject] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: 1,
        totalElements: 0,
    });
    const [selectOption, setSelectOption] = useState([]);
    const [totalPrice, setTotalPrice] = useState(0);



    useEffect(() => {
        getDetailData();
    }, [productId]);

    const getDetailData = async () => {

        await defaultAxios.get(`product/${productId}`)
            .then(res => {
                console.log('productDetail axios ::  res : ', res);
                const productContent = res.data;
                const productReview = res.data.productReviewList;
                const productQnA = res.data.productQnAList;

                setProductData({
                    productId: productContent.productId,
                    productName: productContent.productName,
                    productPrice: productContent.productPrice,
                    productLikeStat: productContent.likeStat,
                });

                let thumbnailArr = [];
                thumbnailArr.push(
                    productContent.productImageName
                );

                for(let i = 0; i < productContent.productThumbnailList.length; i++){
                    thumbnailArr.push(
                        productContent.productThumbnailList[i]
                    );
                }

                setThumbnail(thumbnailArr);
                setInfoImage(productContent.productInfoImageList);
                setProductOption(productContent.productOptionList);

                setProductReview(productReview.content);
                const reviewPagingObject = productDetailPagingObject(productReview.number + 1, productReview.totalPages);
                setReviewPagingObject({
                    startPage: reviewPagingObject.startPage,
                    endPage: reviewPagingObject.endPage,
                    prev: reviewPagingObject.prev,
                    next: reviewPagingObject.next,
                    activeNo: productReview.number + 1,
                    totalElements: productReview.totalElements,
                });

                setProductQnA(productQnA.content);
                const qnaPagingObject = productDetailPagingObject(productQnA.number + 1, productQnA.totalPages);
                setProductQnAPagingObject({
                    startPage: qnaPagingObject.startPage,
                    endPage: qnaPagingObject.endPage,
                    prev: qnaPagingObject.prev,
                    next: qnaPagingObject.next,
                    activeNo: productQnA.number + 1,
                    totalElements: productQnA.totalElements,
                });

            })
            .catch(err => {
                console.log('productDetail axios :: err : ', err);
            })
    }

    const handleSelectBoxOnChange = (e) => {
        const elementValue = e.target.value;
        const valueArr = elementValue.split('/');
        const optionId = valueArr[0];
        let size = null;
        let color = null;

        for(let i = 1; i < valueArr.length; i++){
            if(valueArr[i].startsWith('s'))
                size = valueArr[i].substring(2);
            else if(valueArr[i].startsWith('c'))
                color = valueArr[i].substring(2);
        }

        const arr = [...selectOption];
        arr.push({
            optionId: optionId,
            count: 1,
            price: productData.productPrice,
            size: size,
            color: color,
        })

        setSelectOption(arr);
        setTotalPrice(totalPrice + productData.productPrice);
    }

    const handleCountUp = (e) => {
        const idx = e.target.name;

        countUpDown(idx, 1);
        setTotalPrice(totalPrice + productData.productPrice);
    }

    const handleCountDown = (e) => {
        const idx = e.target.name;
        const count = selectOption[idx].count;

        if(count !== 1) {
            countUpDown(idx, -1);
            setTotalPrice(totalPrice - productData.productPrice);
        }
    }

    const countUpDown = (idx, count) => {

        selectOption[idx] = {
            optionId: selectOption[idx].optionId,
            count: selectOption[idx].count + count,
            price: productData.productPrice * (selectOption[idx].count + count),
            size: selectOption[idx].size,
            color: selectOption[idx].color,
        }

        setSelectOption([...selectOption]);
    }

    const handleOptionRemove = (e) => {
        const idx = e.target.name;

        let arr = [...selectOption];
        const optionPrice = arr[idx].price;
        arr.splice(idx, 1);

        setSelectOption(arr);
        setTotalPrice(totalPrice - optionPrice);
    }

    const handleBuyBtn = () => {

    }

    const handleCartBtn = () => {

    }

    const handleLikeBtn = () => {
        const pid = productData.productId;
    }

    return (
        <div className="product-detail-content">
            <div className="product-detail-header">
                <ProductDetailThumbnail
                    imageName={thumbnail}
                />

                <div className="product-detail-option">
                    <div className="product-detail-option-detail">
                        <div className="product-default-info">
                            <div className="product-name mgt-4">
                                <label>상품명</label>
                                <span className="name">{productData.productName}</span>
                            </div>
                            <div className="product-price mgt-4">
                                <label>가격</label>
                                <span className="price">{productData.productPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')} 원</span>
                            </div>
                            <ProductDetailSelect
                                productOption={productOption}
                                onChange={handleSelectBoxOnChange}
                            />
                        </div>
                        <div className="product-info temp-order mgt-4">
                            <table className="temp-order-table">
                                <tbody className="temp-order-table-body">
                                    {selectOption.map((option, index) => {
                                        return (
                                            <TempOrderTableBody
                                                key={index}
                                                idx={index}
                                                selectOption={option}
                                                handleCountUp={handleCountUp}
                                                handleCountDown={handleCountDown}
                                                handleOptionRemove={handleOptionRemove}
                                            />
                                        )
                                    })}
                                </tbody>
                            </table>
                        </div>
                        <button onClick={handleBuyBtn}>바로구매</button>
                        <button onClick={handleCartBtn}>장바구니</button>
                        <button onClick={handleLikeBtn}>관심상품</button>
                        <TotalPrice
                            totalPrice={totalPrice}
                        />
                    </div>
                </div>
            </div>

            <div className="product-detail-content">
                <div className="product-detail-content-btn">
                    <label htmlFor="detail-btn">상품정보</label>
                    <button id={'detail-btn'} name={'detail'}></button>
                    <label htmlFor="review-btn">리뷰({reviewPagingObject.totalElements})</label>
                    <button id={'review-btn'} name={'review'}></button>
                    <label htmlFor="qna-btn">QnA({productQnAPagingObject.totalElements})</label>
                    <button id={'qna-btn'} name={'qna'}></button>
                    <label htmlFor="order-info-btn">주문정보</label>
                    <button id={'order-info-btn'} name={'order-info'}></button>
                </div>
                <div className="product-detail-info">
                    <h2>상품 정보</h2>
                    <ProductDetailInfoImage imageInfo={infoImage} />
                </div>
                <div className="product-detail-review">
                    <div className="product-detail-review-header">
                        <h2>상품 리뷰</h2>
                    </div>
                    <div className="product-detail-review-content">
                        <ul>
                            <Review
                                data={productReview}
                            />
                        </ul>
                    </div>
                    <div className="product-detail-review-paging">

                    </div>
                </div>
                <div className="product-detail-qna">
                    <div className="product-detail-qna-header">
                        <h2>상품 문의</h2>
                        <div className="qna-input">
                            <textarea name="qna-text"></textarea>
                        </div>
                        <button>문의하기</button>
                    </div>
                    <div className="product-detail-qna-content">
                        <ul>
                            <QnA
                                data={productQnA}
                            />
                        </ul>
                    </div>
                    <div className="product-detail-qna-paging">

                    </div>
                </div>

                <div className="product-detail-order-info">
                    <div className="product-detail-order-info-header">
                        <h2>배송 정보</h2>
                    </div>
                    <div className="product-detail-order-info-content">
                        <table className="delivery-info" border={1}>
                            <tbody>
                                <tr>
                                    <th>배송 방법</th>
                                    <td>순차 배송</td>
                                </tr>
                                <tr>
                                    <th>묶음배송 여부</th>
                                    <td>가능</td>
                                </tr>
                                <tr>
                                    <th>배송비</th>
                                    <td>
                                        <ul>
                                            <li>3,500원 / 10만원 이상 구매시 무료배송</li>
                                            <li>제주, 도서산간 지역 배송은 5,000원 / 10만원 이상 구매시 무료배송</li>
                                        </ul>
                                    </td>
                                </tr>
                                <tr>
                                    <th>배송기간</th>
                                    <td>결제일 기준 평균 2 ~ 4일 소요</td>
                                </tr>
                                <tr>
                                    <th>무통장입금 계좌번호</th>
                                    <td>
                                        <ul>
                                            <li>농협 000-000000-000</li>
                                            <li>기업은행 0000-00000-000</li>
                                        </ul>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div className="product-detail-order-info-header">
                        <h2>교환 / 반품 안내</h2>
                    </div>
                    <div className="product-detail-order-info-content">
                        <table className="delivery-info" border={1}>
                            <tbody>
                                <tr>
                                    <th>교환/반품 비용</th>
                                    <td>
                                        5,000원
                                        <ul>
                                            <li>단, 고객의 변심의 경우에만 발생</li>
                                        </ul>
                                    </td>
                                </tr>
                                <tr>
                                    <th>교환 반품 신청 기준일</th>
                                    <td>
                                        <ul>
                                            <li>
                                                상품 수령 후 7일 이내 마이페이지>문의사항을 통해 접수.
                                            </li>
                                            <li>
                                                타 택배사 이용 시 2,500원을 동봉해 선불로 보내주셔야 합니다.
                                            </li>
                                        </ul>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>

                    <div className="product-detail-order-info-header">
                        <h2>교환 / 반품 제한사항</h2>
                    </div>
                    <div className="product-detail-order-info-content">
                        <ul>
                            <li>향수등의 냄새가 배거나 착용흔적이 보이는 제품, 오랜기간 방치로 인한 제품의 가치가 하락한 제품등의 경우는 교환/환불이 어려울 수 있습니다.</li>
                            <li>착용흔적, 훼손이 있을 경우에는 교환/환불 처리가 어려울 수 있기 때문에 개별적으로 연락을 드립니다.</li>
                            <li>불량품, 오배송의 경우는 mansShop에서 배송비를 부담합니다.</li>
                            <li>반드시 우체국 택배를 통해 보내주셔야 합니다. 그외 택배사 이용시 선불로 택배를 보내주셔야 합니다. 착불 시 추가적 배송비는 고객님이 부담하게 됩니다.</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    )
}

function ProductDetailSelect(props) {
    const { productOption, onChange } = props;

    if(productOption === [])
        return null;
    else {
        return (
            <div className="product-detail-select mgt-4">
                <label>옵션</label>
                <select id={'product-detail-option-select-box'} defaultValue={'default'} onChange={onChange}>
                    <option value={'default'} disabled={true}>옵션을 선택해주세요</option>
                    {productOption.map((option, index) => {
                        return(
                            <ProductDetailSelectOption
                                key={index}
                                productOption={option}
                            />
                        )
                    })}
                </select>
            </div>
        )
    }
}

function ProductDetailSelectOption(props) {
    const { productOption } = props;
    let optionText = '';
    let valueText = `${productOption.optionId}`;
    const size = productOption.size;
    const sizeText = `사이즈 : ${size}`;
    const sizeValueText = `/s:${productOption.size}`;
    const color = productOption.color;
    const colorText = `컬러 : ${color}`;
    const colorValueText = `/c:${productOption.color}`;

    if(size !== null){
        if(color !== null) {
            optionText = `${sizeText}, ${colorText}`;
            valueText += `${sizeValueText}${colorValueText}`;
        }else {
            optionText = `${sizeText}`;
            valueText += `${sizeValueText}`;
        }
    }else if(color !== null) {
        optionText = `${colorText}`;
        valueText = `${colorValueText}`;
    }

    if(productOption.stock == 0) {
        return (
            <>
                <option value={valueText}  disabled={true}>(품절) {optionText} </option>
            </>
        )
    }else {
        return (
            <>
                <option value={valueText} > {optionText} </option>
            </>
        )
    }
}

function TempOrderTableBody(props) {
    const { idx, selectOption, handleCountUp, handleCountDown, handleOptionRemove } = props;

    if(selectOption === [])
        return null;
    else {
        let optionText = '';
        const size = selectOption.size;
        const sizeText = `사이즈 : ${size}`;
        const color = selectOption.color;
        const colorText = `컬러 : ${color}`;

        if(size !== null){
            if(color !== null) {
                optionText = `${sizeText}, ${colorText}`;
            }else {
                optionText = `${sizeText}`;
            }
        }else if(color !== null) {
            optionText = `${colorText}`;
        }

        const price = selectOption.price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') + ' 원';


        return (
            <>
                <tr className="product-temp-cart">
                    <td>{optionText}</td>
                    <td className="product-temp-cart-input">
                        <input type={'text'} value={selectOption.count} readOnly={true}/>
                        <div className="product-temp-count">
                            <div className="count-up-down">
                                <img src={`${process.env.PUBLIC_URL}/image/up.jpg`} name={idx} onClick={handleCountUp}/>
                                <img src={`${process.env.PUBLIC_URL}/image/down.jpg`} onClick={handleCountDown} name={idx}/>
                            </div>
                            <div className="count-remove">
                                <img src={`${process.env.PUBLIC_URL}/image/del.jpg`} onClick={handleOptionRemove} name={idx}/>
                            </div>
                        </div>
                    </td>
                    <td className={'product-price'}>
                        <span>{price}</span>
                    </td>
                </tr>
            </>
        )
    }
}

function TotalPrice(props) {
    const { totalPrice } = props;

    if(totalPrice === 0)
        return null;
    else{
        return (
            <div className="total-price">
                <p>
                    총 금액 : <span>{totalPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')} 원</span>
                </p>
            </div>
        )
    }
}

function Review(props) {
    const { data } = props;

    if(data === []){
        return (
            <li>
                <span>아직 작성된 리뷰가 없습니다.</span>
            </li>
        );
    }else {
        return (
            <>
                {data.map((review, index) => {
                    if(review.reviewStep === 0){
                        return(
                            <li className={'review-content writer'}>
                                <div className="review-content-header">
                                    <strong className="reviewer">{review.writer}</strong>
                                    <small className={'pull-right text-muted'}>{dayjs(review.createdAt).format('YYYY-MM-DD')}</small>
                                </div>
                                <div className="review-content-content">
                                    <p>{review.reviewContent}</p>
                                </div>
                            </li>
                        )
                    }else {
                        return (
                            <li className={'review-content reply'}>
                                <div className="review-content-header">
                                    <strong className="reviewer">{review.writer}</strong>
                                    <small className={'pull-right text-muted'}>{dayjs(review.createdAt).format('YYYY-MM-DD')}</small>
                                </div>
                                <div className="review-content-content">
                                    <p>{review.reviewContent}</p>
                                </div>
                            </li>
                        )
                    }
                })}
            </>
        )
    }

}

function QnA(props) {
    const { data } = props;

    if(data === null) {
        return null;
    }else {
        return (
            <>
                {data.map((qna, index) => {
                    let statElem = '';
                    if(qna.productQnAStat === 1)
                        statElem = <small className={'pull-right answer'}>답변완료</small>;
                    if(qna.productQnAStep === 0){
                        return (
                            <li className="qna-content">
                                <div className="qna-content-header">
                                    <strong className="qna-writer">{qna.writer}</strong>
                                    <small className={'pull-right text-muted'}>{dayjs(qna.createdAt).format('YYYY-MM-DD')}</small>
                                    {statElem}
                                </div>
                                <div className="qna-content-content">
                                    <p>{qna.qnaContent}</p>
                                </div>
                            </li>
                        )
                    }else {
                        return (
                            <div className="qna-content-reply">
                                <li className="qna-content">
                                    <div>
                                        <div className="qna-content-header">
                                            <strong className="qna-writer">{qna.writer}</strong>
                                            <small className={'pull-right text-muted'}>{dayjs(qna.createdAt).format('YYYY-MM-DD')}</small>
                                        </div>
                                        <div className="qna-content-content">
                                            <p>{qna.qnaContent}</p>
                                        </div>
                                    </div>
                                </li>
                            </div>
                        )
                    }
                })}
            </>
        )
    }
}

export default ProductDetail;