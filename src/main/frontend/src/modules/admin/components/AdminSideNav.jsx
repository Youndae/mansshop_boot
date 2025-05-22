import React from 'react';
import { Link } from 'react-router-dom';

import { SIDE_MENU_MAP } from '../constants/adminSideNavData';

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
                <li><Link to={'/admin/failedQueue'}>메시지 관리</Link></li>
            </ul>
        </div>
    )
}

function SideMenuCategory(props) {
    const { categoryStatus, status } = props;

    if(categoryStatus !== status)
        return null;

    const categoryData = SIDE_MENU_MAP[status] ?? [];

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