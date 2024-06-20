import React, {useEffect, useState} from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {getClickNumber, getNextNumber, getPrevNumber, productDetailPagingObject} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import Paging from "../../ui/Paging";

import dayjs from "dayjs";

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
    const [detailData, setDetailData] = useState({
        recipient : '',
        uid: '',
        orderDate: '',
        phone: '',
        addr: '',
    });
    const [detailProductData, setDetailProductData] = useState([]);
    const [keywordSelectValue, setKeywordSelectValue] = useState('recipient');

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        getOrderList();
    }, [page, keyword]);

    const getOrderList = async () => {

        await axiosInstance.get(`admin/order/all?page=${page}&keyword=${keyword}&searchType=${searchType}`)
            .then(res => {
                //recipient, userId, phone, createdAt 구조.
                //orderId 필요.
                console.log('all orderList res : ', res);
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
        // const value = e.target.attribute;

        console.log('handleOnClick : ', idx);
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
        if(keyword == null)
            navigate(`/admin/order/all?page=${pageNum}`);
        else
            navigate(`/admin/order/all?keyword=${keyword}&page=${pageNum}`);
    }

    const handleSearchOnClick = async () => {
        navigate(`/admin/order/all?keyword=${keywordInput}`);
    }

    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        setKeywordSelectValue(value);
    }

    const handleKeywordOnChange = (e) => {
        setKeywordInput(e.target.value);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'order'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>전체 주문 목록</h1>
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table">
                        <thead>
                            <th>받는사람</th>
                            <th>사용자 아이디</th>
                            <th>연락처</th>
                            <th>주문일</th>
                        </thead>
                        <tbody>
                            {data.map((bodyData, index) => {
                                return (
                                    <tr key={index} value={index} onClick={() => handleOnClick(index)} className="admin-order-body-tr">
                                        <td>{bodyData.recipient}</td>
                                        <td>{bodyData.userId}</td>
                                        <td>{bodyData.phone}</td>
                                        <td>{dayjs(bodyData.createdAt).format('YYYY-MM-DD HH:mm')}</td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                    <div className="admin-search">
                        <select className="admin-order-search" value={searchType} onChange={handleSelectOnChange}>
                            <option value={'recipient'}>받는 사람</option>
                            <option value={'userId'}>사용자 아이디</option>
                        </select>
                        <input type={'text'} onChange={handleKeywordOnChange} value={keywordInput}/>
                        <img alt={''} src={"https://as1.ftcdn.net/v2/jpg/03/25/73/68/1000_F_325736897_lyouuiCkWI59SZAPGPLZ5OWQjw2Gw4qY.jpg"} onClick={handleSearchOnClick}/>
                        <Paging
                            pagingData={pagingData}
                            onClickNumber={handlePageBtn}
                            onClickPrev={handlePagePrev}
                            onClickNext={handlePageNext}
                            className={'like-paging'}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AdminAllOrder;