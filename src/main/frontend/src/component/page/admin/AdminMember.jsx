import React, {useState, useEffect, useRef} from 'react';
import {useDispatch, useSelector} from "react-redux";
import {useNavigate, useSearchParams} from "react-router-dom";

import {axiosInstance, checkResponseMessageOk} from "../../../modules/customAxios";
import {
    getClickNumber,
    getNextNumber,
    getPrevNumber, mainProductPagingObject,
    pageSubmit,
    searchTypePageSubmit, searchTypeSubmit
} from "../../../modules/pagingModule";
import {setMemberObject} from "../../../modules/loginModule";
import {createPageAndSearchTypeKeyword} from "../../../modules/requestUrlModule";

import AdminSideNav from "../../ui/nav/AdminSideNav";
import Paging from "../../ui/Paging";
import AdminOrderModal from "./modal/AdminOrderModal";
import DefaultBtn from "../../ui/DefaultBtn";

/*
        회원 목록.
        아이디, 가입일만 테이블 구조로 출력.
        클릭시 상세페이지 이동.

        data

        content : [
            {
                userId
                nickname
                createdAt
                point
                phone
                addr
                email
                birth
            }
        ]

        검색은 아이디로만.
     */
function AdminMember() {
    const loginStatus = useSelector((state) => state.member.loginStatus);
    const [params] = useSearchParams();
    const page = params.get('page');
    const keyword = params.get('keyword');
    const searchType = params.get('type') == null ? 'userId' : params.get('type');

    const [data, setData] = useState([]);
    const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });
    const [keywordInput, setKeywordInput] = useState('');
    const [modalData, setModalData] = useState({
        userId: '',
        userName: '',
        nickname: '',
        phone: '',
        email: '',
        birth: '',
        point: 0,
        createdAt: '',
    })
    const [pointValue, setPointValue] = useState(0);
    const [keywordSelectValue, setKeywordSelectValue] = useState('');
    const [modalIsOpen, setModalIsOpen] = useState(false);

    const modalRef = useRef(null);

    const dispatch = useDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        setKeywordSelectValue(searchType);
        getMemberList();
    }, [page, keyword, searchType]);

    const getMemberList = async () => {
        let url = `admin/member${createPageAndSearchTypeKeyword(page, keyword, searchType)}`;

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

                const member = setMemberObject(res, loginStatus);

                if(member !== undefined)
                    dispatch(member);
            })
    }

    const handleOnClick = (userId) => {
        const arr = [...data];
        const userData = arr.find(function (item) {
            return item.userId === userId;
        });

        setModalData(userData);
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
            pageSubmit(pageNum, navigate);
        else
            searchTypePageSubmit(searchType, keyword, pageNum, navigate);
    }

    const handleSearchOnClick = () => {
        searchTypeSubmit(keywordSelectValue, keywordInput, navigate);
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

    const handlePointOnChange = (e) => {
        setPointValue(e.target.value);
    }

    const handlePostPoint = async () => {
        const uid = modalData.userId;

        await axiosInstance.patch(`admin/member/point`, {
            userId: uid,
            point: pointValue
        })
            .then(res => {
                if(checkResponseMessageOk(res))
                    alert('포인트 지급 완료!');
            })
    }

    const handleMemberOrder = () => {
        const uid = modalData.userId;
        modalClose();
        //회원 주문 정보 검색 페이지.
        navigate(`/admin/order/all?page=1&keyword=${uid}&type=userId`);
    }

    const handleMemberProductQnA = () => {
        const uid = modalData.nickname;
        modalClose();
        //회원 상품 문의 목록 검색
        navigate(`/admin/qna/product?type=all&keyword=${uid}&page=1`);
    }

    const handleMemberQnA = () => {
        const uid = modalData.nickname;
        modalClose();
        //회원 문의 내역 검색
        navigate(`/admin/qna/member?type=all&keyword=${uid}&page=1`)
    }

    const modalClose = () => {
        setModalIsOpen(false);
        document.body.style.cssText = '';
    }

    const handleSelectOnChange = (e) => {
        const value = e.target.value;

        setKeywordSelectValue(value);
    }

    return (
        <div className="mypage">
            <AdminSideNav
                categoryStatus={'member'}
            />
            <div className="admin-content">
                <div className="admin-content-header">
                    <h1>회원 목록</h1>
                </div>
                <div className="admin-content-content">
                    <table className="admin-content-table">
                        <thead>
                            <th>아이디</th>
                            <th>이름</th>
                            <th>닉네임</th>
                            <th>가입일</th>
                        </thead>
                        <tbody>
                            {data.map((value, index) => {
                                return (
                                    <tr key={index} onClick={() => handleOnClick(value.userId)} className={'admin-order-body-tr'}>
                                        <td>{value.userId.length > 15 ? `${value.userId.slice(0, 15)}...` : value.userId}</td>
                                        <td>{value.userName}</td>
                                        <td>{value.nickname}</td>
                                        <td>{value.createdAt}</td>
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
                                    <div className="admin-member-info">
                                        <div className="form-group">
                                            <label>아이디 : </label>
                                            <span>{modalData.userId}</span>
                                        </div>
                                        <div className="form-group">
                                            <label>닉네임 : </label>
                                            <span>{modalData.nickname}</span>
                                        </div>
                                        <div className="form-group">
                                            <label>연락처 : </label>
                                            <span>{modalData.phone}</span>
                                        </div>
                                        <div className="form-group">
                                            <label>이메일 : </label>
                                            <span>{modalData.email}</span>
                                        </div>
                                        <div className="form-group">
                                            <label>생년월일 : </label>
                                            <span>{modalData.birth}</span>
                                        </div>
                                        <div className="form-group">
                                            <label>보유 포인트 : </label>
                                            <span>{modalData.point}</span>
                                        </div>
                                        <div className="form-group">
                                            <label>가입일 : </label>
                                            <span>{modalData.createdAt}</span>
                                        </div>
                                    </div>
                                    <div className="admin-member-proc">
                                        <div className="admin-member-point-input">
                                            <div className="form-group">
                                                <label>포인트 지급 : </label>
                                                <input type={'number'} className="member-point-input" value={pointValue} onChange={handlePointOnChange}/>
                                                <DefaultBtn
                                                    btnText={'지급'}
                                                    className={'member-point-btn'}
                                                    onClick={handlePostPoint}
                                                />
                                            </div>
                                        </div>
                                        <div className="admin-member-btn">
                                            <DefaultBtn
                                                btnText={'주문 정보'}
                                                className={'admin-member-order'}
                                                onClick={handleMemberOrder}
                                            />
                                            <DefaultBtn
                                                btnText={'상품 문의 내역'}
                                                className={'admin-member-order'}
                                                onClick={handleMemberProductQnA}
                                            />
                                            <DefaultBtn
                                                btnText={'문의 내역'}
                                                className={'admin-member-order'}
                                                onClick={handleMemberQnA}
                                            />
                                        </div>
                                    </div>
                                </>
                            }
                        />
                    )}
                    <div className="admin-search">
                        <select className="admin-order-search" value={keywordSelectValue} onChange={handleSelectOnChange}>
                            <option value={'userId'}>아이디</option>
                            <option value={'userName'}>이름</option>
                            <option value={'nickname'}>닉네임</option>
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

export default AdminMember;