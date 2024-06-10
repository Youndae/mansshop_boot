import React, {useEffect, useState} from 'react';
import {axiosInstance} from "../../../modules/customAxios";
import MyPageSideNav from "../../ui/MyPageSideNav";

import '../../css/mypage.css';
import {useNavigate, useSearchParams} from "react-router-dom";
import {getClickNumber, getNextNumber, getPrevNumber, productDetailPagingObject} from "../../../modules/pagingModule";
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
    const navigate = useNavigate();


    useEffect(() => {
        getOrderData(term);
    }, [term, page]);

    const getOrderData = async (term) => {

        await axiosInstance.get(`my-page/order/${term}/${page}`)
            .then(res => {
                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

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

    const handleSelectOnChange = (e) => {
        const selectTerm = e.target.value;

        navigate(`/my-page/order?term=${selectTerm}`)
    }

    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        navigate(`/my-page/order?term=${term}&page=${pageNum}`);
    }

    return (
        <div className="mypage">
            <MyPageSideNav/>
            <OrderListDetail
                className={'mypage-content'}
                orderData={orderData}
                pagingData={pagingData}
                term={term}
                userType={userType}
                handleSelectOnChange={handleSelectOnChange}
                handlePageBtn={handlePageBtn}
                handlePagePrev={handlePagePrev}
                handlePageNext={handlePageNext}
            />
        </div>
    )
}

export default MyPageOrder;