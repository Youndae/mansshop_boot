import React from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";

function AdminMember() {
    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'member'}
            />
        </div>
    )
}

export default AdminMember;