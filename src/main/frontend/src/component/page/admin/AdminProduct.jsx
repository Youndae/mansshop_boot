import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

import "../../css/admin.css";

function AdminProduct() {

    /*
        상품 리스트를 출력하고
        상품 또는 옵션을 추가하고
        상품을 검색할 수도 있어야 하고
        카테고리 별로 상품을 볼 수도 있어야 한다.

        출력 정보로는 분류, 상품명, 재고, 옵션 수, 가격

        paging기능이 필요하다.

        디자인으로는

        테이블 상단에 상품 추가, 옵션 추가 버튼, 카테고리 select box를 배치하고
        테이블 하단에 검색과 페이징을 추가한다.
     */





    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'product'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>상품 목록</h1>
                </div>
                <div className="admin-content-content">

                </div>
            </div>
        </div>
    )
}

export default AdminProduct;