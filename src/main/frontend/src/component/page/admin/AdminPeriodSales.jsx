import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminPeriodSales() {
    /*
        기간별 매출.

        default로 일별 매출을 보여준다.

        테이블 구조로 보여줌.
        날짜, 매출, 판매량으로 보여준다.

        테이블 오른쪽 상단에서는 selectbox를 통해 일, 월, 연별 선택할 수 있도록 처리.

        상세 컴포넌트는 생성하긴 할건데
        고민중.

        일단 상세에서 보여줘야 하는 정보로는
        해당 기간의 상품 분류별 매출, 판매량, 상품별 매출, 판매량 정도가 아닐까 싶은데??
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

export default AdminPeriodSales;