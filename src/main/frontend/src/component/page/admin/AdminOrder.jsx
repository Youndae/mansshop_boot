import React, {useEffect, useRef, useState} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";

import {numberComma} from "../../../modules/numberCommaModule";
import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {productDetailPagingObject} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";

import dayjs from "dayjs";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import DefaultBtn from "../../ui/DefaultBtn";
import AdminOrderListForm from "./AdminOrderListForm";
import AdminOrderModalDetail from "./modal/AdminOrderModalDetail";

/*
        미처리 주문 목록.

        서버에서 해당 날짜 16시 이전 주문건에 대한 데이터 중 미처리 항목만 가져와 출력한다.
        Recipient와 createdAt만 테이블 구조로 출력하며 클릭시 modal 창을 출력한다.

        modal에서는 주문 상세정보와 주문자 정보를 보여주며
        하단에 주문 확인 버튼을 통해 배송 준비 단계로 넘어갈 수 있다.
     */
function AdminOrder() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
    const keyword = params.get('keyword');
    const searchType = params.get('type') == null ? 'recipient' : params.get('type');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        totalElements: 0,
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
    const [keywordSelectValue, setKeywordSelectValue] = useState('');
    const [modalIsOpen, setModalIsOpen] = useState(false);

    const modalRef = useRef(null);

    const dispatch = useDispatch();

    useEffect(() => {
        setKeywordSelectValue(searchType);
        getOrderList();
    }, [page, keyword]);

    const getOrderList = async () => {
        let url = `admin/order/new?page=${page}`;
        if(keyword !== null)
            url += `&keyword=${keyword}&searchType=${searchType}`;

        await axiosInstance.get(url)
            .then(res => {
                setData(res.data.content);

                const pagingObject = productDetailPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    totalElements: res.data.totalElements,
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleOnClick = (idx) => {
        setModalOrderData(data[idx]);
        setModalIsOpen(true);
    }

    const handleSelectOnChange = (e) => {
        const value = e.target.value;
        setKeywordSelectValue(value);
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    const closeModal = (e) => {
        if(modalIsOpen && modalRef.current && !modalRef.current.contains(e.target)){
            setModalIsOpen(false);

            document.body.style.cssText = '';
        }
    }

    const handlePreparation = async (e) => {
        const orderId = e.target.value;

        await axiosInstance.patch(`admin/order/${orderId}`)
            .then(res => {
                if(checkResponseMessageOk(res)){
                    setModalIsOpen(false);

                    document.body.style.cssText = '';

                    getOrderList();
                }
            })
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'order'}
            />
            <AdminOrderListForm
                header={`미처리 주문 목록 (${numberComma(pagingData.totalElements)} 건)`}
                data={data}
                handleOnClick={handleOnClick}
                modalIsOpen={modalIsOpen}
                closeModal={closeModal}
                modalRef={modalRef}
                modalOrderData={modalOrderData}
                searchType={searchType}
                handlePreparation={handlePreparation}
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
                        <div className="admin-order-detail-check-btn">
                            <DefaultBtn
                                btnText={'상품 준비'}
                                onClick={handlePreparation}
                                className={'order-preparation-btn'}
                                value={modalOrderData.orderId}
                            />
                        </div>
                    </>
                }
            />
        </div>
    )
}

export default AdminOrder;