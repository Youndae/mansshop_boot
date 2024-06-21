import React, {useEffect, useRef, useState} from 'react';
import AdminSideNav from "../../ui/nav/AdminSideNav";
import {useDispatch, useSelector} from "react-redux";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import {axiosInstance} from "../../../modules/customAxios";
import {getClickNumber, getNextNumber, getPrevNumber, productDetailPagingObject} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import Paging from "../../ui/Paging";

import dayjs from "dayjs";
import AdminOrderModal from "./modal/AdminOrderModal";
import {numberComma} from "../../../modules/numberCommaModule";

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
        detailList: [],
    })
    const [keywordSelectValue, setKeywordSelectValue] = useState('recipient');
    const [modalIsOpen, setModalIsOpen] = useState(false);
    const modalRef = useRef(null);

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
        console.log('handleOnClick : ', idx);

        console.log('handleOnClick data : ', data[idx]);

        setModalOrderData(data[idx]);
        setModalIsOpen(true);
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
                            <th>처리 상태</th>
                        </thead>
                        <tbody>
                            {data.map((bodyData, index) => {
                                return (
                                    <tr key={index} value={index} onClick={() => handleOnClick(index)} className="admin-order-body-tr">
                                        <td>{bodyData.recipient}</td>
                                        <td>{bodyData.userId}</td>
                                        <td>{bodyData.phone}</td>
                                        <td>{dayjs(bodyData.createdAt).format('YYYY-MM-DD HH:mm')}</td>
                                        <td>{bodyData.orderStatus}</td>
                                    </tr>
                                )
                            })}
                        </tbody>
                    </table>
                    {modalIsOpen && (
                        <AdminOrderModal
                            closeModal={closeModal}
                            modalRef={modalRef}
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
                                                <ModalOrderDetail
                                                    key={index}
                                                    data={data}
                                                />
                                            )
                                        })}
                                    </div>
                                </>
                        }
                        />
                    )}
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

function ModalOrderDetail(props) {
    const { data } = props;
    let reviewText = '미작성'
    if(data.reviewStatus === 1)
        reviewText = '작성'
    else if(data.reviewStatus === 2)
        reviewText = '삭제'

    return (
        <div className="admin-order-detail-form">
            <h3>{data.productName}</h3>
            <div className="admin-order-detail-info">
                <div className="form-group">
                    <label>분류 : </label>
                    <span>{data.classification}</span>
                </div>
                <div className="detail-info-form-group">
                    <div className="form-group">
                        <label>사이즈 : </label>
                        <span>{data.size}</span>
                    </div>
                    <div className="form-group">
                        <label>컬러 : </label>
                        <span>{data.color}</span>
                    </div>
                    <div className="form-group">
                        <label>수량 : </label>
                        <span>{data.count}</span>
                    </div>
                    <div className="form-group">
                        <label>금액 : </label>
                        <span>{numberComma(data.price)}</span>
                    </div>
                    <div className="form-group">
                        <label>리뷰 작성 여부 : </label>
                        <span>{reviewText}</span>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AdminAllOrder;