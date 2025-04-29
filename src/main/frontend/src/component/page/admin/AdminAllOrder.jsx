import React, {useEffect, useRef, useState} from 'react';
import {useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import { mainProductPagingObject } from "../../../modules/pagingModule";
import {createPageAndSearchTypeKeyword} from "../../../modules/requestUrlModule";

import dayjs from "dayjs";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import AdminOrderListForm from "./AdminOrderListForm";
import AdminOrderModalDetail from "./modal/AdminOrderModalDetail";

/*
        모든 주문 목록.
        최근 순으로 정렬.
        테이블 구조로 처리.
        recipient, userId, phone, createdAt 구조.

        하단에는 주문자, 아이디를 통한 검색과 페이징이 존재.
 */
function AdminAllOrder() {
    const [params] = useSearchParams();
    const page = params.get('page');
    const keyword = params.get('keyword');
    const searchType = params.get('type');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');
    const [modalOrderData, setModalOrderData] = useState({
        orderId: 0,
        recipient: '',
        userId: '',
        phone: '',
        date: '',
        address: '',
        orderStatus: '',
        detailList: [],
    })
    const [keywordSelectValue, setKeywordSelectValue] = useState('recipient');
    const [modalIsOpen, setModalIsOpen] = useState(false);

    const modalRef = useRef(null);

    useEffect(() => {
        getOrderList();
    }, [page, keyword, searchType]);

    //전체 주문 목록 조회.
    const getOrderList = async () => {
        let url = `admin/order/all${createPageAndSearchTypeKeyword(page, keyword, searchType)}`;

        await axiosInstance.get(url)
            .then(res => {
                setData(res.data.content);

                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });
            })
    }

    //주문 상세 modal창 오픈 이벤트
    const handleOnClick = (idx) => {
        setModalOrderData(data[idx]);
        setModalIsOpen(true);
    }

    //검색 타입 select box 이벤트
    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        setKeywordSelectValue(value);
    }

    //검색 input 입력 이벤트
    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    //주문 상세 modal창 close 이벤트
    const closeModal = (e) => {

        if(modalIsOpen && modalRef.current && !modalRef.current.contains(e.target)){
            setModalIsOpen(false);

            document.body.style.cssText = '';
        }
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'order'}
            />
            <AdminOrderListForm
                header={'전체 주문 목록'}
                data={data}
                handleOnClick={handleOnClick}
                modalIsOpen={modalIsOpen}
                closeModal={closeModal}
                modalRef={modalRef}
                modalOrderData={modalOrderData}
                searchType={searchType}
                keywordSelectValue={keywordSelectValue}
                handleSelectOnChange={handleSelectOnChange}
                handleKeywordOnChange={handleKeywordOnChange}
                keywordInput={keywordInput}
                pagingData={pagingData}
                keyword={keyword}
                render={() =>
                    <>
                        <div className="admin-order-info">
                            <div className="form-group">
                                <label>받는 사람 : </label>
                                <span>{modalOrderData.recipient}</span>
                            </div>
                            <div className="form-group">
                                <label>사용자 아이디 : </label>
                                <span>{modalOrderData.userId}</span>
                            </div>
                            <div className="form-group">
                                <label>연락처 : </label>
                                <span>{modalOrderData.phone}</span>
                            </div>
                            <div className="form-group">
                                <label>주문일 : </label>
                                <span>{dayjs(modalOrderData.createdAt).format('YYYY-MM-DD dd요일 HH:mm')}</span>
                            </div>
                            <div className="form-group">
                                <label>배송지 : </label>
                                <span>{modalOrderData.address}</span>
                            </div>
                            <div className="form-group">
                                <label>배송 상태 : </label>
                                <span>{modalOrderData.orderStatus}</span>
                            </div>
                        </div>
                        <div className="admin-order-detail">
                            {modalOrderData.detailList.map((data, index) => {
                                return (
                                    <AdminOrderModalDetail
                                        key={index}
                                        data={data}
                                        orderStatus={modalOrderData.orderStatus}
                                    />
                                )
                            })}
                        </div>
                    </>
                }
            />
        </div>
    )
}

export default AdminAllOrder;