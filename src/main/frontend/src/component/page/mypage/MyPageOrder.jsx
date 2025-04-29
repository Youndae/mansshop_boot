import React, {useEffect, useState} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject
} from "../../../modules/pagingModule";
import {createPageParam} from "../../../modules/requestUrlModule";

import MyPageSideNav from "../../ui/nav/MyPageSideNav";
import OrderListDetail from "../../ui/OrderListDetail";

import '../../css/mypage.css';

/*
    주문 목록 조회
    select box를 통한 기간별 조회 가능.
    3, 6, 12 개월, 전체 조회 가능.
 */
function MyPageOrder() {
    const [params] = useSearchParams();
    const page = params.get('page');
    const term = params.get('term') === null ? 3 : params.get('term');

    const [orderData, setOrderData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    const userType = 'user';

    const navigate = useNavigate();

    useEffect(() => {
        getOrderData(term);
    }, [term, page]);

    //주문 목록 조회. select box의 기간을 기준으로 조회
    const getOrderData = async (term) => {

        await axiosInstance.get(`my-page/order/${term}${createPageParam(page)}`)
            .then(res => {
                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });

                setOrderData(res.data.content);
            })
    }

    //기간 select box 이벤트
    const handleSelectOnChange = (e) => {
        const selectTerm = e.target.value;

        navigate(`?term=${selectTerm}`)
    }

    //페이지네이션 버튼 이벤트
    const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    //페이지네이션 이전 버튼 이벤트
    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    //페이지네이션 다음 버튼 이벤트
    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    //페이지네이션 이벤트
    const handlePagingSubmit = (pageNum) => {
        navigate(`?term=${term}&page=${pageNum}`);
    }

    return (
        <div className="mypage">
            <MyPageSideNav
                qnaStat={false}
            />
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