import React from 'react';
import {Link} from "react-router-dom";

/*
    * 사용자 마이페이지 side Nav
    * 주문 조회
    * 찜 목록
    * 문의 내역
    *   상품 문의
    *   문의 사항 ( 회원 문의 )
    * 리뷰 목록
    * 회원 정보 수정
*/
import '../../css/sidenav.css';

function MyPageSideNav(props) {
    const { qnaStat } = props;

    if(qnaStat){
        return (
            <div className="side-nav">
                <ul className="side-nav-category-ul">
                    <li><Link to={'/my-page/order'}>주문 내역</Link></li>
                    <li><Link to={'/my-page/like'}>관심 상품</Link></li>
                    <li>
                        <Link to={'/my-page/qna/product'}>문의 내역</Link>
                        <ul className="qna-nav-ul">
                            <li>
                                <Link to={'/my-page/qna/product'}>상품 문의</Link>
                            </li>
                            <li>
                                <Link to={'/my-page/qna/member'}>문의 사항</Link>
                            </li>
                        </ul>
                    </li>
                    <li><Link to={'/my-page/review'}>리뷰 내역</Link></li>
                    <li><Link to={'/my-page/info'}>정보 수정</Link></li>
                </ul>
            </div>
        )
    }else {
        return (
            <div className="side-nav">
                <ul className="side-nav-ul">
                    <li><Link to={'/my-page/order'}>주문 내역</Link></li>
                    <li><Link to={'/my-page/like'}>관심 상품</Link></li>
                    <li><Link to={'/my-page/qna/product'}>문의 내역</Link></li>
                    <li><Link to={'/my-page/review'}>리뷰 내역</Link></li>
                    <li><Link to={'/my-page/info'}>정보 수정</Link></li>
                </ul>
            </div>
        )
    }
}

export default MyPageSideNav;