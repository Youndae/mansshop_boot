import React, {useEffect, useState} from 'react';
import {numberComma} from "../../modules/numberCommaModule";
import {axiosInstance} from "../../modules/customAxios";
import Paging from "./Paging";
import {Link, useNavigate} from "react-router-dom";
import Image from "./Image";

function OrderListDetail(props) {
    const {
        className
        , orderData
        , pagingData
        , term
        , userType
        , handleSelectOnChange
        , handlePageBtn
        , handlePagePrev
        , handlePageNext
    } = props;


    return (
        <>
            <div className={className}>
                <div className="mypage-order-header">
                    <h1>주문내역</h1>
                    <div className="mypage-order-term-select">
                        <select name="mypage-order-select" onChange={handleSelectOnChange} value={term}>
                            <option value="3">3개월</option>
                            <option value="6">6개월</option>
                            <option value="12">12개월</option>
                            <option value="all">전체</option>
                        </select>
                    </div>
                </div>
                <OrderList
                    orderData={orderData}
                    userType={userType}
                />
                <Paging
                    pagingData={pagingData}
                    onClickNumber={handlePageBtn}
                    onClickPrev={handlePagePrev}
                    onClickNext={handlePageNext}
                    className={'order-paging'}
                />
            </div>
        </>
    )
}

function OrderList(props) {
    const { orderData, userType } = props;
    console.log('orderList orderData : ', orderData);
    if(orderData.length === 0) {
        return (
            <div className="non-order-data-header">
                <h2>주문 상품이 없습니다.</h2>
            </div>
        )
    }else {

        return (
            <div className="mypage-order-list-content">
                <div className="mypage-order-list">
                    {orderData.map((data, index) => {
                        return (
                            <div key={index} className="mypage-order-content">
                                <div className="mypage-order-list-content-header">
                                    <span>주문일 : {data.orderDate}</span>
                                    <button type={'button'}>배송조회</button>
                                </div>
                                {data.detail.map((detail, index) => {
                                    return (
                                        <OrderDetail
                                            key={index}
                                            data={detail}
                                            orderStat={data.orderStat}
                                            userType={userType}
                                        />
                                    )
                                })}
                                <div className="order-data-info-content">
                                    <OrderStatus
                                        orderStat={data.orderStat}
                                    />
                                    <span
                                        className='order-data-totalprice'>총 주문 금액 : {numberComma(data.orderTotalPrice)}</span>
                                </div>
                            </div>
                        )
                    })}
                </div>
            </div>
        )
    }
}


function OrderDetail(props) {
    const { data, orderStat, userType } = props;

    let sizeText = '사이즈 : ';
    let colorText = '컬러 : ';

    if(data.size === null && data.color !== null) {
        sizeText = '';
        colorText += `${data.color}`;
    }else {
        if(data.color !== null){
            sizeText += `${data.size}, `;
            colorText += `${data.color}`;
        }else {
            sizeText += `${data.size}`;
            colorText = '';
        }
    }

    const optionText = `${sizeText}${colorText}`;

    console.log('data : ', data);


    return (
        <div className="mypage-order-data-detail">
            <div className="mypage-order-data-header">
                <Link to={`/product/${data.productId}`}>
                    <span>{data.productName}</span>
                </Link>
            </div>
            <div className="mypage-order-data-content">
                <Image
                    imageName={data.thumbnail}
                />
                <div className="order-data-info">
                    <span className="order-data-product-option">{optionText}</span>
                    <span className="order-data-product-count">주문 수량 : {data.detailCount}</span>
                    <span className="order-data-product-price">금액 : {numberComma(data.detailPrice)} 원</span>
                </div>
                <div className="order-data-info-btn">
                    <ReviewBtn
                        reviewStat={data.reviewStatus}
                        orderStat={orderStat}
                        userType={userType}
                        productName={data.productName}
                        productId={data.productId}
                    />
                </div>
            </div>
        </div>
    )
}

function ReviewBtn(props) {
    const { reviewStat, orderStat, userType, productName, productId } = props;
    const navigate = useNavigate();

    if(orderStat !== '배송 완료' || userType === 'none')
        return null;

    const handleReviewBtn = () => {
        navigate('/my-page/review/write', {state : {productId: productId, productName: productName}});
    }

    if(reviewStat === 0) {
        return (
            <button type={'button'} onClick={handleReviewBtn}>리뷰작성</button>
        )
    }else {
        return (
            <button type={'button'} disabled={true}>리뷰작성완료</button>
        )
    }
}

function OrderStatus(props) {
    const { orderStat } = props;

    /*let orderText = '';
    if(orderStat === 0)
        orderText = '상품 준비중';
    else if(orderStat === 1)
        orderText = '배송중';
    else
        orderText = '배송완료';*/

    const orderStatusText = `배송현황 : ${orderStat}`;


    return (
        <span>{orderStatusText}</span>
    )
}

export default OrderListDetail;