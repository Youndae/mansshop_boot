import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { getMemberList, postPoint } from '../../services/adminMemberService';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';

import AdminSideNav from '../../components/AdminSideNav';
import DefaultButton from '../../../../common/components/DefaultButton';
import AdminOrderModal from '../../components/modal/AdminOrderModal';
import Pagination from '../../../../common/components/Pagination';

/*
	회원 목록.
	아이디, 가입일만 테이블 구조로 출력.
	클릭시 상세페이지 이동.

	검색 타입은 사용자 아이디, 사용자 이름, 닉네임
*/
function AdminMemberList() {
	const [params] = useSearchParams();
	const { page, keyword, searchType = 'userId' } = Object.fromEntries(params);

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

    const navigate = useNavigate();

	useEffect(() => {
		const getList = async () => {
			try {
				const res = await getMemberList(page, keyword, searchType);

				setData(res.data.content);
                const pagingObject = mainProductPagingObject(page, res.data.totalPages);

                setPagingData({
                    startPage: pagingObject.startPage,
                    endPage: pagingObject.endPage,
                    prev: pagingObject.prev,
                    next: pagingObject.next,
                    activeNo: pagingObject.activeNo,
                });
			} catch(err) {
				console.log(err);
			}
		}

        window.scrollTo(0, 0);
		setKeywordSelectValue(searchType);
		getList();
	}, [page, keyword, searchType]);

	//리스트 Element 클릭 이벤트
    //사용자 정보 Modal 오픈
    const handleOnClick = (userId) => {
        const arr = [...data];
        const userData = arr.find(function (item) {
            return item.userId === userId;
        });

        setModalData(userData);
        setModalIsOpen(true);
    }

	// 페이지 버튼 클릭 이벤트
    const handlePageBtn = (type) => {
        handlePageChange({
            typeOrNumber: type,
            pagingData,
            navigate,
			keyword: keyword,
			searchType: searchType,
        })
    }

	//검색 이벤트
	const handleSearchOnClick = () => {
		const queryString = buildQueryString({
								keyword: keywordInput,
								type: keywordSelectValue,
							})

		navigate(`/admin/member${queryString}`);
	}

	// 검색 input 입력 이벤트
	const handleKeywordOnChange = (e) => {
		setKeywordInput(e.target.value);
	}

	//사용자 정보 Modal Close 이벤트
    const closeModal = (e) => {
        if(modalIsOpen && modalRef.current && !modalRef.current.contains(e.target)){
            setModalIsOpen(false);

            document.body.style.cssText = '';
        }
    }

    //포인트 지급 input 이벤트
    const handlePointOnChange = (e) => {
        setPointValue(e.target.value);
    }

	//포인트 지급 submit 이벤트
	const handlePostPoint = async() => {
		try {
			const userId = modalData.userId;
			const res = await postPoint(userId, pointValue);

			if(res.data.message === RESPONSE_MESSAGE.OK)
				alert('포인트 지급 완료');
		} catch(err) {
			console.log(err);
		}
	}

	//Modal 내 주문정보 버튼 이벤트
    const handleMemberOrder = () => {
        const uid = modalData.userId;
        modalClose();
        //회원 주문 정보 검색 페이지.
        navigate(`/admin/order/all?page=1&keyword=${uid}&type=userId`);
    }

    //Modal 내 상품 문의 목록 버튼 이벤트
    const handleMemberProductQnA = () => {
        const uid = modalData.nickname;
        modalClose();
        //회원 상품 문의 목록 검색
        navigate(`/admin/qna/product?type=all&keyword=${uid}&page=1`);
    }

    //Modal 내 문의 목록 버튼 이벤트
    const handleMemberQnA = () => {
        const uid = modalData.nickname;
        modalClose();
        //회원 문의 내역 검색
        navigate(`/admin/qna/member?type=all&keyword=${uid}&page=1`)
    }

    //Modal Close 이벤트
    const modalClose = () => {
        setModalIsOpen(false);
        document.body.style.cssText = '';
    }

    //검색 타입 select box 이벤트
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
                            <tr>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>닉네임</th>
                                <th>가입일</th>
                            </tr>
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
                                                <DefaultButton
                                                    btnText={'지급'}
                                                    className={'member-point-btn'}
                                                    onClick={handlePostPoint}
                                                />
                                            </div>
                                        </div>
                                        <div className="admin-member-btn">
                                            <DefaultButton
                                                btnText={'주문 정보'}
                                                className={'admin-member-order'}
                                                onClick={handleMemberOrder}
                                            />
                                            <DefaultButton
                                                btnText={'상품 문의 내역'}
                                                className={'admin-member-order'}
                                                onClick={handleMemberProductQnA}
                                            />
                                            <DefaultButton
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
                        <Pagination
                            pagingData={pagingData}
                            handlePageBtn={handlePageBtn}
                            className={'like-paging'}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default AdminMemberList;