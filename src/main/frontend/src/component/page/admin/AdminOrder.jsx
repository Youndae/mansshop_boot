import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminOrder() {

    /*
        미처리 주문 목록.

        서버에서 해당 날짜 16시 이전 주문건에 대한 데이터 중 미처리 항목만 가져와 출력한다.
        Recipient와 createdAt만 테이블 구조로 출력하며 클릭시 modal 창을 출력한다.

        modal에서는 주문 상세정보와 주문자 정보를 보여주며
        하단에 주문 확인 버튼을 통해 배송 준비 단계로 넘어갈 수 있다.
     */

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'order'}
            />
        </div>
    )
}

export default AdminOrder;