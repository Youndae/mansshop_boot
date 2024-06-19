import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminMember() {

    /*
        회원 목록.
        아이디, 가입일만 테이블 구조로 출력.
        클릭시 상세페이지 이동.

     */

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'member'}
            />
        </div>
    )
}

export default AdminMember;