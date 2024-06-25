import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminProductSalesDetail() {
    /*
        params로 productId를 받는다.

        해당 상품의 총 매출
        총 판매량
        옵션별 판매량과 매출을 출력.

        당해, 전년 월별 판매량과 매출을 출력.
     */

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'sales'}
            />
            <div className="admin-content">
                <div className="admin-content-header">

                </div>
                <div className="admin-content-content">

                </div>
            </div>
        </div>
    )
}

export default AdminProductSalesDetail;