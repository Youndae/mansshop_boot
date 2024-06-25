import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminPeriodSales() {
    /*
        기간별 매출.

        월별 매출을 리스트로 보여준다.
        단위는 연단위.

        오른쪽 상단에서는 select box를 통해 5년까지의 매출을 확인할 수 있도록 처리.
        리스트는 select box를 통한 선택된 해의 월 매출을 보여준다.
        이때 매출이 없는 개월에 대해서는 0을 출력하도록 서버에서 데이터를 파싱해 전달하도록 한다.

        하단에는 해당 년의 총 주문 건수와 총 판매량, 총 매출액을 출력하도록 한다.

        상세 페이지에서는 그 달의 상품 분류별 매출 및 판매량을 출력한다.
        ex
        YYYY년 MM월 매출 정보
            월 매출액 : 000,000원
            월 판매량 : 000,000개
            월 주문량 : 000,000개
            전년 대비 : +- 000,000원

        가장 많이 판매된 상품 5개
            상품명 판매량 매출

        상품 분류 : OUTER 상세보기 버튼
            총 판매량 : 000,000개
            총 매출액 : 000,000원
        ....

        OUTER 상세 내역(Modal)
            상품명 옵션 수량 가격


        일별 매출액 (table)
        날짜 판매량 일매출 (클릭시 Modal을 통한 상세 정보)

        YYYY년 MM월 DD일 매출 상세 내역 (버튼을 통한 당일 상세 주문 내역 페이지로 이동)
            총 판매량
            총 매출액
            총 주문량
            상품 출고량
            상품 분류 : OUTER
                총 판매량 : 000,000개
                총 매출액 : 000,000원
                총 판매 수량 : 000,000개
            ....



        자세한 주문 내역을 보여주기 위해 새로운 컴포넌트 추가?
        해당 컴포넌트에서는 해당 기간의 모든 주문 내역을 출력하도록 처리???
        이때 주문 내역에 출력할 데이터로는 주문내역별로 묶어서 내부에 상품명, 옵션, 수량, 가격, 배송비, 총 결제액을 출력하도록 한다.
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