import React from 'react';
import {Link} from "react-router-dom";

/*
    * 사용자 side
    * 주문 조회
    * 찜 목록
    * 문의 내역
    * 리뷰 목록
    * 회원 정보 수정
    *
    *
    * 관리자 side
    * 상품 관리
    *   상품 목록(메인)
    *   상품 재고
    *   상품 할인
    *   카테고리 설정
    * 주문 관리
    *   미처리 목록
    *   전체 목록
    * 문의 관리
    *   상품 문의
    *   회원 문의
    *   카테고리 설정
    * 회원 관리
    * 매출 관리
    *   기간별
    *   상품별
    * */
function AdminSideNav() {

    return (
        <div className="side-nav">
            <ul className="side-nav-ul">
                <div>
                    <div className="nav-header">
                        <li><span>상품 관리</span></li>
                    </div>
                    <div className="nav-content">
                        <li><Link to={'/admin/product'}>상품 목록</Link></li>
                        <li><Link to={'/admin/product/stock'}>상품 재고</Link></li>
                        <li><Link to={'/admin/product/discount'}>상품 할인</Link></li>
                        <li><Link to={'/admin/product/classification'}>카테고리 설정</Link></li>
                    </div>
                </div>
                <div>
                    <div className="nav-header">
                        <li><span>주문 관리</span></li>
                    </div>
                    <div className="nav-content">
                        <li><Link to={'/admin/order/new'}>미처리 목록</Link></li>
                        <li><Link to={'/admin/order/all'}>전체 목록</Link></li>
                    </div>
                </div>
                <div>
                    <div className="nav-header">
                        <li><span>문의 관리</span></li>
                    </div>
                    <div className="nav-content">
                        <li><Link to={'/admin/qna/product'}>상품 문의</Link></li>
                        <li><Link to={'/admin/qna/member'}>회원 문의</Link></li>
                        <li><Link to={'/admin/qna/classification'}>카테고리 설정</Link></li>
                    </div>
                </div>
                <div>
                    <div className="nav-header">
                        <li><Link to={'/admin/member'}>회원 관리</Link></li>
                    </div>
                </div>
                <div>
                    <div className="nav-header">
                        <li><span>매출 관리</span></li>
                    </div>
                    <div className="nav-content">
                        <li><Link to={'/admin/sales/period'}>기간별 매출</Link></li>
                        <li><Link to={'/admin/sales/product'}>상품별 매출</Link></li>
                    </div>
                </div>
            </ul>
        </div>
    )
}

export default AdminSideNav;