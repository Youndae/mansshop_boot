import React, {useEffect, useRef, useState} from 'react';
import { useSearchParams, useNavigate } from "react-router-dom";

import { getNewOrderList, patchOrderStatus } from '../../services/adminOrderService';
import { numberComma } from '../../../../common/utils/formatNumberComma';
import { RESPONSE_MESSAGE } from '../../../../common/constants/responseMessageType';
import { mainProductPagingObject } from '../../../../common/utils/paginationUtils';
import { handlePageChange } from '../../../../common/utils/paginationUtils';
import { buildQueryString } from '../../../../common/utils/queryStringUtils';

import dayjs from 'dayjs';

import AdminSideNav from '../../components/AdminSideNav';
import DefaultButton from '../../../../common/components/DefaultButton';
import AdminOrderListForm from '../../components/AdminOrderListForm';
import AdminOrderModalDetail from '../../components/modal/AdminOrderModalDetail';

/*
	미처리 주문 목록.

	서버에서 해당 날짜 16시 이전 주문건에 대한 데이터 중 미처리 항목만 가져와 출력한다.
	Recipient와 createdAt만 테이블 구조로 출력하며 클릭시 modal 창을 출력한다.

	modal에서는 주문 상세정보와 주문자 정보를 보여주며
	하단에 주문 확인 버튼을 통해 배송 준비 단계로 넘어갈 수 있다.

	검색 타입은 사용자 아이디 및 받는 사람.
*/
function AdminNewOrderList() {
	const [ params ] = useSearchParams();
	const { page, keyword, searchType = 'recipient' } = Object.fromEntries([...params]);

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

	const navigate = useNavigate();

	const getOrderList = async () => {
		try {
			const res = await getNewOrderList(page, keyword, searchType);

			setData(res.data.content);
			const pagingObject = mainProductPagingObject(page, res.data.totalPages);

			setPagingData({
				startPage: pagingObject.startPage,
				endPage: pagingObject.endPage,
				prev: pagingObject.prev,
				next: pagingObject.next,
				totalElements: res.data.totalElements,
				activeNo: pagingObject.activeNo,
			});
		} catch(err) {
			console.log(err);
		}
	}

	useEffect(() => {
        window.scrollTo(0, 0);
		setKeywordSelectValue(searchType);
		getOrderList();
	}, [page, keyword]);

	//리스트 Element 클릭 이벤트
    //주문 정보 modal Open
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

    //주문 정보 modal close
    const closeModal = (e) => {
        if(modalIsOpen && modalRef.current && !modalRef.current.contains(e.target)){
            setModalIsOpen(false);

            document.body.style.cssText = '';
        }
    }

	// 주문 정보 Model 내부 상품 준비 버튼 이벤트
	const handlePreparationBtn = async (e) => {
		e.preventDefault();

		try {
			const res = await patchOrderStatus(e.target.value);

			if(res.data.message === RESPONSE_MESSAGE.OK) {
				setModalIsOpen(false);

				document.body.style.cssText = '';

				getOrderList();
			}
		} catch(err) {
			console.log(err);
		}
	}

	const handlePageBtn = (type) => {
		handlePageChange({
			typeOrNumber: type,
			pagingData,
			navigate,
			searchType: keywordSelectValue,
			keyword: keywordInput,
		});
	}

	const handleSearchBtn = () => {
		if(keywordInput !== '') {
			const queryString = buildQueryString({
				type: keywordSelectValue,
				keyword: keywordInput,
			});
	
			navigate(`${queryString}`);	
		}else
			alert('검색어를 입력해주세요.');		
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
				keywordSelectValue={keywordSelectValue}
				handleSelectOnChange={handleSelectOnChange}
				handleKeywordOnChange={handleKeywordOnChange}
				keywordInput={keywordInput}
				pagingData={pagingData}
				handlePageBtn={handlePageBtn}
				handleSearchBtn={handleSearchBtn}
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
                            <DefaultButton
                                btnText={'상품 준비'}
                                onClick={handlePreparationBtn}
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

export default AdminNewOrderList;