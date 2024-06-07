import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate, useSearchParams} from "react-router-dom";
import {useSelector} from "react-redux";
import {axiosInstance} from "../../../modules/customAxios";
import {productDetailPagingObject} from "../../../modules/pagingModule";

import OrderListDetail from "../../ui/OrderListDetail";

import '../../css/mypage.css';

function NonMemberOrderList() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') === null ? 1 : params.get('page');
    const term = params.get('term') === null ? 3 : params.get('term');
    const location = useLocation();
    const state = location.state;
    const [orderData, setOrderData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const userType = 'none';

    const navigate = useNavigate();

    useEffect(() => {
        if(loginStatus)
            navigate('/my-page/order');
        else
            getOrderData();
    }, [term, page]);

    const getOrderData = async () => {
        await axiosInstance.get(`main/order/${term}/${page}`, {
            headers: {
                'Content-Type' : 'application/json',
            },
            params : {
                recipient: state.recipient,
                phone: state.phone,
            }
        })
            .then(res => {
                const pagingObject = productDetailPagingObject(page, res.data.content.totalPages);
                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: page,
                });

                setOrderData(res.data.content);
            })
            .catch(err => {
                console.error('non member order axios error : ', err);
            })
    }

    return (
        <div className="non-member-order">
            <OrderListDetail
                orderData={orderData}
                pagingData={pagingData}
                term={term}
                userType={userType}
            />
        </div>
    )
}

export default NonMemberOrderList;