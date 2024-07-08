import React from 'react';

import {numberComma} from "../../../../modules/numberCommaModule";

function AdminOrderModalDetail(props) {
    const { data, orderStatus } = props;

    let reviewText = '미작성'
    if(data.reviewStatus)
        reviewText = '작성'

    let reviewForm = '';

    if(orderStatus === '배송 완료') {
        reviewForm = <div className="form-group">
                        <label>리뷰 작성 여부 : </label>
                        <span>{reviewText}</span>
                    </div>;
    }

    return (
        <div className="admin-order-detail-form">
            <h3>{data.productName}</h3>
            <div className="admin-order-detail-info">
                <div className="form-group">
                    <label>분류 : </label>
                    <span>{data.classification}</span>
                </div>
                <div className="detail-info-form-group">
                    <div className="form-group">
                        <label>사이즈 : </label>
                        <span>{data.size}</span>
                    </div>
                    <div className="form-group">
                        <label>컬러 : </label>
                        <span>{data.color}</span>
                    </div>
                    <div className="form-group">
                        <label>수량 : </label>
                        <span>{data.count}</span>
                    </div>
                    <div className="form-group">
                        <label>금액 : </label>
                        <span>{numberComma(data.price)}</span>
                    </div>
                    {reviewForm}
                </div>
            </div>
        </div>
    )
}

export default AdminOrderModalDetail;