import React from 'react';
import {Link} from "react-router-dom";

/*
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
function AdminSideNav(props) {
    const { categoryStatus } = props;

    return (
        <div className="side-nav">
            <ul className="side-nav-ul">
                <li>
                    <Link to={'/admin/product'}>상품 관리</Link>
                    <SideMenuCategory categoryStatus={categoryStatus} status={'product'}/>
                </li>
                <li>
                    <Link to={'/admin/order'}>주문 관리</Link>
                    <SideMenuCategory categoryStatus={categoryStatus} status={'order'}/>
                </li>
                <li>
                    <Link to={'/admin/qna/product'}>문의 관리</Link>
                    <SideMenuCategory categoryStatus={categoryStatus} status={'qna'}/>
                </li>
                <li>
                    <Link to={'/admin/review'}>리뷰 관리</Link>
                    <SideMenuCategory categoryStatus={categoryStatus} status={'review'}/>
                </li>
                <li><Link to={'/admin/member'}>회원 관리</Link></li>
                <li>
                    <Link to={'/admin/sales/period'}>매출 관리</Link>
                    <SideMenuCategory categoryStatus={categoryStatus} status={'sales'}/>
                </li>
            </ul>
        </div>
    )
}

function SideMenuCategory(props) {
    const { categoryStatus, status } = props;

    let categoryData = [];

    if(categoryStatus === status){
        if(categoryStatus === 'product'){
            categoryData = [
                {
                    link: '/admin/product',
                    text: '상품 목록',
                },
                {
                    link: '/admin/product/stock',
                    text: '재고 관리',
                },
                {
                    link: '/admin/product/discount',
                    text: '할인 설정',
                }
            ]
        }else if(categoryStatus === 'order') {
            categoryData = [
                {
                    link: '/admin/order',
                    text: '미처리 목록',
                },
                {
                    link: '/admin/order/all',
                    text: '전체 목록',
                }
            ]
        }else if(categoryStatus === 'qna'){
            categoryData = [
                {
                    link: '/admin/qna/product',
                    text: '상품 문의',
                },
                {
                    link: '/admin/qna/member',
                    text: '회원 문의',
                },
                {
                    link: '/admin/qna/classification',
                    text: '문의 카테고리 설정',
                }
            ];
        }else if(categoryStatus === 'review') {
            categoryData = [
                {
                    link: '/admin/review',
                    text: '미답변 목록',
                },
                {
                    link: '/admin/review/all',
                    text: '전체 목록',
                }
            ]
        }else if(categoryStatus === 'sales') {
            categoryData = [
                {
                    link: '/admin/sales/period',
                    text: '기간별 매출',
                },
                {
                    link: '/admin/sales/product',
                    text: '상품별 매출',
                }
            ]
        }
    }



    return (
        <ul className="admin-side-nav-category-ul">
            {categoryData.map((data, index) => {
                return (
                    <li key={index}><Link to={data.link}>{data.text}</Link></li>
                )
            })}
        </ul>
    )
}





export default AdminSideNav;