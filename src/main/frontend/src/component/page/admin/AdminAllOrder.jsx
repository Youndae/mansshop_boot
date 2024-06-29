import React, {useEffect, useRef, useState} from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber,
    pageSubmit,
    productDetailPagingObject, searchTypePageSubmit, searchTypeSubmit
} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import Paging from "../../ui/Paging";

import dayjs from "dayjs";
import AdminOrderModal from "./modal/AdminOrderModal";
import {numberComma} from "../../../modules/numberCommaModule";
import DefaultBtn from "../../ui/DefaultBtn";
import AdminOrderListForm from "./AdminOrderListForm";
import AdminOrderModalDetail from "./modal/AdminOrderModalDetail";

/*
        모든 주문 목록.
        최근 순으로 정렬.
        테이블 구조로 처리.
        recipient, userId, phone, createdAt 구조.

        하단에는 주문자, 아이디를 통한 검색과 페이징이 존재.

        클릭시 OrderModal을 통해 주문 정보를 확인할 수 있지만 버튼은 활성화 시키지 않는다.

        추후 매니저 권한으로 접근할 수 있는 컴포넌트로 처리하기 위함.
     */
function AdminAllOrder() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page') == null ? 1 : params.get('page');
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

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getOrderList();
    }, [page, keyword, searchType]);

    const getOrderList = async () => {

        let url = `admin/order/all?page=${page}`;
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
                    activeNo: page,
                });

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
            .catch(err => {
                console.error('all Order axios get Error : ', err);
            })
    }

    const handleOnClick = (idx) => {

        setModalOrderData(data[idx]);
        setModalIsOpen(true);
    }

    /*const handlePageBtn = (e) => {
        handlePagingSubmit(getClickNumber(e));
    }

    const handlePagePrev = () => {
        handlePagingSubmit(getPrevNumber(pagingData));
    }

    const handlePageNext = () => {
        handlePagingSubmit(getNextNumber(pagingData));
    }

    const handlePagingSubmit = (pageNum) => {
        if(keyword == null)
            pageSubmit(pageNum, navigate);
        else
            searchTypePageSubmit(keywordSelectValue, keyword, pageNum, navigate);
    }

    const handleSearchOnClick = () => {
        searchTypeSubmit(keywordSelectValue, keywordInput, navigate);
    }*/

    /*const handleSearchOnClick = async () => {
        navigate(`?keyword=${keywordInput}&type=${keywordSelectValue}`);
    }*/

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