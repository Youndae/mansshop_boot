import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminProductSales() {
    /*
        상품별 매출.

        옵션별이 아닌 상품별로 볼 수 있도록.
        분류, 상품명, 상품 총 매출, 판매량

        테이블 오른쪽 상단에서 분류 선택할 수 있도록 처리.
        조회 정렬은 분류 아이디 순서로.

        테이블 하단에는 상품명 검색과 페이징.

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

export default AdminProductSales;