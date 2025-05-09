import React, {useEffect, useRef, useState} from 'react';
import {useLocation, useNavigate, useParams} from "react-router-dom";
import {useSelector} from "react-redux";

import {axiosDefault, axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {getClickNumber,
        getNextNumber,
        getPrevNumber,
        productDetailPagingObject
} from "../../../modules/pagingModule";
import {handleLocationPathToLogin} from "../../../modules/loginModule";
import {numberComma} from "../../../modules/numberCommaModule";

import ProductDetailThumbnail from "../../ui/ProductDetailThumbnail";
import Paging from "../../ui/Paging";
import Image from "../../ui/Image";
import DefaultBtn from "../../ui/DefaultBtn";

import '../../css/productDetail.css';

/*
    상품 상세 페이지
    현재는 백엔드에서 한번의 요청으로 모든 데이터를 전달해주는 형태.
 */
function ProductDetail() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const { productId } = useParams();
    const [productData, setProductData] = useState({
        productId: '',
        productName: '',
        productPrice: 0,
        productLikeStat: false,
        discount: 0,
        discountPrice: 0,
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
    const [qnaInputValue, setQnAInputValue] = useState('');

    const productInfoElem = useRef(null);
    const productReviewElem = useRef(null);
    const productQnAElem = useRef(null);
    const productOrderInfoElem = useRef(null);
    const { pathname } = useLocation();

    const navigate = useNavigate();

    useEffect(() => {
        window.scrollTo(0, 0);
        getDetailData();
    }, [productId]);

    //상품 상세 페이지 데이터 조회
    const getDetailData = async () => {

        await axiosInstance.get(`product/${productId}`)
            .then(res => {
                const productContent = res.data;
                const productReview = res.data.productReviewList;
                const productQnA = res.data.productQnAList;

                setProductData({
                    productId: productContent.productId,
                    productName: productContent.productName,
                    productPrice: productContent.productPrice,
                    productLikeStat: productContent.likeStat,
                    discount: productContent.discount,
                    discountPrice: productContent.discountPrice,
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
    }

    //옵션 select box 이벤트
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
            price: productData.discountPrice,
            size: size,
            color: color,
        })

        setSelectOption(arr);
        setTotalPrice(totalPrice + productData.discountPrice);
    }

    //옵션 선택 이후 상품 수량 증가 버튼 이벤트
    const handleCountUp = (e) => {
        const idx = e.target.name;

        countUpDown(idx, 1);
        setTotalPrice(totalPrice + productData.discountPrice);
    }

    //옵션 선택 이후 상품 수량 감소 버튼 이벤트
    const handleCountDown = (e) => {
        const idx = e.target.name;
        const count = selectOption[idx].count;

        if(count !== 1) {
            countUpDown(idx, -1);
            setTotalPrice(totalPrice - productData.discountPrice);
        }
    }

    //상품 수량 증감 처리
    const countUpDown = (idx, count) => {

        selectOption[idx] = {
            optionId: selectOption[idx].optionId,
            count: selectOption[idx].count + count,
            price: productData.discountPrice * (selectOption[idx].count + count),
            size: selectOption[idx].size,
            color: selectOption[idx].color,
        }

        setSelectOption([...selectOption]);
    }

    //선택 옵션 제거 이벤트
    const handleOptionRemove = (e) => {
        const idx = e.target.name;

        let arr = [...selectOption];
        const optionPrice = arr[idx].price;
        arr.splice(idx, 1);

        setSelectOption(arr);
        setTotalPrice(totalPrice - optionPrice);
    }

    //바로 구매 버튼 이벤트
    const handleBuyBtn = () => {
        if(selectOption.length === 0){
            alert('상품 옵션을 선택해주세요');
        }else {
            let orderProductArr = [];

            for(let i = 0; i < selectOption.length; i++) {
                orderProductArr.push({
                    optionId: selectOption[i].optionId,
                    count: selectOption[i].count,
                })
            }

            getOrderData(orderProductArr);
        }
    }

    //바로 구매 요청 시 상품 데이터 재 확인 후 state에 담아 주문 페이지 호출
    const getOrderData = async (selectData) => {
        await axiosInstance.post(`order/product`, selectData)
            .then(res => {
                const dataList = res.data.orderData;
                const totalPrice = res.data.totalPrice;

                navigate('/productOrder', {state : {
                        orderProduct : dataList,
                        orderType: 'direct',
                        totalPrice: totalPrice,
                    }});
            })
    }

    //장바구니 담기 버튼 이벤트
    const handleCartBtn = async () => {

        if(selectOption.length === 0){
            alert('상품 옵션을 선택해주세요');
        }else {
            let addList = [];

            for (let i = 0; i < selectOption.length; i++) {
                addList.push({
                    optionId: selectOption[i].optionId,
                    count: selectOption[i].count,
                })
            }

            await axiosDefault.post(`cart/`, addList)
                .then(res => {
                    if (checkResponseMessageOk(res))
                        alert('장바구니에 상품을 추가했습니다.');
                })
                .catch(() => {
                    alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
                })
        }
    }

    //관심상품 등록 이벤트
    const handleLikeBtn = async () => {
        if(!loginStatus){
            if(window.confirm('로그인 사용자만 관심상품 등록이 가능합니다.\n로그인 하시겠습니까?'))
                handleLocationPathToLogin(pathname, navigate);
        }else {
            const likeStatus = productData.productLikeStat;

            await axiosDefault.post(`product/like`, JSON.stringify(productId), {
                headers: {'Content-Type' : 'application/json'},
            })
                .then(res => {

                    setProductData({
                        ...productData,
                        productLikeStat: !likeStatus,
                    });
                })
                .catch(() => {
                    alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
                })
        }
    }

    //관심상품 해제 이벤트
    const handleDeLikeBtn = async () => {
        const pid = productData.productId;
        const likeStatus = productData.productLikeStat;

        await axiosDefault.delete(`product/like/${pid}`)
            .then(res => {
                if(checkResponseMessageOk(res)) {
                    setProductData({
                        ...productData,
                        productLikeStat: !likeStatus,
                    });
                }
            })
            .catch(() => {
                alert('오류가 발생했습니다.\n문제가 계속된다면 관리자에게 문의해주세요.');
            })
    }

    //UI내 상품 정보, 리뷰, 문의 버튼 클릭 시 스크롤 이동 이벤트
    const handleDetailBtn = (e) => {
        const name = e.target.name;

        if(name === 'detail')
            productInfoElem.current.scrollIntoView({ behavior: 'smooth', block: 'start'});
        else if(name === 'review')
            productReviewElem.current.scrollIntoView({ behavior: 'smooth', block: 'center'});
        else if(name === 'qna')
            productQnAElem.current.scrollIntoView({ behavior: 'smooth', block: 'center'});
        else
            productOrderInfoElem.current.scrollIntoView({ behavior: 'smooth', block: 'center'});
    }

    //리뷰 페이지네이션 버튼 이벤트
    const handleReviewPagingClickNumber = (e) => {
        handleReviewPaging(getClickNumber(e));
    }

    //리뷰 페이지네이션 이전 버튼 이벤트
    const handleReviewPagingPrev = () => {
        handleReviewPaging(getPrevNumber(reviewPagingObject));
    }

    //리뷰 페이지네이션 다음 버튼 이벤트
    const handleReviewPagingNext = () => {
        handleReviewPaging(getNextNumber(reviewPagingObject));
    }

    //리뷰 페이지네이션 처리
    const handleReviewPaging = async (clickNo) => {

        await axiosInstance.get(`product/${productId}/review?page=${clickNo}`)
            .then(res => {
                setProductReview(res.data.content);

                const pageNumber = res.data.number + 1;
                const reviewPagingObject = productDetailPagingObject(pageNumber, res.data.totalPages);
                setReviewPagingObject({
                    startPage: reviewPagingObject.startPage,
                    endPage: reviewPagingObject.endPage,
                    prev: reviewPagingObject.prev,
                    next: reviewPagingObject.next,
                    activeNo: pageNumber,
                    totalElements: res.data.totalElements,
                });
            })
    }

    //상품 문의 페이지네이션 버튼 이벤트
    const handleQnAPagingClickNumber = (e) => {
        handleQnAPaging(getClickNumber(e));
    }

    //상품 문의 페이지네이션 이전 버튼 이벤트
    const handleQnAPagingPrev = () => {
        handleQnAPaging(getPrevNumber(productQnAPagingObject));
    }

    //상품 문의 페이지네이션 다음 버튼 이벤트
    const handleQnAPagingNext = () => {
        handleQnAPaging(getNextNumber(productQnAPagingObject));
    }

    //상품 문의 페이지네이션 처리
    const handleQnAPaging = async (clickNo) => {

        await axiosInstance.get(`product/${productId}/qna?page=${clickNo}`)
            .then(res => {
                setProductQnA(res.data.content);

                const pageNumber = res.data.number + 1;
                const qnaPagingObject = productDetailPagingObject(pageNumber, res.data.totalPages);
                setProductQnAPagingObject({
                    startPage: qnaPagingObject.startPage,
                    endPage: qnaPagingObject.endPage,
                    prev: qnaPagingObject.prev,
                    next: qnaPagingObject.next,
                    activeNo: pageNumber,
                    totalElements: res.data.totalElements,
                })
            })
    }

    //상품 문의 작성 textarea 입력 이벤트
    const handleQnAOnChange = (e) => {
        setQnAInputValue(e.target.value);
    }

    //상품 문의 작성 버튼 이벤트
    const handleQnASubmit = async () => {
        if(!loginStatus){
            if(window.confirm('상품문의는 로그인시에만 가능합니다.\n로그인하시겠습니까?'))
                navigate('/login');
        }else {
            const productId = productData.productId;

            await axiosInstance.post(`product/qna`, {
                productId: productId,
                content: qnaInputValue,
            }, {
                headers: {'Content-Type' : 'application/json'}
            })
                .then(res => {
                    if(checkResponseMessageOk(res)){
                        setQnAInputValue('');
                        handleQnAPaging(1);
                    }
                })
        }
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
                                <ProductPrice productData={productData}/>
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
                        <DefaultBtn onClick={handleBuyBtn} btnText={'바로구매'}/>
                        <DefaultBtn onClick={handleCartBtn} btnText={'장바구니'}/>
                        <ProductLikeBtn
                            likeStatus={productData.productLikeStat}
                            handleLikeBtn={handleLikeBtn}
                            handleDeLikeBtn={handleDeLikeBtn}
                        />
                        <TotalPrice
                            totalPrice={totalPrice}
                        />
                    </div>
                </div>
            </div>

            <div className="product-detail-content">
                <div className="product-detail-content-btn">
                    <label htmlFor="detail-btn">상품정보</label>
                    <button id={'detail-btn'} name={'detail'} onClick={handleDetailBtn}></button>
                    <label htmlFor="review-btn">리뷰({reviewPagingObject.totalElements})</label>
                    <button id={'review-btn'} name={'review'}  onClick={handleDetailBtn}></button>
                    <label htmlFor="qna-btn">QnA({productQnAPagingObject.totalElements})</label>
                    <button id={'qna-btn'} name={'qna'} onClick={handleDetailBtn}></button>
                    <label htmlFor="order-info-btn">주문정보</label>
                    <button id={'order-info-btn'} name={'order-info'} onClick={handleDetailBtn}></button>
                </div>
                <div className="product-detail-info" ref={productInfoElem}>
                    <h2>상품 정보</h2>
                    {infoImage.map((image, index) => {
                        return (
                            <div key={index} className={'info-image-div'}>
                                <Image className={'info-image'} imageName={image}/>
                            </div>
                        )
                    })}
                </div>
                <div className="product-detail-review" ref={productReviewElem}>
                    <div className="product-detail-review-header">
                        <h2>상품 리뷰</h2>
                    </div>
                    <div className="product-detail-review-content">
                        <ul>
                            {productReview.map((review, index) => {
                                return (
                                    <Review
                                        data={review}
                                        key={index}
                                    />
                                )
                            })}
                        </ul>
                    </div>
                    <div className="product-detail-review-paging">
                        <Paging
                            pagingData={reviewPagingObject}
                            onClickNumber={handleReviewPagingClickNumber}
                            onClickPrev={handleReviewPagingPrev}
                            onClickNext={handleReviewPagingNext}
                            clasName={''}
                        />
                    </div>
                </div>
                <div className="product-detail-qna" ref={productQnAElem}>
                    <div className="product-detail-qna-header">
                        <h2>상품 문의</h2>
                        <div className="qna-input">
                            <textarea name="qna-text" value={qnaInputValue} onChange={handleQnAOnChange}>{qnaInputValue}</textarea>
                        </div>
                        <DefaultBtn onClick={handleQnASubmit} btnText={'문의하기'}/>
                    </div>
                    <div className="product-detail-qna-content">
                        <ul>
                            {productQnA.map((qna, index) => {
                                return (
                                    <QnA
                                        data={qna}
                                        key={index}
                                    />
                                )
                            })}
                        </ul>
                    </div>
                    <div className="product-detail-qna-paging">
                        <Paging
                            pagingData={productQnAPagingObject}
                            onClickNumber={handleQnAPagingClickNumber}
                            onClickPrev={handleQnAPagingPrev}
                            onClickNext={handleQnAPagingNext}
                            clasName={''}
                        />
                    </div>
                </div>

                <div className="product-detail-order-info" ref={productOrderInfoElem}>
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

//상품 가격 폼
function ProductPrice(props) {
    const { productData } = props;

    if(productData.discount === 0){
        return (
            <span className="price">{numberComma(productData.productPrice)}원</span>
        )
    }else {
        return (
            <>
                <span className="original-price">{numberComma(productData.productPrice)}</span>
                <span className="discount-value">{productData.discount}%</span>
                <span className="discount-Price">{numberComma(productData.discountPrice)}원</span>
            </>
        )
    }
}

//상품 옵션 select box 폼
function ProductDetailSelect(props) {
    const { productOption, onChange } = props;

    if(productOption.length === 0)
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

//상품 옵션 select box option 태그 폼
function ProductDetailSelectOption(props) {
    const { productOption } = props;
    let optionText = '단일 옵션';
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
        valueText += `${colorValueText}`;
    }

    if(productOption.stock === 0) {
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

//옵션 선택 시 출력 폼
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
                        <span>{`${numberComma(selectOption.price)} 원`}</span>
                    </td>
                </tr>
            </>
        )
    }
}

//옵션 선택에 따른 전체 금액 출력 폼
function TotalPrice(props) {
    const { totalPrice } = props;

    if(totalPrice === 0)
        return null;
    else{
        return (
            <div className="total-price">
                <p>
                    총 금액 : <span>{`${numberComma(totalPrice)} 원`}</span>
                </p>
            </div>
        )
    }
}

//상품 리뷰 폼
function Review(props) {
    const { data } = props;

    const defaultLi = <li className={'review-content-default'}>
                            <div key={data.writer} className="review-content-header">
                                <strong className="reviewer">{data.reviewWriter}</strong>
                                <small className={'pull-right text-muted'}>{data.reviewCreatedAt}</small>
                            </div>
                            <div className="review-content-content">
                                <p>{data.reviewContent}</p>
                            </div>
                        </li>;
    let answerLi = null;

    if(data.answerContent !== null){
        answerLi = <div className="review-content-reply">
                        <li className={'review-content'}>
                            <div className="review-content-header">
                                <strong className="reviewer">관리자</strong>
                                <small className={'pull-right text-muted'}>{data.answerCreatedAt}</small>
                            </div>
                            <div className="review-content-content">
                                <p>{data.answerContent}</p>
                            </div>
                        </li>
                    </div>;
    }

    return (
        <>
            {defaultLi}
            {answerLi}
        </>
    )
}

//상품 문의 폼
function QnA(props) {
    const { data } = props;

    let statElem = '';
    const replyList = data.replyList;

    if(data.productQnAStat)
        statElem = <small className={'pull-right answer'}>답변완료</small>;

    if(replyList === []){
        return (
            <li className="qna-content-default">
                <div className="qna-content-header">
                    <strong className="qna-writer">{data.writer}</strong>
                    <small className={'pull-right text-muted'}>{data.createdAt}</small>
                    {statElem}
                </div>
                <div className="qna-content-content">
                    <p>{data.qnaContent}</p>
                </div>
            </li>
        )
    }else {
        return(
            <>
                <li className="qna-content-default">
                    <div className="qna-content-header">
                        <strong className="qna-writer">{data.writer}</strong>
                        <small className={'pull-right text-muted'}>{data.createdAt}</small>
                        {statElem}
                    </div>
                    <div className="qna-content-content">
                        <p>{data.qnaContent}</p>
                    </div>
                </li>
                {replyList.map((reply, index) => {
                    return (
                        <QnAReply
                            data={reply}
                            key={index}
                        />
                    )
                })}
            </>
        )
    }
}

//상품 문의 답변 폼
function QnAReply(props) {
    const { data } = props;

    if(data === [])
        return null;
    else {
        return (
            <div className="qna-content-reply">
                <li className="qna-content">
                    <div>
                        <div className="qna-content-header">
                            <strong className="qna-writer">{data.writer}</strong>
                            <small className={'pull-right text-muted'}>{data.createdAt}</small>
                        </div>
                        <div className="qna-content-content">
                            <p>{data.content}</p>
                        </div>
                    </div>
                </li>
            </div>
        )
    }
}

//관심상품 버튼 폼
function ProductLikeBtn(props) {
    const { likeStatus, handleLikeBtn, handleDeLikeBtn } = props;

    if(likeStatus){
        return (
            <DefaultBtn onClick={handleDeLikeBtn} btnText={'관심상품 해제'}/>
        )
    }else {
        return (
            <DefaultBtn onClick={handleLikeBtn} btnText={'관심상품 등록'}/>
        )
    }
}

export default ProductDetail;