import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { useSelector } from 'react-redux';

import { getOrderList } from '../services/mainService';
import { mainProductPagingObject } from '../../../common/utils/paginationUtils';
import { handlePageChange, getClickPageNumber } from '../../../common/utils/paginationUtils';

import OrderListForm from '../../../common/components/OrderListForm';

import '../../../styles/mypage.css';

/*
    비회원의 주문 목록 페이지
    회원이 접근하는 경우 mypage의 주문 목록 페이지로 강제 이동
 */
function AnonymousOrderList() {
	const loginStatus = useSelector(state => state.member.loginStatus);
	const [params] = useSearchParams();
	const { page = 1, term = 3 } = Object.fromEntries(params);

	const location = useLocation();
	const state = location.state;

	const [orderData, setOrderData] = useState([]);
	const [pagingData, setPagingData] = useState({
        startPage: 0,
        endPage: 0,
        prev: false,
        next: false,
        activeNo: page,
    });

    const userType = 'none';
    const navigate = useNavigate();

	useEffect(() => {
		window.scrollTo(0, 0);

		const getOrderData = async() => {
			try{
				const res = await getOrderList(page, term, state.recipient, state.phone);

				setOrderData(res.data.content);
				const pagingData = mainProductPagingObject(page, res.data.totalPages);
				setPagingData({
					startPage: pagingData.startPage,
					endPage: pagingData.endPage,
					prev: pagingData.prev,
					next: pagingData.next,
					activeNo: pagingData.activeNo,
				});

				setOrderData(res.data.content);
			}catch(err) {
				console.log(err);
			}
		}
		
		if(loginStatus) 
			navigate('/my-page/order');
		else
			getOrderData();
	}, [term, page]);

	// 기간 select box 이벤트
	const handleSelectOnChange = (e) => {
		const { value } = e.target;
		navigate(
			`/order/detail?term=${value}`,
			{
				state: {
					recipient: state.recipient,
					phone: state.phone,
				}
			}
		);
	}

	// 페이지 버튼 이벤트
	const handlePageBtn = (type) => {
		const onClickPage = getClickPageNumber(type, pagingData);
		navigate(
			`/order/detail?term=${term}&page=${onClickPage}`,
			{
				state: { recipient: state.recipient, phone: state.phone}
			}
		);
	}

	return (
		<div className="non-member-order">
            <OrderListForm
                className={'non-member-order-content'}
                orderData={orderData}
                pagingData={pagingData}
                term={term}
                userType={userType}
                handleSelectOnChange={handleSelectOnChange}
                handlePageBtn={handlePageBtn}
            />
        </div>
	)
}

export default AnonymousOrderList;