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
import '../../css/sidenav.css';

function MyPageSideNav() {

    return (
        <div className="side-nav">
            <ul className="side-nav-ul">
                <li><Link to={'/my-page/order'}>주문 내역</Link></li>
                <li><Link to={'/my-page/like'}>관심 상품</Link></li>
                <li><Link to={'/my-page/qna'}>문의 내역</Link></li>
                <li><Link to={'/my-page/review'}>리뷰 내역</Link></li>
                <li><Link to={'/my-page/info'}>정보 수정</Link></li>
            </ul>
        </div>
    )
}

export default MyPageSideNav;