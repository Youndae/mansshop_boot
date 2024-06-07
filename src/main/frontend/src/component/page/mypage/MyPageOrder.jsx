import React, {useEffect, useState} from 'react';
import {axiosInstance} from "../../../modules/customAxios";
import MyPageSideNav from "../../ui/MyPageSideNav";

import '../../css/mypage.css';
import {useSearchParams} from "react-router-dom";
import {productDetailPagingObject} from "../../../modules/pagingModule";
import {useDispatch, useSelector} from "react-redux";
import {setMemberObject} from "../../../modules/loginModule";
import OrderListDetail from "../../ui/OrderListDetail";

/*
    페이징 추가해야함.
 */
function MyPageOrder(props) {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') === null ? 1 : params.get('page');
    const term = params.get('term') === null ? 3 : params.get('term');
    const [orderData, setOrderData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    const dispatch = useDispatch();
    const userType = 'user';


    useEffect(() => {
        getOrderData(term);
    }, []);

    const getOrderData = async (term) => {

        await axiosInstance.get(`my-page/order/${term}/${page}`)
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

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('mypage order err : ', err);
            })
    }

    return (
        <div className="mypage">
            <MyPageSideNav/>
            <OrderListDetail
                orderData={orderData}
                pagingData={pagingData}
                term={term}
                userType={userType}
            />
        </div>
    )
}

export default MyPageOrder;