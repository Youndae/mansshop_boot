import React, {useEffect, useState} from 'react';
import {numberComma} from "../../modules/numberCommaModule";
import {axiosInstance} from "../../modules/customAxios";

function OrderListDetail(props) {
    const { orderData, pagingData, term, userType } = props;


    if(orderData === null) {
        return (
            <h2>주문 상품이 없습니다.</h2>
        )
    }else {
        return (
            <>
                <div className="mypage-content">
                    <div className="mypage-order-header">
                        <h1>주문내역</h1>
                        <div className="mypage-order-term-select">
                            <select name="mypage-order-select" value={term}>
                                <option value="3">3개월</option>
                                <option value="6">6개월</option>
                                <option value="12">12개월</option>
                                <option value="all">전체</option>
                            </select>
                        </div>
                    </div>
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
                                            return(
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
                                            <span className='order-data-totalprice'>총 주문 금액 : {numberComma(data.orderTotalPrice)}</span>
                                        </div>
                                    </div>
                                )
                            })}
                        </div>
                    </div>
                </div>
            </>
        )
    }
}


function OrderDetail(props) {
    const { data, orderStat, userType } = props;
    const [imgSrc, setImgSrc] = useState('');

    useEffect(() => {
        getImageDisplay(data.thumbnail);
    }, []);

    const getImageDisplay = async (imageName) => {
        await axiosInstance.get(`main/display/${imageName}`, {
            responseType: 'blob',
        })
            .then(res => {
                const url = window
                    .URL
                    .createObjectURL(
                        new Blob([res.data], {type: res.headers['content-type']})
                    );

                setImgSrc(url);
            })
            .catch(err => {
                console.error('orderDetail display axios error : ', err);
            });
    }


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


    return (
        <div className="mypage-order-data-detail">
            <div className="mypage-order-data-header">
                <span>{data.productName}</span>
            </div>
            <div className="mypage-order-data-content">
                <img src={imgSrc} alt={''}/>
                <div className="order-data-info">
                    <span className="order-data-product-option">{optionText}</span>
                    <span className="order-data-product-count">주문 수량 : {data.detailCount}</span>
                    <span className="order-data-product-price">금액 : {numberComma(data.detailPrice)} 원</span>
                </div>
                <div className="order-data-info-btn">
                    <ReviewBtn
                        reviewStat={data.reviewStatus}
                        detailId={data.detailId}
                        orderId={data.orderId}
                        orderStat={orderStat}
                        userType={userType}
                    />
                </div>
            </div>
        </div>
    )
}

function ReviewBtn(props) {
    const { reviewStat, detailId, orderId, orderStat, userType } = props;

    let reviewBtnValue = `${orderId}/${detailId}`;

    if(orderStat !== 2 || userType === 'none')
        return null;


    if(reviewStat === 0) {
        return (
            <button type={'button'} value={reviewBtnValue}>리뷰작성</button>
        )
    }else {
        return (
            <button type={'button'} disabled={true}>리뷰작성완료</button>
        )
    }
}

function OrderStatus(props) {
    const { orderStat } = props;

    let orderText = '';
    if(orderStat === 0)
        orderText = '상품 준비중';
    else if(orderStat === 1)
        orderText = '배송중';
    else
        orderText = '배송완료';

    const orderStatusText = `배송현황 : ${orderText}`;


    return (
        <span>{orderStatusText}</span>
    )
}

export default OrderListDetail;